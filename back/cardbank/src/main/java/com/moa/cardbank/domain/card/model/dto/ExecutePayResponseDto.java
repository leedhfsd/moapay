package com.moa.cardbank.domain.card.model.dto;

import com.moa.cardbank.domain.card.model.PayStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutePayResponseDto {
    private String merchantName;
    private String categoryId;
    private PayStatus status;
    private UUID paymentId;
    private long amount;
    private boolean benefitActivated;
    private long benefitBalance;
    private long remainedBenefit;
    private BenefitDetailDto benefitDetail;
}
