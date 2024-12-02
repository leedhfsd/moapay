package com.moa.payment.domain.charge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenefitDetailVO {
    private long discount;
    private long point;
    private long cashback;

    @Override
    public String toString() {
        return "[discount : " + discount + ", point : " + point + ", cashback : " + cashback+"]";
    }
}
