package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DutchGetMyPriceRequestDto {
    private UUID roomId;
    private UUID memberId;
}
