package com.moa.payment.domain.analysis.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardHistoryPaymentLogDto {
    private String merchantName;
    private long amount;
    private long benefitBalance;
    private String categoryId;
    private LocalDateTime createTime;
}
