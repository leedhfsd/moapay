package com.moa.moapay.domain.code.model.dto;

import com.moa.moapay.domain.generalpay.model.CardSelectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBarcodeInfoResponseDto {
    private UUID memberId;
    private CardSelectionType type;
    private String cardNumber;
    private String cvc;
}
