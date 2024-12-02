package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardRestWrapperDto {
    private String status;
    private String message;
    private CardRestTemplateDto data;
}
