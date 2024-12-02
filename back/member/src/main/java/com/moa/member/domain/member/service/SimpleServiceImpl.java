package com.moa.member.domain.member.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moa.member.domain.member.model.Member;
import com.moa.member.domain.member.repository.MemberRepository;
import com.moa.member.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SimpleServiceImpl implements SimpleService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void register(String uuid, String simplePassword) {
		Member member = memberRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new BusinessException(
			HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));

		String simple = passwordEncoder.encode(simplePassword);

		member.updateSimplePassword(simple);

		memberRepository.save(member);

	}

	@Override
	public void verify(String uuid, String simplePassword){
		Member member = memberRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new BusinessException(
			HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));
		// 비밀번호 검증
		if (!passwordEncoder.matches(simplePassword, member.getSimplePassword())) {
			throw new BusinessException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
		}
	}

}
