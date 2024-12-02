package com.moa.payment.domain.analysis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CardHistoryRequestDto {
    private UUID cardId;
    private int year;
    private int month;
}
