package com.moa.payment.domain.charge.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChargeRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean registRequestCode(UUID code) {
        // redis 레포에 삽입을 시도하고, 만일 이미 있는 값이었다면 false를 반환한다
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String key = "payment-code:"+code.toString();
        boolean succeed = ops.setIfAbsent(key, "checked");
        if(succeed) {
            redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        }
        return succeed;
    }
}
