package com.moa.moapay.domain.card.model.dto;

import java.util.UUID;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@ToString
@NoArgsConstructor
public class MyCardInfoDto {

    private String cardNumber;
    private String cvc;
    private boolean performanceFlag;
    private Long cardLimit;
    private Long amount;
    private Long benefitUsage;
    private CardInfoResponseDto cardInfo;
    private boolean cardStatus;
}
