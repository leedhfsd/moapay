package com.moa.moapay.domain.dutchpay.model.vo;

import com.moa.moapay.domain.generalpay.model.vo.PaymentCardInfoVO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DutchPayCompleteVo {
    private UUID orderId;
    private UUID merchantId;
    private List<PaymentCardInfoVO> paymentInfoList;
    private String paymentType;

    private String status;
    private UUID dutchUuid;
    private UUID requestId;
}
