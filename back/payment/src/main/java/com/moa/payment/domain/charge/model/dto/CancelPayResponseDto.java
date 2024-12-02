package com.moa.payment.domain.charge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPayResponseDto {
    private long amount;
    private long benefitBalance;
    private long remainedBenefit;
}
