package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyPriceResponseDto {
    private long price;
}
