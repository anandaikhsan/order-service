package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmedEvent {
    private UUID orderId;
    private String orderNumber;
    private UUID seatId;
    private String paymentReference;
    private Double amount;
    private String paymentMethod;
}