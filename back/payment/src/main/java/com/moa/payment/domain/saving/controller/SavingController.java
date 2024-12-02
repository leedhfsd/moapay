package com.moa.payment.domain.saving.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moa.payment.domain.saving.entity.dto.GetSavingRequestDto;
import com.moa.payment.domain.saving.entity.dto.GetSavingResponseDto;
import com.moa.payment.domain.saving.entity.dto.LimitRequestDto;
import com.moa.payment.domain.saving.entity.dto.SetSavingRequestDto;
import com.moa.payment.domain.saving.entity.dto.UpdateDailyRequestDto;
import com.moa.payment.domain.saving.service.SavingService;
import com.moa.payment.global.response.ResultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/saving")
public class SavingController {

	private final SavingService savingService;

	@PostMapping("/setLimit")
	public ResponseEntity<ResultResponse> setLimit(@RequestBody LimitRequestDto dto) {
		savingService.setLimit(dto);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "월 목표금액 설정 완료");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	@PostMapping("/setSaving")
	public ResponseEntity<ResultResponse> setSaving(@RequestBody SetSavingRequestDto dto) {
		savingService.setSaving(dto);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "초기 절약 설정 완료");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	@PostMapping("/getSaving")
	public ResponseEntity<ResultResponse> getSaving(@RequestBody GetSavingRequestDto dto) {
		GetSavingResponseDto savingDto = savingService.getSaving(dto.getMemberId());
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "멤버 절약 정보 가져오기",savingDto);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	//매일 밤 23:59 호출
	@PostMapping("/updateDaily")
	public ResponseEntity<ResultResponse> updateDaily(@RequestBody UpdateDailyRequestDto dto) {
		savingService.updateDaily(dto);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "일별 지출 업데이트 완료");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	@GetMapping("/resetSaving")
	public ResponseEntity<ResultResponse> resetSaving() {
		savingService.resetSaving();
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "모든 멤버 saving 초기화 완료");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}


}
