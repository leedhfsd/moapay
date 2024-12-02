package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardProductDto {
    private UUID cardProductUuid;
    private String cardProductName;
    private String cardProductCompanyName;
    private long cardProductBenefitTotalLimit;
    private String cardProductType;
    private long cardProductAnnualFee;
    private long cardProductAnnualFeeForeign;
    private long cardProductPerformance;
    private String cardProductImgUrl;

    List<CardBenefitDto> cardBenefits;
}
