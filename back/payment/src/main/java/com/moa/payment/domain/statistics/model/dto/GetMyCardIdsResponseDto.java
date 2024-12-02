package com.moa.payment.domain.statistics.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyCardIdsResponseDto {
	private UUID memberId;
	private List<UUID> myCardIds;
}
