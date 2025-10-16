package service;

import dto.OrderRequest;
import dto.OrderResponse;
import dto.SeatLockedEvent;
import entity.OrderEntity;
import entity.SeatEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class OrderService {

    @Inject
    EntityManager entityManager;

    @Inject
    RedisService redisService;

    @Inject
    KafkaService kafkaService;

    /**
     * Create a new order for a seat
     * @param request The order request
     * @return The created order
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Find the seat
        SeatEntity seat = entityManager.find(SeatEntity.class, request.getSeatId());
        if (seat == null) {
            throw new IllegalArgumentException("Seat not found");
        }

        // Check if seat is available
        if (seat.getStatus() != SeatEntity.SeatStatus.AVAILABLE) {
            throw new IllegalArgumentException("Seat is not available");
        }

        // Check if seat is already locked in Redis
        if (redisService.isSeatLocked(seat.getId())) {
            throw new IllegalArgumentException("Seat is already locked");
        }

        // Generate order number
        String orderNumber = generateOrderNumber();

        // Create order entity with PENDING status
        OrderEntity order = OrderEntity.builder()
                .orderNumber(orderNumber)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .status(OrderEntity.OrderStatus.PENDING)
                .totalAmount(seat.getPrice())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(OrderEntity.PaymentStatus.PENDING)
                .seat(seat)
                .build();

        // Save order to database
        entityManager.persist(order);
        entityManager.flush(); // Ensure ID is generated

        // Lock seat in Redis with 2-minute TTL
        boolean locked = redisService.lockSeat(seat.getId(), order.getId());
        if (!locked) {
            throw new RuntimeException("Failed to lock seat");
        }

        // Update seat status to RESERVED
        seat.setStatus(SeatEntity.SeatStatus.RESERVED);
        entityManager.merge(seat);

        // Publish seat-locked event to Kafka
        SeatLockedEvent event = SeatLockedEvent.builder()
                .orderId(order.getId())
                .seatId(seat.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .amount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .build();
        kafkaService.publishSeatLockedEvent(event);

        // Return order response
        return OrderResponse.fromEntity(order);
    }

    /**
     * Update order status
     * @param orderId The order ID
     * @param status The new status
     * @return The updated order
     */
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderEntity.OrderStatus status) {
        OrderEntity order = entityManager.find(OrderEntity.class, orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        order.setStatus(status);
        entityManager.merge(order);

        return OrderResponse.fromEntity(order);
    }

    /**
     * Update payment status
     * @param orderId The order ID
     * @param status The new payment status
     * @return The updated order
     */
    @Transactional
    public OrderResponse updatePaymentStatus(UUID orderId, OrderEntity.PaymentStatus status) {
        OrderEntity order = entityManager.find(OrderEntity.class, orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        order.setPaymentStatus(status);
        entityManager.merge(order);

        return OrderResponse.fromEntity(order);
    }

    /**
     * Generate a unique order number
     * @return A unique order number
     */
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
