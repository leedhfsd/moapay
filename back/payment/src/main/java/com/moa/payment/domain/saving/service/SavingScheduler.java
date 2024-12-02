package com.moa.payment.domain.saving.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SavingScheduler {

	private final SavingService savingService;

	@Scheduled(cron="0 0 0 1 1/1 *") //매달 1일 00:00:00
	//@Scheduled(cron="0 55 2 1/1 * *") //오전 2시 55분. 테스트용.
	public void resetSaving(){
		savingService.resetSaving();
	}
}
