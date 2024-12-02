package com.moa.cardbank.domain.card.model.dto.getMyCard;

import com.moa.cardbank.domain.card.model.CardType;
import lombok.*;

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
    private CardType cardProductType;
    private long cardProductAnnualFee;
    private long cardProductAnnualFeeForeign;
    private long cardProductPerformance;
    private String cardProductImgUrl;

    // 혜택 할 까 말 까?
}
