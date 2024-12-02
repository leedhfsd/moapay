package com.moa.moapay.domain.dutchpay.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class FCMRedisRepository {

    private static final String FCM_TOKEN_KEY_PREFIX = "fcm_token:";
    public final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public FCMRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // FCM 토큰 저장 메서드
    public void saveToken(String userId, String token, long expirationTimeInSeconds) {
        String key = FCM_TOKEN_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, token);
    }

    // FCM 토큰 조회 메서드
    public String getToken(String userId) {
        String key = FCM_TOKEN_KEY_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    // 토큰 목록 조회 (예: 모든 사용자)
    public Set<String> getAllTokens() {
        return redisTemplate.keys(FCM_TOKEN_KEY_PREFIX + "*");
    }

    // 토큰 삭제 메서드
    public void deleteToken(String userId) {
        String key = FCM_TOKEN_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
