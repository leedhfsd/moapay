package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPaymentLogResponseDto {
    private Map<String, Long> categoryCountMap;
}
