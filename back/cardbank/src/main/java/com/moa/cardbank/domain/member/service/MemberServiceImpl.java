package com.moa.cardbank.domain.member.service;

import com.moa.cardbank.domain.member.entity.Member;
import com.moa.cardbank.domain.member.model.dto.JoinMemberRequestDto;
import com.moa.cardbank.domain.member.model.dto.JoinMemberResponseDto;
import com.moa.cardbank.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public JoinMemberResponseDto joinMember(JoinMemberRequestDto dto) {
        Member member = Member.builder()
                .name(dto.getName())
                .build();
        memberRepository.save(member);
        return JoinMemberResponseDto.builder()
                .id(member.getUuid())
                .name(member.getName())
                .build();
    }
}
