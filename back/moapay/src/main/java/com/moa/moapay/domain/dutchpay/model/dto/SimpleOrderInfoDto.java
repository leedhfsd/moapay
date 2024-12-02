package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderInfoDto {
    private String thumbnailUrl;
    private String[] itemNames;
    private String url;
}
