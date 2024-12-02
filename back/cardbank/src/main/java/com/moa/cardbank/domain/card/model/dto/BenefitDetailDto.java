package com.moa.cardbank.domain.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenefitDetailDto {
    private long discount;
    private long point;
    private long cashback;
}
