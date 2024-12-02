package com.moa.payment.domain.charge.model.dto;

import com.moa.payment.domain.charge.model.vo.BenefitDetailVO;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentResultCardInfoDto {
    private UUID paymentId;
    private String cardName;
    private String imageUrl;
    private UUID cardId;
    private String cardNumber;
    private long amount; // 결제 요청시 넣었던 금액
    private long actualAmount; // 실제 결제된 금액
    private long performance; // 채워야하는 실적
    private long usedAmount; // 지금까지 채운 실적
    private boolean benefitActivated;
    private long benefitUsage; // 이번 결제비용 포함하여 얼마만큼 혜택을 받았는가
    private BenefitDetailVO benefitDetail;
}
