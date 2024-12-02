package com.moa.moapay.domain.card.model.vo;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentResultCardInfoVO {
    private UUID cardId;
    private long amount;
    private boolean benefitActivated;
    private long benefitBalance;
    private long remainedBenefit;
    private BenefitDetailVO benefitDetail;
}
