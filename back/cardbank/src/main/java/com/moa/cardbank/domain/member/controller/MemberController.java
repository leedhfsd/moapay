package com.moa.cardbank.domain.member.controller;

import com.moa.cardbank.domain.member.model.dto.JoinMemberRequestDto;
import com.moa.cardbank.domain.member.model.dto.JoinMemberResponseDto;
import com.moa.cardbank.domain.member.service.MemberService;
import com.moa.cardbank.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<ResultResponse> joinMember(@RequestBody JoinMemberRequestDto dto){
        log.info("member join : {}", dto.getName());
        JoinMemberResponseDto responseDto = memberService.joinMember(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "회원가입을 완료했습니다.", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
}
