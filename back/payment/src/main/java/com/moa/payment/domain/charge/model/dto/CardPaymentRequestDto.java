package com.moa.payment.domain.charge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentRequestDto {
    private UUID merchantId;
    private UUID cardId;
    private String cardNumber;
    private String cvc;
    private long amount;
}
