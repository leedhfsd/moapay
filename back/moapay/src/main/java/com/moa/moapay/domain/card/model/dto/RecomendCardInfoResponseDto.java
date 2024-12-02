package com.moa.moapay.domain.card.model.dto;

import com.moa.moapay.domain.card.model.CardType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecomendCardInfoResponseDto {
    private UUID cardProductUuid;
    private String cardProductName;
    private String cardProductCompanyName;
    private long cardProductBenefitTotalLimit;
    private CardType cardProductType;
    private long cardProductAnnualFee;
    private long cardProductAnnualFeeForeign;
    private long cardProductPerformance;
    private String cardProductImgUrl;
    private List<CardBenefitDto> cardBenefits;
}
