package com.moa.moapay.domain.card.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetMyCardIdsResponseDto {
	private UUID memberId;
	private List<UUID> myCardIds;
}
