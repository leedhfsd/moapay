package com.moa.member.domain.member.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequestDto {
	String phoneNumber;
	String code;
}
