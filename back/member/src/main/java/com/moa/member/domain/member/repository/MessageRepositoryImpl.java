package com.moa.member.domain.member.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository{

	private static final String PREFIX = "sms:"; // key값이 중복되지 않도록 상수 선언
	private final int LIMIT_TIME = 3 * 60; // 인증번호 유효 시간 : 3분

	private final StringRedisTemplate stringRedisTemplate;

	// Redis에 저장
	@Override
	public void createSmsCertification(String toNumber, String code) {
		stringRedisTemplate.opsForValue()
			.set(PREFIX + toNumber, code, Duration.ofSeconds(LIMIT_TIME));
	}

	// 휴대전화 번호에 해당하는 인증번호 불러오기
	@Override
	public String getSmsCertification(String toNumber) {
		return stringRedisTemplate.opsForValue().get(PREFIX + toNumber);
	}

	// 인증 완료 시, 인증번호 Redis에서 삭제
	@Override
	public void deleteSmsCertification(String toNumber) {
		stringRedisTemplate.delete(PREFIX + toNumber);
	}

	// Redis에 해당 휴대번호로 저장된 인증번호가 존재하는지 확인
	@Override
	public boolean hasKey(String toNumber) {
		return stringRedisTemplate.hasKey(PREFIX + toNumber);
	}

}
