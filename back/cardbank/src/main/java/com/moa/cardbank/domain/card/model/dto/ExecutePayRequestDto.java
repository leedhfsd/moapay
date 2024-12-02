package com.moa.cardbank.domain.card.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class ExecutePayRequestDto {
    private UUID merchantId;
    private UUID cardId;
    private String cardNumber;
    private String cvc;
    private long amount;
}
