package com.moa.payment.domain.analysis.model.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class getMemberResponseDto {
	private UUID memberId;
	private String phoneNumber;
	private LocalDate birthDate;
	private String gender;
}
