package com.moa.moapay.domain.card.model.dto;

import com.moa.moapay.domain.card.model.dto.getMyCard.GetMyCardDto;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPaymentLogWrapper {
    private String status;
    private String message;
    private GetPaymentLogResponseDto data;
}
