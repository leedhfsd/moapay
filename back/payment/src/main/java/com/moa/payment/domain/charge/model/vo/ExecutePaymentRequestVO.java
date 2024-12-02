package com.moa.payment.domain.charge.model.vo;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExecutePaymentRequestVO {
    private UUID dutchPayId;
    private UUID requestId;
    private UUID orderId;
    private UUID merchantId;
    private List<PaymentCardInfoVO> paymentInfoList;
    private String paymentType;
    private String operation;
}
