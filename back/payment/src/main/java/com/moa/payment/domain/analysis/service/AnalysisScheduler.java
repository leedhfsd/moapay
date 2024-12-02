package com.moa.payment.domain.analysis.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalysisScheduler {

	private final AnalysisService analysisService;

	@Scheduled(cron="0 0 0 1 1/1 *") //매달 1일 00:00:00
	//@Scheduled(cron="0 55 2 1/1 * *") //오전 2시 55분. 테스트용.
	public void saveAverage(){
		analysisService.setAverage();
	}

}
