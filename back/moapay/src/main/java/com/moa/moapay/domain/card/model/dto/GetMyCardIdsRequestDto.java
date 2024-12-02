package com.moa.moapay.domain.card.model.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class GetMyCardIdsRequestDto {
	private UUID memberId;
}
