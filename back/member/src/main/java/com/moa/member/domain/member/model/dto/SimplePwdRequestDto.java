package com.moa.member.domain.member.model.dto;

import com.moa.member.domain.member.model.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimplePwdRequestDto {
	private String uuid;
	private String simplePassword;
}
