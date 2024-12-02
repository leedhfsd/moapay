package com.moa.payment.domain.statistics.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyConsumptionResponseDto {
	private UUID memberId;
	private long totalAmounts;
	private int year;
	private int month;
	private List<MonthlyPaymentStatisticsDto> paymentStatistics;
}
