package consumer;

import dto.PaymentConfirmedEvent;
import entity.OrderEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import service.OrderService;
import service.RedisService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class OrderConsumer {

    private static final Logger LOG = Logger.getLogger(OrderConsumer.class);

    @Inject
    OrderService orderService;

    @Inject
    RedisService redisService;

    /**
     * Listen to payment-confirmed events and update order status to PAID
     * @param event The payment-confirmed event
     * @return CompletionStage<Void>
     */
    @Incoming("payment-confirmed-in")
    public CompletionStage<Void> processPaymentConfirmed(PaymentConfirmedEvent event) {
        LOG.info("Received payment-confirmed event for order: " + event.getOrderNumber());

        try {
            // Update order status to PAID
            LOG.info("Updating order status to PAID for order: " + event.getOrderNumber());
            orderService.updateOrderStatus(event.getOrderId(), OrderEntity.OrderStatus.CONFIRMED);
            orderService.updatePaymentStatus(event.getOrderId(), OrderEntity.PaymentStatus.PAID);
            
            // Release seat lock in Redis (optional, as it will expire automatically after 2 minutes)
            redisService.releaseSeatLock(event.getSeatId());
            
            LOG.info("Order status updated to PAID for order: " + event.getOrderNumber());
        } catch (Exception e) {
            LOG.error("Error updating order status for order: " + event.getOrderNumber(), e);
        }

        return CompletableFuture.completedFuture(null);
    }
}