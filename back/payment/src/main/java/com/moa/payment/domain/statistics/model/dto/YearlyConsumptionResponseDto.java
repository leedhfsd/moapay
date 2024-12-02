package com.moa.payment.domain.statistics.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YearlyConsumptionResponseDto {
	private UUID memberId;
	private List<MonthlyAmount> monthlyAmounts;
}
