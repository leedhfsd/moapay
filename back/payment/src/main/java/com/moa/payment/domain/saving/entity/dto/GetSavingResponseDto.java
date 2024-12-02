package com.moa.payment.domain.saving.entity.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetSavingResponseDto {

	UUID memberId;
	long todayAmount; //오늘 사용한 금액
	long limitAmount; //목표금액
	long amount; //이번달 전체 금액
	String daily; //이번달 일별 사용금액

}
