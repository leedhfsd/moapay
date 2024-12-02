package com.moa.payment.domain.statistics.controller;

import com.moa.payment.domain.statistics.model.dto.GetMyCardIdsRequestDto;
import com.moa.payment.domain.statistics.model.dto.MonthlyBenefitResponseDto;
import com.moa.payment.domain.statistics.model.dto.MonthlyConsumptionResponseDto;
import com.moa.payment.domain.statistics.model.dto.YearlyBenefitResponseDto;
import com.moa.payment.domain.statistics.model.dto.YearlyConsumptionResponseDto;
import com.moa.payment.domain.statistics.service.StatisticsService;
import com.moa.payment.global.response.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/consumption/{year}/{month}")
    public ResponseEntity<ResultResponse> getMonthlyConsumption(@PathVariable int year,
                                                                @PathVariable int month, @RequestBody GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        MonthlyConsumptionResponseDto monthlyConsumption = statisticsService.getMonthlyConsumption(year, month,
            getMyCardIdsRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "월별 결제 내역 조회에 성공했습니다.", monthlyConsumption);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/benefit/{year}/{month}")
    public ResponseEntity<ResultResponse> getMonthlyBenefit(@PathVariable int year,
                                                            @PathVariable int month, @RequestBody GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        MonthlyBenefitResponseDto monthlyBenefit = statisticsService.getMonthlyBenefit(year, month,
            getMyCardIdsRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "월별 혜택 내역 조회에 성공했습니다.", monthlyBenefit);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/consumption/statistics")
    public ResponseEntity<ResultResponse> getYearlyConsumption(@RequestBody GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        YearlyConsumptionResponseDto yearlyConsumption = statisticsService.getYearlyConsumption(getMyCardIdsRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "최근 1년 소비 조회에 성공했습니다.", yearlyConsumption);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/benefit/statistics")
    public ResponseEntity<ResultResponse> getYearlyBenefit(@RequestBody GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        YearlyBenefitResponseDto yearlyBenefit = statisticsService.getYearlyBenefit(getMyCardIdsRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "최근 1년 혜택 조회에 성공했습니다.", yearlyBenefit);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

}
