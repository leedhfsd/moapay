package com.moa.payment.domain.statistics.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MonthlyPaymentStatisticsDto {
	private String categoryId;
	private long money;
	private double per;

	public MonthlyPaymentStatisticsDto(String categoryId, long money, double per) {
		this.categoryId = categoryId;
		this.money = money;
		this.per = Double.parseDouble(String.format("%.2f", per));
	}
}
