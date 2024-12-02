package com.moa.cardbank.domain.card.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CancelPayRequestDto {
    private UUID paymentId;
    private UUID cardId;
    private String cardNumber;
    private String cvc;
}
