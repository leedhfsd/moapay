package com.moa.moapay.domain.card.model.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendCardResponseDto {
    private Map<String, Long> categoryUsage;
    private List<RecomendCardInfoResponseDto> recommend;
}
