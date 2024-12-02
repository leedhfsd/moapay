package com.moa.cardbank.domain.account.service;

import com.moa.cardbank.domain.account.entity.Account;
import com.moa.cardbank.domain.account.entity.AccountLog;
import com.moa.cardbank.domain.account.model.AccountLogType;
import com.moa.cardbank.domain.account.model.dto.*;
import com.moa.cardbank.domain.account.repository.AccountLogRepository;
import com.moa.cardbank.domain.account.repository.AccountRepository;
import com.moa.cardbank.domain.member.entity.Member;
import com.moa.cardbank.domain.member.repository.MemberRepository;
import com.moa.cardbank.global.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final AccountLogRepository accountLogRepository;

    @Override
    public CreateAccountResponseDto createAccount(CreateAccountRequestDto dto) {
        Member member = memberRepository.findByUuid(dto.getMemberId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "요청 데이터가 잘못되었습니다."));
        // 계좌번호는 생성일시 + 난수 4자리로 생성
        Date now = new Date();
        String date = new SimpleDateFormat("yyyymmddhhmmss").format(now);
        String randomNumber = String.valueOf((int)(Math.random()*10000));
        log.info("new account number : {}", date+randomNumber);
        Account account = Account.builder()
                .memberId(member.getId())
                .number(date+randomNumber)
                .balance(0L)
                .build();
        accountRepository.save(account);
        return CreateAccountResponseDto.builder()
                .accountId(account.getUuid())
                .accountNumber(account.getNumber())
                .build();
    }

    @Override
    @Transactional
    public DepositAccountResponseDto depositAccount(DepositAccountRequestDto dto) {
        Account account = accountRepository.findByUuid(dto.getAccountId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
        // 본인 계좌가 아닌 경우, 입금 금액이 0 이하인 경우 입금 불가
        if(!account.getMember().getUuid().equals(dto.getMemberId()) || dto.getValue() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
        }
        long newBalance = account.getBalance() + dto.getValue();
        Account newAccount = account.toBuilder()
                .balance(newBalance)
                .build();
        accountRepository.save(newAccount);
        AccountLog accountLog = AccountLog.builder()
                .accountId(newAccount.getId())
                .type(AccountLogType.DEPOSIT)
                .value(dto.getValue())
                .memo(dto.getMemo())
                .build();
        accountLogRepository.save(accountLog);
        return DepositAccountResponseDto.builder()
                .logId(accountLog.getUuid())
                .accountId(account.getUuid())
                .accountBalance(newBalance)
                .build();

    }

    @Override
    @Transactional
    public WithdrawAccountResponseDto withdrawAccount(WithdrawAccountRequestDto dto) {
        // 출금 처리를 위해, 현재 account 잔액을 조회
        Account account = accountRepository.findByUuid(dto.getAccountId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
        // 본인 계좌가 아닌 경우, 출금 금액이 0 이하인 경우 출금 불가
        if(!account.getMember().getUuid().equals(dto.getMemberId()) || dto.getValue() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
        }
        if(account.getBalance() < dto.getValue()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "잔액이 부족합니다.");
        }
        // 잔액이 부족하지 않는다면, 출금 처리 후 계좌거래내역을 갱신한다
        long newBalance = account.getBalance() - dto.getValue();
        Account newAccount = account.toBuilder()
                .balance(newBalance)
                .build();
        accountRepository.save(newAccount);
        AccountLog accountLog = AccountLog.builder()
                .accountId(newAccount.getId())
                .type(AccountLogType.WITHDRAWAL)
                .value(dto.getValue())
                .memo(dto.getMemo())
                .build();
        accountLogRepository.save(accountLog);
        return WithdrawAccountResponseDto.builder()
                .logId(accountLog.getUuid())
                .accountId(account.getUuid())
                .accountBalance(newBalance)
                .build();
    }

    @Override
    public boolean WithdrawByDebitCard(WithdrawByDebitCardDto dto) {
        // 출금 처리를 위해, 현재 account 잔액을 조회
        Account account = accountRepository.findByUuid(dto.getAccountId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
        if(account.getBalance() < dto.getValue()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "잔액이 부족합니다.");
        }
        if(dto.getValue() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
        }
        long newBalance = account.getBalance() - dto.getValue();
        Account newAccount = account.toBuilder()
                .balance(newBalance)
                .build();
        accountRepository.save(newAccount);
        AccountLog accountLog = AccountLog.builder()
                .accountId(newAccount.getId())
                .type(AccountLogType.WITHDRAWAL)
                .value(dto.getValue())
                .memo(dto.getMemo())
                .build();
        accountLogRepository.save(accountLog);
        return true;
    }

    @Override
    public boolean RefundByCard(RefundByCardDto dto) {
        // 입금 처리를 위한 정보 불러오기
        Account account = accountRepository.findByUuid(dto.getAccountId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
        if(dto.getValue() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
        }
        long newBalance = account.getBalance() + dto.getValue();
        Account newAccount = account.toBuilder()
                .balance(newBalance)
                .build();
        accountRepository.save(newAccount);
        AccountLog accountLog = AccountLog.builder()
                .accountId(newAccount.getId())
                .type(AccountLogType.DEPOSIT)
                .value(dto.getValue())
                .memo(dto.getMemo())
                .build();
        accountLogRepository.save(accountLog);
        return true;
    }

    @Override
    public boolean RefundEarning(RefundEarningDto dto) {
        Account account = accountRepository.findByUuid(dto.getAccountId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
        // 포인트, 캐시백 환불은 마이너스 통장을 만들 수 있음...
        long newBalance = account.getBalance() - dto.getValue();
        Account newAccount = account.toBuilder()
                .balance(newBalance)
                .build();
        accountRepository.save(newAccount);
        AccountLog accountLog = AccountLog.builder()
                .accountId(newAccount.getId())
                .type(AccountLogType.WITHDRAWAL)
                .value(dto.getValue())
                .memo(dto.getMemo())
                .build();
        accountLogRepository.save(accountLog);
        return true;
    }
}
