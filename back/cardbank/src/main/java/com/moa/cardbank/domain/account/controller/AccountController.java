package com.moa.cardbank.domain.account.controller;

import com.moa.cardbank.domain.account.model.dto.*;
import com.moa.cardbank.domain.account.service.AccountService;
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
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<ResultResponse> createAccount(@RequestBody CreateAccountRequestDto dto) {
        CreateAccountResponseDto responseDto =  accountService.createAccount(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "계좌를 생성했습니다.", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/deposit")
    public ResponseEntity<ResultResponse> depositAccount(@RequestBody DepositAccountRequestDto dto) {
        log.info("deposit account from {} | value : {}", dto.getAccountId(), dto.getValue());
        DepositAccountResponseDto responseDto =  accountService.depositAccount(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "입금을 완료했습니다.", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ResultResponse> withdrawAccount(@RequestBody WithdrawAccountRequestDto dto)  {
        log.info("withdraw account from {} | value : {}", dto.getAccountId(), dto.getValue());
        WithdrawAccountResponseDto responseDto =  accountService.withdrawAccount(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "출금을 완료했습니다.", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
}
