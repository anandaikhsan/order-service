package service;

import dto.PaymentConfirmedEvent;
import dto.SeatLockedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaService {

    @Inject
    @Channel("seat-locked")
    Emitter<SeatLockedEvent> seatLockedEmitter;

    @Inject
    @Channel("payment-confirmed")
    Emitter<PaymentConfirmedEvent> paymentConfirmedEmitter;

    /**
     * Publish a seat-locked event to Kafka
     * @param event The seat-locked event to publish
     */
    public void publishSeatLockedEvent(SeatLockedEvent event) {
        seatLockedEmitter.send(event);
    }

    /**
     * Publish a payment-confirmed event to Kafka
     * @param event The payment-confirmed event to publish
     */
    public void publishPaymentConfirmedEvent(PaymentConfirmedEvent event) {
        paymentConfirmedEmitter.send(event);
    }
}