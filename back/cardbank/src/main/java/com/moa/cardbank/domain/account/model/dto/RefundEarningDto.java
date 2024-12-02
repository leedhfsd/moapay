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
public class RefundEarningDto {
    private UUID accountId;
    private long value;
    private String memo;
}
