package com.moa.payment.domain.analysis.model.dto;

import com.moa.payment.domain.analysis.entity.Gender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class averageResponseDto {
	Long average;
	String gender;
}
