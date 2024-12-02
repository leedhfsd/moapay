package com.moa.moapay.domain.generalpay.repository;

import com.moa.moapay.domain.generalpay.model.dto.ExecuteGeneralPayRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class GeneralPayRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean registRequestId(UUID requestId) {
        // redis 레포에 삽입을 시도하고, 만일 이미 있는 값이었다면 false를 반환한다
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String key = "general-pay-id"+requestId.toString();
        boolean succeed = ops.setIfAbsent(key, "checked");
        if(succeed) {
            redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        }
        return succeed;
    }

    public void registerPaymentInformation(UUID code, ExecuteGeneralPayRequestDto dto) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String key = "general-pay-id"+code.toString();
        ops.put(key, "orderId", dto.getOrderId().toString());
        ops.put(key, "merchantId", dto.getMerchantId().toString());
        // 일단은 주문ID와 상점ID만 넣고, 추후 필요한 정보 있으면 더 넣기로
    }
}
