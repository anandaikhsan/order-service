package dto;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class SeatLockedDeserializer extends JsonbDeserializer<SeatLockedEvent> {
    public SeatLockedDeserializer() {
        super(SeatLockedEvent.class);
    }
}