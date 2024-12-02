package com.moa.payment.domain.statistics.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.moa.payment.domain.statistics.model.dto.GetMyCardIdsRequestDto;
import com.moa.payment.global.response.ResultResponse;

@FeignClient(name = "moapay", url = "https://j11c201.p.ssafy.io/api/moapay")
public interface MoaPayClient {

    @PostMapping("core/card/getMyCardIds")
    ResponseEntity<ResultResponse> getCardIdsByMemberId(@RequestBody GetMyCardIdsRequestDto getMyCardIdsRequestDto);
}


