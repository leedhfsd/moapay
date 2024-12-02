package com.moa.member.domain.member.service;

import java.io.IOException;
import java.util.UUID;

import com.moa.member.domain.member.model.dto.JoinRequestDto;
import com.moa.member.domain.member.model.dto.JoinResponseDto;
import com.moa.member.domain.member.model.dto.LoginRequestDto;
import com.moa.member.domain.member.model.dto.getMemberResponseDto;
import com.moa.member.domain.member.model.dto.isMemberResponseDto;
import com.moa.member.domain.member.security.TokenDto;

public interface MemberService {
	JoinResponseDto join(JoinRequestDto joinRequestDto) throws IOException;

	TokenDto login(LoginRequestDto dto);

	isMemberResponseDto isMember(String phoneNumber);

	void selectType(String uuid, String type);

	getMemberResponseDto getMember(UUID memberId);
}
