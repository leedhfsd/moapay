package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardRestTemplateDto {
    private UUID uuid;
    private String cardNumber;
    private String cvc;
    private boolean performanceFlag;
    private long benefitUsage;
    private long amount;
    private long cardLimit;

    private UUID cardProductUuid;
    private String cardProductName;
    private String cardProductCompanyName;
    private long cardProductBenefitTotalLimit;
    private String cardProductType;
    private long cardProductAnnualFee;
    private long cardProductAnnualFeeForeign;
    private long cardProductPerformance;
    private String cardProductImgUrl;

    private UUID accountUuid;
    private String accountNumber;
    private long balance;
}
