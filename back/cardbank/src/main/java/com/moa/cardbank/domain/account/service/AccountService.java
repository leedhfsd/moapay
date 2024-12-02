package com.moa.cardbank.domain.account.service;

import com.moa.cardbank.domain.account.model.dto.*;

public interface AccountService {
    CreateAccountResponseDto createAccount(CreateAccountRequestDto dto);
    DepositAccountResponseDto depositAccount(DepositAccountRequestDto dto);
    WithdrawAccountResponseDto withdrawAccount(WithdrawAccountRequestDto dto);
    boolean WithdrawByDebitCard(WithdrawByDebitCardDto dto);
    boolean RefundByCard(RefundByCardDto dto);
    boolean RefundEarning(RefundEarningDto dto);
}
