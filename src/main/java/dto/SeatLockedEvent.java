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
public class SeatLockedEvent {
    private UUID orderId;
    private UUID seatId;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private Double amount;
    private String paymentMethod;
}