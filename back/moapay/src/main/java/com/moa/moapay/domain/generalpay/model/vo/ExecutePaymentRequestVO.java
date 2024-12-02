package com.moa.moapay.domain.generalpay.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutePaymentRequestVO {
    private UUID dutchPayId;
    private UUID requestId;
    private UUID orderId;
    private UUID merchantId;
    private List<PaymentCardInfoVO> paymentInfoList;
    private String paymentType;
    private String operation;
}
