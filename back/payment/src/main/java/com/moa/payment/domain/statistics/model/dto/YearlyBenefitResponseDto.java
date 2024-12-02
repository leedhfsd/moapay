package com.moa.payment.domain.statistics.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YearlyBenefitResponseDto {
	private UUID memberId;
	private List<MonthlyBenefit> monthlyBenefits;
}
