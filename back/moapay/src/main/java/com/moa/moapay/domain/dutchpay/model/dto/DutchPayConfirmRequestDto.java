package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutchPayConfirmRequestDto {
    private UUID roomId;
    private long memberCnt;
    List<ConfirmPriceDto> confirmPriceDtos;
}
