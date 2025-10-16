package dto ;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class OrderRequestDeserializer extends JsonbDeserializer<OrderRequest> {
    public OrderRequestDeserializer() {
        super(OrderRequest.class);
    }
}