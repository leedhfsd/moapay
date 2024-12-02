package com.moa.member.domain.member.model.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginRequestDto {
	private String uuid;
	private String phoneNumber;
	private String simplePassword;
}
