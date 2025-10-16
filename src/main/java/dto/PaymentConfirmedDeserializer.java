package dto ;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class PaymentConfirmedDeserializer extends JsonbDeserializer<PaymentConfirmedEvent> {
    public PaymentConfirmedDeserializer() {
        super(PaymentConfirmedEvent.class);
    }
}