package com.moa.moapay.domain.card.model.dto.getMyCard;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetMyCardDto {
    private UUID uuid;
    private String cardNumber;
    private String cvc;
    private boolean performanceFlag;
    private long cardLimit;
    private long amount;
    private long benefitUsage;

    private CardProductDto cardProduct;
    private AccountDto accounts;
}
