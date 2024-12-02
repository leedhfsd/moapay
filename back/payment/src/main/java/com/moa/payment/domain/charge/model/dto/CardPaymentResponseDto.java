package com.moa.payment.domain.charge.model.dto;

import com.moa.payment.domain.charge.model.PayStatus;
import com.moa.payment.domain.charge.model.vo.BenefitDetailVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentResponseDto {
    private String merchantName;
    private String categoryId;
    private PayStatus status;
    private UUID paymentId;
    private long amount;
    private boolean benefitActivated;
    private long benefitBalance;
    private long remainedBenefit;
    private BenefitDetailVO benefitDetail;

    @Override
    public String toString() {
        return "CardPaymentResponseDto{" +
                "merchantName='" + merchantName + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", status=" + status +
                ", paymentId=" + paymentId +
                ", amount=" + amount +
                ", benefitActivated=" + benefitActivated +
                ", benefitBalance=" + benefitBalance +
                ", remainedBenefit=" + remainedBenefit +
                ", benefitDetail=" + benefitDetail +
                '}';
    }
}
