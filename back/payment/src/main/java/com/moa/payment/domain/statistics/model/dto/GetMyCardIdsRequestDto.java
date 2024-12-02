package com.moa.payment.domain.statistics.model.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class GetMyCardIdsRequestDto {
	private UUID memberId;
}