package com.moa.moapay.domain.card.service;

import com.moa.moapay.domain.card.model.RecommendType;
import com.moa.moapay.domain.card.model.dto.CardInfoResponseDto;
import com.moa.moapay.domain.card.model.dto.GetRecomendCardRequestDto;
import com.moa.moapay.domain.card.model.dto.RecommendCardResponseDto;
import com.moa.moapay.domain.generalpay.model.vo.PaymentCardInfoVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface RecommendCardService {

    RecommendCardResponseDto recommendCard(UUID memberId);

    List<PaymentCardInfoVO> recommendPayCard(UUID memberId, String categoryId, RecommendType recommendType, long totalPrice);

}
