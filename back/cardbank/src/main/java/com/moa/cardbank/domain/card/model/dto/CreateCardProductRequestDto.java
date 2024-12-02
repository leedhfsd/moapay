package com.moa.cardbank.domain.card.model.dto;

import com.moa.cardbank.domain.card.model.CardType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateCardProductRequestDto {
    private String name;
    private String companyName;
    private long benefitTotalLimit;
    private CardType type; // DEBIT, CREDIT
    private Long annualFee;
    private Long annualFeeForeign;
    private Long performance;
    private String imageUrl;
    private List<CardBenefitDto> benefitList;
}
