package com.moa.moapay.domain.card.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CardHistoryRequestDto {
    private UUID cardId;
    private int year;
    private int month;
}
