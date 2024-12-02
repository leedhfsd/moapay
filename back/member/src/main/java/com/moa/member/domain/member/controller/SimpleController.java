package com.moa.member.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.moa.member.domain.member.model.dto.SimpleRequestDto;
import com.moa.member.domain.member.service.SimpleService;
import com.moa.member.global.response.ResultResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/moapay/member/simple")
public class SimpleController {

	private final SimpleService simpleService;

	@PostMapping("/register")
	public ResponseEntity<ResultResponse> join(@RequestBody SimpleRequestDto dto) throws Exception {
		simpleService.register(dto.getUuid(), dto.getSimplePassword());
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "간단 비밀번호 등록을 완료했습니다.");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	@PostMapping("/verify")
	public ResponseEntity<ResultResponse> verify(@RequestBody SimpleRequestDto dto) throws Exception {
		simpleService.verify(dto.getUuid(),dto.getSimplePassword());
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "인증 완료");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

}
