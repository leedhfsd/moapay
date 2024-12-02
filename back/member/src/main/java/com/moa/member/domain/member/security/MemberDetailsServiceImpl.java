package com.moa.member.domain.member.security;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.moa.member.domain.member.model.Member;
import com.moa.member.domain.member.repository.MemberRepository;
import com.moa.member.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class MemberDetailsServiceImpl implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
		Member member = memberRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new BusinessException(
			HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));
		return new MemberPrincipalDetails(member);
	}
}
