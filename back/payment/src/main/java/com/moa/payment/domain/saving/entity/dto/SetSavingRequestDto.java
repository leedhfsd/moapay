package com.moa.payment.domain.saving.entity.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetSavingRequestDto {
	private UUID memberId;
}
