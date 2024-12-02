package com.moa.moapay.domain.card.model.dto;

import com.moa.moapay.domain.card.model.CardType;
import lombok.*;

import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoResponseDto {
    private String cardName;
    private String companyName;
    private Long benefitTotalLimit;
    private CardType cardType;
    private Long annualFee;
    private Long annualFeeForeign;
    private Long performance;
    private String imageUrl;
    private List<CardBenefitDto> benefits;

}
