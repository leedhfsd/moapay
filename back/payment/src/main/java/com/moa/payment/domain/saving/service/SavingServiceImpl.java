package com.moa.payment.domain.saving.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.moa.payment.domain.charge.entity.PaymentLog;
import com.moa.payment.domain.charge.repository.PaymentLogRepository;
import com.moa.payment.domain.saving.entity.Saving;
import com.moa.payment.domain.saving.entity.dto.GetMemberCardsDto;
import com.moa.payment.domain.saving.entity.dto.GetSavingResponseDto;
import com.moa.payment.domain.saving.entity.dto.LimitRequestDto;
import com.moa.payment.domain.saving.entity.dto.SetSavingRequestDto;
import com.moa.payment.domain.saving.entity.dto.UpdateDailyRequestDto;
import com.moa.payment.domain.saving.repository.SavingRepository;
import com.moa.payment.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

    private final PaymentLogRepository paymentLogRepository;
    private final SavingRepository savingRepository;
    private final RestTemplate restTemplate;
    @Value("${external-url.core}")
    String coreUrl;
    private Long[][] monthAmount; //1~12월 & 1~31일

    @Override
    @Transactional
    public void setLimit(LimitRequestDto dto) {
        Saving saving = savingRepository.findByMemberId(dto.getMemberId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "해당 멤버의 절약 정보를 찾을 수 없습니다."));
        saving.updateLimit(dto.getLimitAmount());
        savingRepository.save(saving);
    }

    @Override
    @Transactional
    public void setSaving(SetSavingRequestDto dto) {
        List<GetMemberCardsDto> myCards = getMycards(dto.getMemberId());
        monthAmount = new Long[13][32];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 31; j++) {
                monthAmount[i][j] = 0L;
            }
        }
        Date now = new Date();
        for (GetMemberCardsDto card : myCards) { //카드별
            System.out.println(card.getCardId());
            UUID cardId = card.getCardId();
            List<PaymentLog> paymentLogs = paymentLogRepository.findAllFromMonth(cardId); //이번달 해당카드의 결제내역

            for (PaymentLog log : paymentLogs) {
                System.out.println(log.getAmount());
            }

            Map<LocalDate, List<PaymentLog>> groupedPaymentLogs = processPaymentLogs(paymentLogs);
            long today = 0;
            groupedPaymentLogs.forEach((date, logs) -> {
                int month = date.getMonthValue();
                int day = date.getDayOfMonth();
                System.out.println(month + "월/ " + day + "일");
                logs.forEach(log -> {
                    monthAmount[month][day] += log.getAmount();
                });
            });

        }
        long totalAmount = 0L;
        int currentMonth = now.getMonth() + 1; //1~12
        int currentDate = now.getDate(); //1~31
        String dailyString = "";
        long todayAmount = monthAmount[currentMonth][currentDate];
        for (int i = 1; i < currentDate; i++) {
            dailyString += monthAmount[currentMonth][i] + ",";
            totalAmount += monthAmount[currentMonth][i];
        }

        System.out.println(dailyString);
        System.out.println(todayAmount);

        Saving saving = Saving.builder()
                .amount(totalAmount)
                .todayAmount(todayAmount)
                .daily(dailyString)
                .memberId(dto.getMemberId())
                .limitAmount(0L)
                .build();

        savingRepository.save(saving);
    }

    public Map<LocalDate, List<PaymentLog>> processPaymentLogs(List<PaymentLog> paymentLogs) {
        return paymentLogs.stream()
                // createTime을 기준으로 오름차순 정렬
                .sorted((p1, p2) -> p1.getCreateTime().compareTo(p2.getCreateTime()))
                // LocalDate를 키로 사용하여 그룹화
                .collect(Collectors.groupingBy(paymentLog -> paymentLog.getCreateTime().toLocalDate(), TreeMap::new,
                        Collectors.toList()));
    }

    //멤버의 카드 가져오기
    public List<GetMemberCardsDto> getMycards(UUID memberId) {

        String url = coreUrl + "/card/getMemberCard";

        // HTTP 요청 본문 생성
        HttpEntity<UUID> request = new HttpEntity<>(memberId);

        // POST 요청 보내기 및 응답 받기
        ResponseEntity<List<GetMemberCardsDto>> response = restTemplate.exchange(url, HttpMethod.POST, request,
                new ParameterizedTypeReference<List<GetMemberCardsDto>>() {
                });

        return response.getBody();

    }

    //스케줄링 완료) 매일 23:59:59
    @Override
    @Transactional
    public void updateDaily(UpdateDailyRequestDto dto) {
        Saving saving = savingRepository.findByMemberId(dto.getMemberId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "해당 멤버의 절약 정보를 찾을 수 없습니다."));
        ;
        Long todayAmount = saving.getTodayAmount();
        saving.updateDaily(todayAmount);
        saving.resetTodayAmount();
        savingRepository.save(saving);
    }

    //paymentLog 업데이트 되면 todayAmount 업데이트 + 총 amount 업데이트
    @Override
    @Transactional
    public void updateTodayAmount(PaymentLog paymentLog) {
        long amount = paymentLog.getAmount(); //방금 결제한 금액
        UUID cardId = paymentLog.getCardId();
        UUID memberId = getMemberId(cardId);

        Saving saving = savingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "해당 멤버의 절약 정보를 찾을 수 없습니다."));
        ;
        System.out.println("방금 쓴 금액:" + amount);
        saving.updateTodayAmount(amount);
        saving.updateAmount(amount);
        savingRepository.save(saving);
    }

    @Override
    public GetSavingResponseDto getSaving(UUID memberId) {
        Saving saving = savingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "해당 멤버의 절약 정보를 찾을 수 없습니다."));
        ;
        GetSavingResponseDto savingDto = GetSavingResponseDto.builder()
                .todayAmount(saving.getTodayAmount())
                .amount(saving.getAmount())
                .limitAmount(saving.getLimitAmount())
                .daily(saving.getDaily())
                .memberId(memberId)
                .build();
        return savingDto;
    }

    //paymentlog의 cardId에서 member가져오기
    public UUID getMemberId(UUID cardId) {
        String url = coreUrl + "/card/getMemberId";
        // POST 요청으로 cardId를 보내고, UUID로 응답을 받음
        ResponseEntity<UUID> response = restTemplate.postForEntity(url, cardId, UUID.class);
        return response.getBody();  // 응답에서 UUID를 반환
    }

    @Override
    //스케줄링. 매달 1일 00시 00분에 실행.
    //saving의 모든 데이터의 amount, today_amount, daily 초기화
    public void resetSaving() {
        List<Saving> savings = savingRepository.findAll();

        for (Saving saving : savings) {
            saving.resetSaving();
        }

        savingRepository.saveAll(savings);

    }

}
