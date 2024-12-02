package com.moa.payment.domain.charge.model.dto;

import com.moa.payment.domain.charge.model.PaymentResultStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentResultDto {
    private PaymentResultStatus status;
    private UUID requestId;
    private String merchantName;
    private long totalAmount;
    private LocalDateTime createTime;
    private int usedCardCount;
    private List<PaymentResultCardInfoDto> paymentResultCardInfoList;
}
