package com.moa.payment.domain.statistics.service;

import com.moa.payment.domain.statistics.model.dto.*;

public interface StatisticsService {
    MonthlyConsumptionResponseDto getMonthlyConsumption(int year, int month, GetMyCardIdsRequestDto getMyCardIdsRequestDto);
    MonthlyBenefitResponseDto getMonthlyBenefit(int year, int month, GetMyCardIdsRequestDto getMyCardIdsRequestDto);
    YearlyConsumptionResponseDto getYearlyConsumption(GetMyCardIdsRequestDto getMyCardIdsRequestDto);
    YearlyBenefitResponseDto getYearlyBenefit(GetMyCardIdsRequestDto getMyCardIdsRequestDto);
}
