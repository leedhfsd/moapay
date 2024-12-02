package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DutchCancelDto {
    private UUID paymentId;
    private UUID cardId;
    private String cardNumber;
    private String cvc;
}
