package com.moa.payment.domain.analysis.controller;

import java.util.UUID;

import com.moa.payment.domain.analysis.model.dto.CardHistoryRequestDto;
import com.moa.payment.domain.analysis.model.dto.CardHistoryResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moa.payment.domain.analysis.model.dto.averageRequestDto;
import com.moa.payment.domain.analysis.model.dto.averageResponseDto;
import com.moa.payment.domain.analysis.model.dto.getMemberResponseDto;
import com.moa.payment.domain.analysis.service.AnalysisService;
import com.moa.payment.global.response.ResultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

	//성별+나이대 총소비량 + 멤버수 저장하기
	@GetMapping("/saveAverage")
	public ResponseEntity<ResultResponse> saveAverage(){
		//uuid 통해 member 찾고 member의 성별, 나이대 구한 후 소비평균 가져오기
		analysisService.setAverage();
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "성별/연령대 별 소비총합과 멤버 수 저장 완료");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	//현재 멤버의 성별+연령대에 따라
	@PostMapping("/getAverage")
	public  ResponseEntity<ResultResponse> getAverage(@RequestBody averageRequestDto dto){
		UUID memberId=dto.getMemberId();
		Long average=analysisService.average(memberId);
		getMemberResponseDto member=analysisService.getMemberInfo(memberId);
		averageResponseDto averageDto=new averageResponseDto();
		averageDto.setAverage(average);
		averageDto.setGender(member.getGender());
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "소비평균",averageDto);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

    /* 카드별 결제 내역 영역 */
    @PostMapping("/history")
    public ResponseEntity<ResultResponse> getCardHistory(@RequestBody CardHistoryRequestDto dto) {
		CardHistoryResponseDto responseDto = analysisService.getCardHistory(dto);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "결제 내역을 가져왔습니다.", responseDto);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}


}
