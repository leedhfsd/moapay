package com.moa.moapay.domain.card.service;

import com.moa.moapay.domain.card.model.dto.*;
import com.moa.moapay.domain.card.model.vo.PaymentResultCardInfoVO;

import java.util.List;
import java.util.UUID;


public interface MyCardService {
    List<GetMyCardsResponseDto> getMyCardInfo(UUID memberId);
    List<CardInfoResponseDto> getAllCard();

    List<GetMyCardsResponseDto> getMyCardFromCardBank(GetMyCardsRequestDto getMyCardsRequestDto);
    void renewCardInfo(List<PaymentResultCardInfoVO> renewList);

    GetMyCardsResponseDto registrationCard(CardRegistrationRequestDto registrationRequestDto);

    void disableCard(MyCardStatusRequestDto disableCardRequestDto);
    void ableCard(MyCardStatusRequestDto ableCardRequestDto);

    UUID getMemberId(UUID cardId);
    List<GetMemberCardsDto> getMemberCard(UUID memberId);

    CardHistoryResponseDto getCardHistory(CardHistoryRequestDto cardHistoryRequestDto);
    GetMyCardIdsResponseDto getMyCardIds(UUID memberId);

    void initialize();
}
