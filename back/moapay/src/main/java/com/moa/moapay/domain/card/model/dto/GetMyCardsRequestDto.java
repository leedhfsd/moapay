package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyCardsRequestDto {
    private UUID memberUuid;
    private String phoneNumber;
}
