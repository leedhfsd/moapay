package com.moa.moapay.domain.card.model.dto.getMyCard;

import com.moa.moapay.domain.card.model.dto.CardBenefitDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    private List<CardBenefitDto> cardBenefitDtoList;
}
