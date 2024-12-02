package com.moa.cardbank.domain.store.service;

import com.moa.cardbank.domain.account.entity.Account;
import com.moa.cardbank.domain.account.repository.AccountRepository;
import com.moa.cardbank.domain.store.entity.Merchant;
import com.moa.cardbank.domain.store.model.dto.CreateStoreRequestDto;
import com.moa.cardbank.domain.store.model.dto.CreateStoreResponseDto;
import com.moa.cardbank.domain.store.repository.MerchantRepository;
import com.moa.cardbank.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final MerchantRepository merchantRepository;
    private final AccountRepository accountRepository;

    @Override
    public CreateStoreResponseDto createStore(CreateStoreRequestDto dto) {
        Account account = accountRepository.findByNumber(dto.getAccountNumber())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
        Merchant merchant = Merchant.builder()
                .name(dto.getName())
                .accountId(account.getId())
                .categoryId(dto.getCategoryId())
                .build();
        merchantRepository.save(merchant);
        return CreateStoreResponseDto.builder()
                .merchantId(merchant.getUuid())
                .build();
    }
}
