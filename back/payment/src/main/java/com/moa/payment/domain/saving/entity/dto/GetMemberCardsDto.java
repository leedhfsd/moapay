package com.moa.payment.domain.saving.entity.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetMemberCardsDto {

	private UUID memberId;
	private UUID cardId;
	private Long amount;
	private String cardNumber;

}
