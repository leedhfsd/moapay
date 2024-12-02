package com.moa.store.domain.paymentInfo.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentResultDto {
    private String cardNumber;
    private long amount;
    private long actualAmount;
}
