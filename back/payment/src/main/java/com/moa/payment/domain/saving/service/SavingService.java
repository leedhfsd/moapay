package com.moa.payment.domain.saving.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.moa.payment.domain.charge.entity.PaymentLog;
import com.moa.payment.domain.saving.entity.dto.GetSavingResponseDto;
import com.moa.payment.domain.saving.entity.dto.LimitRequestDto;
import com.moa.payment.domain.saving.entity.dto.SetSavingRequestDto;
import com.moa.payment.domain.saving.entity.dto.UpdateDailyRequestDto;

@Service
public interface SavingService {
	void setLimit(LimitRequestDto dto);
	void setSaving(SetSavingRequestDto dto);
	void updateDaily(UpdateDailyRequestDto dto);
	void updateTodayAmount(PaymentLog paymentLog);
	GetSavingResponseDto getSaving(UUID memberId);
	void resetSaving();
}
