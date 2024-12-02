package com.moa.cardbank.domain.card.model.dto.getMyCard;

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
