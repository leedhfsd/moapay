package com.moa.payment.domain.statistics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyBenefit {
	private int year;
	private int month;
	private long benefit;
}