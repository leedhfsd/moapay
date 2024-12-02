package com.moa.moapay.domain.card.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class BenefitDetailVO {
    private long discount;
    private long point;
    private long cashback;
}
