package com.moa.payment.domain.analysis.model.dto;

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
public class CardHistoryResponseDto {
    private UUID cardId;
    private int year;
    private int month;
    private long totalAmount; // 결제액 총 금액
    private long totalBenefit; // 혜택 총 금액
    List<CardHistoryPaymentLogDto> paymentLogs; // 결제 내역, 시간 역순 정렬
}
