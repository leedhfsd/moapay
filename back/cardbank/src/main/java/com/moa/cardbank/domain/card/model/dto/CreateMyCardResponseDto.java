package com.moa.cardbank.domain.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMyCardResponseDto {
    private UUID myCardId;
    private String myCardNumber;
    private String cvc;
}
