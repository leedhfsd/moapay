package com.moa.cardbank.domain.card.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardRegistrationRequestDto {
    private String cardNumber;
    private String cvc;
}
