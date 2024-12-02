package com.moa.member.domain.member.model.dto;

import com.moa.member.domain.member.security.TokenDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
	private TokenDto token;
	private String uuid;
}
