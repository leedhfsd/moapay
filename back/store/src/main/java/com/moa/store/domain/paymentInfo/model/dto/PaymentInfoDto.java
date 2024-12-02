package com.moa.store.domain.paymentInfo.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentInfoDto {
    private UUID paymentId;
    private String cardNumber;
    private long amount;
    private long actualAmount;
    private String status;
    private LocalDateTime paymentTime;
}
