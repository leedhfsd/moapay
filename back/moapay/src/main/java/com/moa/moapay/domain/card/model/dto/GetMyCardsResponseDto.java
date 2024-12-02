package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyCardsResponseDto {

    private UUID uuid;
    private String cardNumber;
    private String cvc;
    private boolean performanceFlag;
    private long cardLimit;
    private long amount;
    private long benefitUsage;

    private CardProductDto cardProduct;
    private AccountDto account;
}
