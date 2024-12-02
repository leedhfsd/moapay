package com.moa.cardbank.domain.card.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateMyCardRequestDto {
    private UUID memberId;
    private UUID cardProductId;
    private UUID accountId;
    private long cardLimit;
}
