package com.moa.moapay.domain.card.model.dto.getMyCard;

import com.moa.moapay.domain.card.model.dto.CardRestTemplateDto;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetMyCardDtoWrapper {
    private String status;
    private String message;
    private List<GetMyCardDto> data;
}
