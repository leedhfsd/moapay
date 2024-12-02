package com.moa.cardbank.domain.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPayResponseDto {
    private long amount;
    private long benefitBalance;
    private long remainedBenefit;
}
