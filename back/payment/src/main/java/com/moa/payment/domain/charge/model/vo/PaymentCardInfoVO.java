package com.moa.payment.domain.charge.model.vo;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentCardInfoVO {
    private UUID cardId;
    private String cardName;
    private String imageUrl;
    private String cardNumber;
    private String cvc;
    private long amount;
    private long usedAmount; // 해당 카드에서 지금까지 얼마만큼 긁었는지
    private long performance;
    private long benefitUsage;
}
