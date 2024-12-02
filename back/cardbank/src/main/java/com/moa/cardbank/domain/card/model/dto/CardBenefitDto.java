package com.moa.cardbank.domain.card.model.dto;

import com.moa.cardbank.domain.card.model.BenefitType;
import com.moa.cardbank.domain.card.model.BenefitUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardBenefitDto {
    private String categoryId;
    private BenefitType benefitType;
    private BenefitUnit benefitUnit;
    private double benefitValue;
    private String benefitDesc;
}
