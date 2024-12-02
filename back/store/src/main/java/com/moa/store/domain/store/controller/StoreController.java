package com.moa.store.domain.store.controller;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moa.store.domain.store.model.dto.CreateMerchantRequestDto;
import com.moa.store.domain.store.model.dto.MerchantDto;
import com.moa.store.domain.store.model.dto.UpdateMerchantRequestDto;
import com.moa.store.domain.store.service.StoreService;
import com.moa.store.global.response.ResultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store/merchant")
@Slf4j
public class StoreController {

	private final StoreService storeService;

	// 가맹점 생성, API 명세서 10행
	@PostMapping
	public ResponseEntity<ResultResponse> createStore(@RequestBody CreateMerchantRequestDto createMerchantRequestDto) {
		MerchantDto merchant = new MerchantDto(storeService.createStore(createMerchantRequestDto));
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.CREATED, "가맹점 등록을 완료했습니다.", merchant);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 가맹점 정보 수정, API 명세서 11행
	@PutMapping("/{uuid}")
	public ResponseEntity<ResultResponse> updateStore(@PathVariable("uuid") UUID uuid, @RequestBody UpdateMerchantRequestDto updateMerchantRequestDto) {
		MerchantDto merchant = new MerchantDto(storeService.updateStore(uuid, updateMerchantRequestDto));
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "가맹점 정보 수정을 완료했습니다.", merchant);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 가맹점 삭제, API 명세서 12행
	@DeleteMapping("/{uuid}")
	public ResponseEntity<ResultResponse> deleteStore(@PathVariable("uuid") UUID uuid) {
		storeService.deleteStore(uuid);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "가맹점 삭제를 완료했습니다.");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 가맹점 전체 조회, API 명세서 13행
	@GetMapping("/list")
	public ResponseEntity<ResultResponse> getStores() {
		List<MerchantDto> list = storeService.getStores();
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "가맹점 전체조회를 완료했습니다.", list);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 가맹점 상세 조회, API 명세서 14행
	@GetMapping("/{uuid}")
	public ResponseEntity<ResultResponse> getStore(@PathVariable("uuid") UUID uuid) {
		MerchantDto merchant = new MerchantDto(storeService.getStore(uuid));
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "가맹점 조회를 완료했습니다.", merchant);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}
}
