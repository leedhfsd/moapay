package com.moa.payment.domain.charge.model.vo;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DutchPayCompliteVo {
    private UUID orderId;
    private UUID merchantId;
    private List<PaymentCardInfoVO> paymentInfoList;
    private String paymentType;

    private String status;
    private UUID dutchUuid;
    private UUID requestId;
}
