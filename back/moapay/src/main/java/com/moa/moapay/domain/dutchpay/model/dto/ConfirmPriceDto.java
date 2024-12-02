package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPriceDto {
    private UUID memberId;
    private long price;
}
