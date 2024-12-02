package com.moa.cardbank.domain.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawAccountRequestDto {
    private UUID memberId;
    private UUID accountId;
    private long value;
    private String memo;
}
