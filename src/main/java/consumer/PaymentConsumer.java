package consumer;

import dto.PaymentConfirmedEvent;
import dto.SeatLockedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import service.KafkaService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class PaymentConsumer {

    private static final Logger LOG = Logger.getLogger(PaymentConsumer.class);

    @Inject
    KafkaService kafkaService;

    /**
     * Listen to seat-locked events, simulate payment success, and publish payment-confirmed events
     * @param event The seat-locked event
     * @return CompletionStage<Void>
     */
    @Incoming("seat-locked-in")
    public CompletionStage<Void> processSeatLocked(SeatLockedEvent event) {
        LOG.info("Received seat-locked event for order: " + event.getOrderNumber());

        try {
            // Simulate payment processing
            LOG.info("Processing payment for order: " + event.getOrderNumber());
            Thread.sleep(1000); // Simulate a delay

            // Generate payment reference
            String paymentReference = "PAY-" + UUID.randomUUID().toString().substring(0, 8);

            // Simulate payment success
            LOG.info("Payment successful for order: " + event.getOrderNumber() + " with reference: " + paymentReference);

            // Publish payment-confirmed event
            PaymentConfirmedEvent paymentEvent = PaymentConfirmedEvent.builder()
                    .orderId(event.getOrderId())
                    .orderNumber(event.getOrderNumber())
                    .seatId(event.getSeatId())
                    .paymentReference(paymentReference)
                    .amount(event.getAmount())
                    .paymentMethod(event.getPaymentMethod())
                    .build();

            kafkaService.publishPaymentConfirmedEvent(paymentEvent);
            LOG.info("Published payment-confirmed event for order: " + event.getOrderNumber());
        } catch (Exception e) {
            LOG.error("Error processing payment for order: " + event.getOrderNumber(), e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
