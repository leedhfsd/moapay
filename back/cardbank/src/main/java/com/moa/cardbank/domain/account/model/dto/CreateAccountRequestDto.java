package com.moa.cardbank.domain.account.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateAccountRequestDto {
    private UUID memberId;
}
