package com.moa.payment.domain.charge.model.vo;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentResultCardInfoVO {
    private UUID paymentId;
    private UUID cardId;
    private String cardNumber;
    private long amount; // 결제 요청시 넣었던 금액
    private long actualAmount; // 실제 결제된 금액
    private boolean benefitActivated;
    private long benefitBalance;
    private long remainedBenefit;
    private BenefitDetailVO benefitDetail;
}
