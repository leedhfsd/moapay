package com.moa.cardbank.domain.card.model.dto;

import com.moa.cardbank.domain.card.model.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardProductResponseDto {
    private UUID cardId;
}
