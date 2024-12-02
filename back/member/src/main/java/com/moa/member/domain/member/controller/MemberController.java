package com.moa.member.domain.member.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moa.member.domain.member.model.Member;
import com.moa.member.domain.member.model.dto.JoinRequestDto;
import com.moa.member.domain.member.model.dto.JoinResponseDto;
import com.moa.member.domain.member.model.dto.LoginRequestDto;
import com.moa.member.domain.member.model.dto.LoginResponseDto;
import com.moa.member.domain.member.model.dto.MessageRequestDto;
import com.moa.member.domain.member.model.dto.VerificationRequestDto;
import com.moa.member.domain.member.model.dto.getMemberResponseDto;
import com.moa.member.domain.member.model.dto.isMemberRequestDto;
import com.moa.member.domain.member.model.dto.isMemberResponseDto;
import com.moa.member.domain.member.model.dto.selectTypeRequestDto;
import com.moa.member.domain.member.repository.MemberRepository;
import com.moa.member.domain.member.security.JwtTokenProvider;
import com.moa.member.domain.member.security.TokenDto;
import com.moa.member.domain.member.service.MemberService;
import com.moa.member.domain.member.service.MessageService;
import com.moa.member.global.exception.BusinessException;
import com.moa.member.global.response.ResultResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/moapay/member")
public class MemberController {

	private final MemberService memberService;
	private final MessageService messageService;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;


	//회원가입
	@PostMapping("/join")
	public ResponseEntity<ResultResponse> join(@RequestBody JoinRequestDto dto) throws Exception {
		JoinResponseDto member = memberService.join(dto);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "회원가입을 완료했습니다.", member);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	//본인인증 문자 보내기
	@PostMapping("/sendSMS")
	public ResponseEntity<ResultResponse> sendSMS(@RequestBody MessageRequestDto dto) {
		// 성공코드 2000
		String statusCode = messageService.sendSMS(dto.getPhoneNumber()).getStatusCode();
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.BAD_REQUEST, "인증번호 전송에 실패했습니다.");
		if (statusCode.equals("2000")) {
			resultResponse = ResultResponse.of(HttpStatus.OK, "인증번호를 전송했습니다.");
		}
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);

	}

	//인증번호로 본인 인증
	@PostMapping("/verification")
	public ResponseEntity<ResultResponse> verification(@RequestBody VerificationRequestDto dto) {
		boolean pass = messageService.verifySMS(dto.getPhoneNumber(), dto.getCode());
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "인증번호가 일치합니다.");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);

	}

	@PostMapping("/login")
	public ResponseEntity<ResultResponse> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {

		try {
			TokenDto token = memberService.login(dto);
			Member member = memberRepository.findByPhoneNumber(dto.getPhoneNumber())
				.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));
			response.setHeader("Authorization", "Bearer " + token.getAccessToken());
			response.setHeader("RefreshToken", "Bearer " + token.getRefreshToken());

			LoginResponseDto loginResponse = new LoginResponseDto(token, member.getUuid().toString());
			ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "로그인 성공", loginResponse);
			return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
		} catch (BusinessException e) { //비즈니스 예외 처리
			return ResponseEntity.status(e.getStatus()).body(ResultResponse.of(e.getStatus(), e.getMessage(), null));
		} catch (Exception e) { //일반 예외 처리
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResultResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "로그인 중 오류가 발생했습니다.", null));
		}

	}

	// 멤버 존재 여부 확인
	@PostMapping("/isMember")
	public ResponseEntity<ResultResponse> isMember(@RequestBody isMemberRequestDto dto) {
		isMemberResponseDto response = memberService.isMember(dto.getPhoneNumber());
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "멤버 존재 여부 확인", response);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	@PostMapping("/selectType")
	public ResponseEntity<ResultResponse> selectType(@RequestBody selectTypeRequestDto dto) {
		memberService.selectType(dto.getUuid(), dto.getType());
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "결제타입 설정 완료");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// payment로 보내는 memberDto
	@PostMapping("/getMember")
	public ResponseEntity<getMemberResponseDto> getMember(@RequestBody UUID memberId){
		getMemberResponseDto dto=memberService.getMember(memberId);
		return ResponseEntity.ok(dto);
	}

}
