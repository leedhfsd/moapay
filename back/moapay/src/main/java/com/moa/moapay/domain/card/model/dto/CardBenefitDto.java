package com.moa.moapay.domain.card.model.dto;

import com.moa.moapay.domain.card.model.BenefitType;
import com.moa.moapay.domain.card.model.BenefitUnit;
import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardBenefitDto {

    private String categoryName;

    // enum
    private BenefitType benefitType;
    private BenefitUnit benefitUnit;

    private double benefitValue;
    private String benefitDesc;
    private int benefitPoint;

}
