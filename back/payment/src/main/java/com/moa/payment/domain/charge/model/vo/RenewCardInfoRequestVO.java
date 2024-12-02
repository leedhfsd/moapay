package com.moa.payment.domain.charge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewCardInfoRequestVO {
    private UUID cardId;
    private long amount;
    private boolean benefitActivated;
    private long benefitBalance;
    private long remainedBenefit;
}
