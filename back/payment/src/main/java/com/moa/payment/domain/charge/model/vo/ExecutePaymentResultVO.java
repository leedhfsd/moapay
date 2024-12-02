package com.moa.payment.domain.charge.model.vo;

import com.moa.payment.domain.charge.model.PaymentResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutePaymentResultVO {
    private String merchantName;
    private PaymentResultStatus status;
    private List<PaymentResultCardInfoVO> paymentResultInfoList;
}
