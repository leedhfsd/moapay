package com.moa.store.domain.order.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSimpleOrderResponseDto {
    private String thumbnailUrl;
    private String[] itemNames;
    private String url;
}
