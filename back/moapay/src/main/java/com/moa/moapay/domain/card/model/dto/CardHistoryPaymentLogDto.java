package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardHistoryPaymentLogDto {
    private String merchantName;
    private long amount;
    private long benefitBalance;
    private String categoryId;
    private LocalDateTime createTime;
}
