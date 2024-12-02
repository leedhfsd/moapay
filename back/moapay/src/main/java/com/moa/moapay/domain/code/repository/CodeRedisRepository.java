package com.moa.moapay.domain.code.repository;

import com.moa.moapay.domain.code.model.dto.GetBarcodeRequestDto;
import com.moa.moapay.domain.code.model.dto.GetQRCodeRequestDto;
import com.moa.moapay.domain.generalpay.model.CardSelectionType;
import com.moa.moapay.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class CodeRedisRepository {
    @Value("${spring.redis.qrcode-expire}")
    private int qrCodeExpire;

    @Value("${spring.redis.barcode-expire}")
    private int barcodeExpire;

    private final RedisTemplate<String, String> redisTemplate;

    public boolean QRCodeRegistTest(int QRCode) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String code = codeToString(QRCode, 7);
        if(redisTemplate.hasKey("blacklist:QR:" + code)) {
            return false; // 블랙리스트 내에 존재하는 key를 생성하려고 했다면 키 생성 실패
        }
        String key = "QR:"+code;
        return ops.putIfAbsent(key, "exist", "check");
    }

    public String RegistQRCodeInfo(int QRCode, GetQRCodeRequestDto dto) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String code = codeToString(QRCode, 7);
        String qr = "QR:" + code;
        ops.put(qr, "orderId", dto.getOrderId().toString());
        ops.put(qr, "merchantId", dto.getMerchantId().toString());
        ops.put(qr, "merchantName", dto.getMerchantName());
        ops.put(qr, "categoryId", dto.getCategoryId());
        ops.put(qr, "totalPrice", String.valueOf(dto.getTotalPrice()));
        // 데이터를 전부 넣은 후, TIL 설정
        redisTemplate.expire(qr, qrCodeExpire, TimeUnit.MINUTES);
        return code;
    }

    public HashMap<String, String> findQRCodeInfo(String QRCode) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String qr = "QR:" + QRCode;
        if (!ops.hasKey(qr, "exist") || redisTemplate.hasKey("blacklist:QR:" + QRCode)) {
            // 존재하지 않는 QRCode를 보낸 경우, 예외응답 발생
            throw new BusinessException(HttpStatus.BAD_REQUEST, "요청한 코드와 대응되는 정보가 없습니다.");
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("orderId", ops.get(qr, "orderId"));
        result.put("merchantId", ops.get(qr, "merchantId"));
        result.put("merchantName", ops.get(qr, "merchantName"));
        result.put("categoryId", ops.get(qr, "categoryId"));
        result.put("totalPrice", ops.get(qr, "totalPrice"));
        return result;
    }

    public void disableQRCodeInfo(String QRCode) {
        String key = "blacklist:QR:" + QRCode;
        log.info("disable QRCode : {}", key);
        long expireTime = redisTemplate.getExpire("QR:" + QRCode);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.append(key, "exist");
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /* barcode */

    public boolean barcodeRegistTest(int barcode) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String code = codeToString(barcode, 8);
        if(redisTemplate.hasKey("blacklist:BAR:" + code)) {
            return false; // 블랙리스트 내에 존재하는 key를 생성하려고 했다면 키 생성 실패
        }
        String key = "BAR:"+code;
        return ops.putIfAbsent(key, "exist", "check");
    }

    public String registBarcodeInfo(int barcode, GetBarcodeRequestDto dto) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String code = codeToString(barcode, 8);
        String key = "BAR:"+code;
        ops.put(key, "memberId", dto.getMemberId().toString());
        ops.put(key, "type", dto.getType().toString());
        if(dto.getType() == CardSelectionType.FIX) {
            ops.put(key, "cardNumber", dto.getCardNumber());
            ops.put(key, "cvc", dto.getCvc());
        }
        // 데이터를 전부 넣은 후, TIL 설정
        redisTemplate.expire(key, barcodeExpire, TimeUnit.MINUTES);
        return code;
    }

    public HashMap<String, String> findBarcodeInfo(String barcode) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String key = "BAR:"+barcode;
        if(!ops.hasKey(key, "exist")|| redisTemplate.hasKey("blacklist:BAR:" + barcode)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "요청한 코드와 대응되는 정보가 없습니다.");
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("memberId", ops.get(key, "memberId"));
        result.put("type", ops.get(key, "type"));
        result.put("cardNumber", ops.get(key, "cardNumber"));
        result.put("cvc", ops.get(key, "cvc"));
        return result;
    }

    public void disableBarcodeInfo(String barcode) {
        String key = "balcklist:BAR:"+barcode;
        log.info("disable Barcode : {}", key);
        long expireTime = redisTemplate.getExpire("BAR:"+barcode);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.append(key, "exist");
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public String codeToString(int code, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(code));
        if(sb.length() < length) {
            int N = length - sb.length();
            for(int i = 0 ; i < N ; ++i) {
                sb.insert(0, "0"); // 모자란만큼 0을 붙여준다.
            }
        }
        return sb.toString();
    }

}
