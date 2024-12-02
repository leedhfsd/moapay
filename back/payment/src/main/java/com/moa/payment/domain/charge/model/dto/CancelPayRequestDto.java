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
public class CancelPayRequestDto {
    private UUID paymentId;
    private UUID cardId;
    private String cardNumber;
    private String cvc;
}
