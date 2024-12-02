package com.moa.payment.domain.analysis.service;

import com.moa.payment.domain.analysis.model.dto.CardHistoryRequestDto;
import com.moa.payment.domain.analysis.model.dto.CardHistoryResponseDto;
import com.moa.payment.domain.analysis.model.dto.getMemberResponseDto;

import java.util.UUID;

public interface AnalysisService {
	void setAverage();
    Long average(UUID memberId);
    CardHistoryResponseDto getCardHistory(CardHistoryRequestDto dto);
	getMemberResponseDto getMemberInfo(UUID memberId);
}
