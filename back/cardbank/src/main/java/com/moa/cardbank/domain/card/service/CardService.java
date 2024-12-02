package com.moa.cardbank.domain.card.service;

import com.moa.cardbank.domain.card.model.dto.*;
import com.moa.cardbank.domain.card.model.dto.getMyCard.GetMyCardsRequestDto;
import com.moa.cardbank.domain.card.model.dto.getMyCard.GetMyCardsResponseDto;

import java.util.List;

public interface CardService {
    CreateCardProductResponseDto createCardProduct(CreateCardProductRequestDto dto);
    ExecutePayResponseDto executePay(ExecutePayRequestDto dto);
    CancelPayResponseDto cancelPay(CancelPayRequestDto dto);
    CreateMyCardResponseDto createMyCard(CreateMyCardRequestDto dto);

    List<GetMyCardsResponseDto> getMyCards(GetMyCardsRequestDto getMyCardsRequestDto);

    CardRestResponseDto registration(CardRegistrationRequestDto registrationRequestDto);

    void initialize();
}
