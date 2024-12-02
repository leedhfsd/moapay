package com.moa.cardbank.domain.store.controller;

import com.moa.cardbank.domain.store.model.dto.CreateStoreRequestDto;
import com.moa.cardbank.domain.store.model.dto.CreateStoreResponseDto;
import com.moa.cardbank.domain.store.service.StoreService;
import com.moa.cardbank.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/store")
public class StoreController {
    private final StoreService storeService;

    @PostMapping("/create")
    public ResponseEntity<ResultResponse> createStore(@RequestBody CreateStoreRequestDto dto) {
        log.info("create new store : {}", dto.getName());
        CreateStoreResponseDto responseDto = storeService.createStore(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "가맹점을 생성했습니다.", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
}
