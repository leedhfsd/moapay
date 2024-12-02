package com.moa.moapay.domain.card.model;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BenefitInfo {
    private double totalDiscount;
    private double totalPoint;
    private double totalCashback;
    private double totalBenefit;
}
