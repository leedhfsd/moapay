package com.moa.cardbank.domain.account.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class DepositAccountRequestDto {
    private UUID memberId;
    private UUID accountId;
    private long value;
    private String memo;
}
