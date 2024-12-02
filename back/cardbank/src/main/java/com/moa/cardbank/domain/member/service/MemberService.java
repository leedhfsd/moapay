package com.moa.cardbank.domain.member.service;

import com.moa.cardbank.domain.member.model.dto.JoinMemberRequestDto;
import com.moa.cardbank.domain.member.model.dto.JoinMemberResponseDto;

public interface MemberService {
    JoinMemberResponseDto joinMember(JoinMemberRequestDto dto);
}
