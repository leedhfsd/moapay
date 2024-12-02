package com.moa.payment.domain.statistics.service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.payment.domain.analysis.model.dto.CardHistoryPaymentLogDto;
import com.moa.payment.domain.analysis.model.dto.CardHistoryRequestDto;
import com.moa.payment.domain.analysis.model.dto.CardHistoryResponseDto;
import com.moa.payment.domain.analysis.repository.PaymentLogQueryRepository;
import com.moa.payment.domain.analysis.service.AnalysisService;
import com.moa.payment.domain.statistics.model.dto.*;
import com.moa.payment.global.exception.BusinessException;
import com.moa.payment.global.response.ResultResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final MoaPayClient moaPayClient;
    private final PaymentLogQueryRepository paymentLogQueryRepository;
    private final ObjectMapper objectMapper;

    public List<CardHistoryPaymentLogDto> getPaymentLogs(int year, int month, List<UUID> cardIds) {
        log.info("getPaymentLogs - year : {}, month : {}", year, month);
        YearMonth dateInfo = YearMonth.of(year, month);
        int lastDay = dateInfo.atEndOfMonth().lengthOfMonth();
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year, month, lastDay, 23, 59);
        List<CardHistoryPaymentLogDto> result = paymentLogQueryRepository.findAllCardsPaymentLogs(cardIds,
            startTime, endTime);
        log.info("getPaymentLogs -> result count : {}", result.size());
        for(CardHistoryPaymentLogDto logDto : result) {
            log.info(logDto.toString());
        }
        log.info("log list ended");
        return result;
    }

    public List<UUID> getCardsUUID(GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        try {
            ResponseEntity<ResultResponse> response = moaPayClient.getCardIdsByMemberId(getMyCardIdsRequestDto);
            if(response.getStatusCode() != HttpStatus.OK){
                throw new BusinessException(HttpStatus.BAD_REQUEST, "마이카드 UUID 불러오기 실패");
            }
            return objectMapper.convertValue(response.getBody().getData(), GetMyCardIdsResponseDto.class).getMyCardIds();
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "MoaPay에서 사용자 카드 UUID 불러오기 실패 (Feign 문제)");
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "MoaPay에서 사용자 카드 UUID 불러오기 실패 " + e.getMessage());
        }
    }

    @Override
    public MonthlyConsumptionResponseDto getMonthlyConsumption(int year, int month, GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        List<UUID> cardIds = getCardsUUID(getMyCardIdsRequestDto);
        List<CardHistoryPaymentLogDto> paymentLogs = getPaymentLogs(year, month, cardIds);
        long totalAmount = paymentLogs.stream()
            .mapToLong(CardHistoryPaymentLogDto::getAmount)
            .sum();

        Map<String, Long> categoryToTotalMoneyMap = paymentLogs.stream()
            .collect(Collectors.groupingBy(
                CardHistoryPaymentLogDto::getCategoryId,
                Collectors.summingLong(CardHistoryPaymentLogDto::getAmount)
            ));

        List<MonthlyPaymentStatisticsDto> result = categoryToTotalMoneyMap.entrySet().stream()
            .map(entry -> {
                String category = entry.getKey();
                long categoryTotalMoney = entry.getValue();
                double percentage = (double) categoryTotalMoney / totalAmount * 100;
                return new MonthlyPaymentStatisticsDto(category, categoryTotalMoney, percentage);
            })
            .toList();

        return MonthlyConsumptionResponseDto.builder()
            .year(year)
            .month(month)
            .memberId(getMyCardIdsRequestDto.getMemberId())
            .totalAmounts(totalAmount)
            .paymentStatistics(result)
            .build();
    }

    @Override
    public MonthlyBenefitResponseDto getMonthlyBenefit(int year, int month, GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        List<UUID> cardIds = getCardsUUID(getMyCardIdsRequestDto);
        List<CardHistoryPaymentLogDto> paymentLogs = getPaymentLogs(year, month, cardIds);

        long totalBenefit = paymentLogs.stream()
            .mapToLong(CardHistoryPaymentLogDto::getBenefitBalance)
            .sum();

        Map<String, Long> categoryToTotalMoneyMap = paymentLogs.stream()
            .collect(Collectors.groupingBy(
                CardHistoryPaymentLogDto::getCategoryId,
                Collectors.summingLong(CardHistoryPaymentLogDto::getBenefitBalance)
            ));

        List<MonthlyPaymentStatisticsDto> result = categoryToTotalMoneyMap.entrySet().stream()
            .map(entry -> {
                String category = entry.getKey();
                long categoryTotalMoney = entry.getValue();
                double percentage = (double) categoryTotalMoney / totalBenefit * 100;
                return new MonthlyPaymentStatisticsDto(category, categoryTotalMoney, percentage);
            })
            .toList();

        return MonthlyBenefitResponseDto.builder()
            .year(year)
            .month(month)
            .memberId(getMyCardIdsRequestDto.getMemberId())
            .totalBenefits(totalBenefit)
            .paymentStatistics(result)
            .build();
    }

    @Override
    public YearlyConsumptionResponseDto getYearlyConsumption(GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        List<UUID> cardIds = getCardsUUID(getMyCardIdsRequestDto);
        List<MonthlyAmount> monthlyAmounts = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            int year = YearMonth.now().getYear();
            int month = YearMonth.now().getMonthValue();
            YearMonth currentYearMonth = YearMonth.of(year, month).minusMonths(i);
            List<CardHistoryPaymentLogDto> paymentLogs = getPaymentLogs(
                currentYearMonth.getYear(),
                currentYearMonth.getMonthValue(),
                cardIds
            );

            long totalAmount = paymentLogs.stream()
                .mapToLong(CardHistoryPaymentLogDto::getAmount)
                .sum();

            monthlyAmounts.add(new MonthlyAmount(
                currentYearMonth.getYear(),
                currentYearMonth.getMonthValue(),
                totalAmount
            ));
        }

        return YearlyConsumptionResponseDto.builder()
            .memberId(getMyCardIdsRequestDto.getMemberId())
            .monthlyAmounts(monthlyAmounts)
            .build();
    }

    @Override
    public YearlyBenefitResponseDto getYearlyBenefit(GetMyCardIdsRequestDto getMyCardIdsRequestDto) {
        List<UUID> cardIds = getCardsUUID(getMyCardIdsRequestDto);
        List<MonthlyBenefit> monthlyBenefits = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            int year = YearMonth.now().getYear();
            int month = YearMonth.now().getMonthValue();
            YearMonth currentYearMonth = YearMonth.of(year, month).minusMonths(i);
            List<CardHistoryPaymentLogDto> paymentLogs = getPaymentLogs(
                currentYearMonth.getYear(),
                currentYearMonth.getMonthValue(),
                cardIds
            );

            long totalBenefit = paymentLogs.stream()
                .mapToLong(CardHistoryPaymentLogDto::getBenefitBalance)
                .sum();

            monthlyBenefits.add(new MonthlyBenefit(
                currentYearMonth.getYear(),
                currentYearMonth.getMonthValue(),
                totalBenefit
            ));
        }

        return YearlyBenefitResponseDto.builder()
            .memberId(getMyCardIdsRequestDto.getMemberId())
            .monthlyBenefits(monthlyBenefits)
            .build();
    }
}
