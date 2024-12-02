package com.moa.member.domain.member.service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moa.member.domain.member.model.dto.JoinResponseDto;
import com.moa.member.domain.member.model.dto.LoginRequestDto;
import com.moa.member.domain.member.model.dto.getMemberResponseDto;
import com.moa.member.domain.member.model.dto.isMemberResponseDto;
import com.moa.member.domain.member.security.JwtTokenProvider;
import com.moa.member.domain.member.security.MemberPrincipalDetails;
import com.moa.member.domain.member.security.TokenDto;
import com.moa.member.global.exception.BusinessException;
import com.moa.member.domain.member.model.Member;
import com.moa.member.domain.member.model.dto.JoinRequestDto;
import com.moa.member.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final DaoAuthenticationProvider memberAuthenticationProvider;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public JoinResponseDto join(JoinRequestDto joinRequestDto) {
		//핸드폰 번호는 겹칠 수 없다. 고유한 값.
		Optional<Member> memberOptional = memberRepository.findByPhoneNumber(joinRequestDto.getPhoneNumber());
		if (memberOptional.isPresent()) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다.");
		}

		String phone = joinRequestDto.getPhoneNumber();
		String password = passwordEncoder.encode(phone);

		Member member = joinRequestDto.toEntity(password);

		memberRepository.save(member);

		return JoinResponseDto.builder()
			.id(member.getUuid())
			.name(member.getName())
			.gender(member.getGender())
			.birthDate(member.getBirthDate())
			.phoneNumber(member.getPhoneNumber())
			.email(member.getEmail())
			.address(member.getAddress())
			.createTime(member.getCreateTime())
			.updateTime(member.getUpdateTime())
			.build();

	}

	@Override
	public TokenDto login(LoginRequestDto dto) {

		if (dto.getSimplePassword() != null) {

			// simplePassword로 로그인
			Member member = memberRepository.findByUuid(UUID.fromString(dto.getUuid()))
				.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));

			// 비밀번호 검증 (단순 비밀번호의 경우)
			if (!passwordEncoder.matches(dto.getSimplePassword(), member.getSimplePassword())) {
				throw new BusinessException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
			}

		}

		// 기존 UUID와 전화번호로 로그인
		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(dto.getUuid(), dto.getPhoneNumber());
		Authentication authentication = memberAuthenticationProvider.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String accessToken = jwtTokenProvider.generateAccessToken(
			SecurityContextHolder.getContext().getAuthentication());

		// 기존 리프레시 토큰 조회
		String refreshToken = jwtTokenProvider.getRefreshTokenByUuid(dto.getUuid());

		// 리프레시 토큰이 없으면 새로 생성
		if (refreshToken == null) {
			refreshToken = jwtTokenProvider.generateRefreshToken(
				SecurityContextHolder.getContext().getAuthentication());
		}

		return new TokenDto(accessToken, refreshToken);
	}

	@Override
	public isMemberResponseDto isMember(String phoneNumber) {

		isMemberResponseDto response = isMemberResponseDto.builder()
				.isExist(false)
				.build();

		Optional<Member> member=memberRepository.findByPhoneNumber(phoneNumber);
		if(member.isEmpty()){ //멤버가 존재하지 않으면
			return response;
		}
		response = isMemberResponseDto.builder()
				.isExist(true)
				.uuid(member.get().getUuid().toString())
				.phoneNumber(phoneNumber)
				.name(member.get().getName().toString())
				.build();
		return response;
	}

	@Override
	@Transactional
	public void selectType(String uuid, String type) {
		Member member = memberRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new BusinessException(
			HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));
		member.updatePaymentType(type);
		memberRepository.save(member);
	}

	@Override
	public getMemberResponseDto getMember(UUID memberId){
		Member member=memberRepository.findByUuid(memberId).orElseThrow(()->new BusinessException(HttpStatus.NOT_FOUND,"멤버가 존재하지 않습니다."));
		System.out.println("멤버 가져오기 성공");
		System.out.println(member.getPhoneNumber()+"/"+member.getGender()+"/"+member.getBirthDate());
		getMemberResponseDto dto= getMemberResponseDto.builder()
			.memberId(memberId)
			.phoneNumber(member.getPhoneNumber())
			.birthDate(member.getBirthDate())
			.gender(member.getGender())
			.build();
		return dto;
	}

}
