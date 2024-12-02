package com.moa.moapay.domain.dutchpay.repository;
import com.moa.moapay.domain.dutchpay.model.dto.SimpleOrderInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DutchPayRedisRepository {
    private static final String FCM_TOKEN_KEY_PREFIX = "fcm_token:";
    private final String keyPrefix = "dutchpay:";
    public final RedisTemplate<String, String> redisTemplate;

    // FCM 토큰 저장 메서드
    public void save(String dutchId, String requestId) {
        String key = FCM_TOKEN_KEY_PREFIX + dutchId;
        redisTemplate.opsForValue().set(key, requestId, 3600 * 2, TimeUnit.SECONDS);
    }

    // FCM 토큰 조회 메서드
    public String getToken(String dutchId) {
        String key = FCM_TOKEN_KEY_PREFIX + dutchId;
        return redisTemplate.opsForValue().get(key);
    }

    public void RegisterSimpleOrderInfo(UUID orderId, SimpleOrderInfoDto dto) {
        log.info("saving simple order information");
        if(dto == null) {
            log.info("SimpleOrderInfoDto is null");
            return;
        }
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String key = keyPrefix + orderId.toString();
        // not null인 경우에만 삽입 시도
        if (dto.getThumbnailUrl() != null) {
            valueOps.set(key + ":thumbnailUrl", dto.getThumbnailUrl(), 1, TimeUnit.HOURS);
        }
        if (dto.getUrl() != null) {
            valueOps.set(key + ":url", dto.getUrl(), 1, TimeUnit.HOURS);
        }
        if (dto.getItemNames() != null) {
            listOps.rightPushAll(key + ":itemNames", dto.getItemNames());
            redisTemplate.expire(key + ":itemNames", 1, TimeUnit.HOURS);
        }
    }

    public SimpleOrderInfoDto GetSimpleOrderInfo(UUID orderId) {
        log.info("getting simple order information");
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String key = keyPrefix + orderId.toString();
        String thumbnailUrl = valueOps.get(key + ":thumbnailUrl");
        String url = valueOps.get(key + ":url");
        long size = listOps.size(key + ":itemNames");
        String[] itemName = new String[(int) size];
        for (int s = 0; s < size; ++s) {
            itemName[s] = listOps.index(key + ":itemNames", s);
        }
        return SimpleOrderInfoDto.builder()
                .thumbnailUrl(thumbnailUrl)
                .url(url)
                .itemNames(itemName)
                .build();
    }
}
    
