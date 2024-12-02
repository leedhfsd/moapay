package com.moa.cardbank.domain.card.model.dto.getMyCard;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyCardsRequestDto {
    private String phoneNumber;
}
