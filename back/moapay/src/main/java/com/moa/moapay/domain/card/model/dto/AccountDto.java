package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID accountUuid;
    private String accountNumber;
    private long balance;
}
