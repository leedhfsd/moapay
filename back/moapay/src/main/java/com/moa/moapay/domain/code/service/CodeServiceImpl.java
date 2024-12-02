package com.moa.moapay.domain.code.service;

import com.moa.moapay.domain.card.entity.MyCard;
import com.moa.moapay.domain.card.repository.MyCardRepository;
import com.moa.moapay.domain.code.model.dto.*;
import com.moa.moapay.domain.code.repository.CodeRedisRepository;
import com.moa.moapay.domain.generalpay.model.CardSelectionType;
import com.moa.moapay.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeServiceImpl implements CodeService {

    private final CodeRedisRepository redisRepository;
    private final MyCardRepository myCardRepository;

    @Override
    public GetQRCodeResponseDto getQRCode(GetQRCodeRequestDto dto) {
        log.info("make QRcode => {}", dto);
        // [1] 등록된 값과 연동할 QRcode 발급, redis에 등록(Hash)
        int QRCode;
        while(true) {
            QRCode = (int)(Math.random() * 10000000);
            if(redisRepository.QRCodeRegistTest(QRCode)) break; // 성공적으로 등록되었다면 return
        }
        // [2] 중복 아닌 거 확인했다면 그 값 기반으로 나머지 결제정보 등록
        String code = redisRepository.RegistQRCodeInfo(QRCode, dto);
        log.info("QRcode done");
        // [3] QRcode 리턴
        return GetQRCodeResponseDto.builder()
                .QRCode(code)
                .build();
    }

    @Override
    public GetQRInfoResponseDto getQRInfo(String QRCode) {
        log.info("get QRcode info => {}", QRCode);
        HashMap<String, String> searchedInfo = redisRepository.findQRCodeInfo(QRCode);
        GetQRInfoResponseDto result = GetQRInfoResponseDto.builder()
                .orderId(UUID.fromString(searchedInfo.get("orderId")))
                .merchantId(UUID.fromString(searchedInfo.get("merchantId")))
                .merchantName(searchedInfo.get("merchantName"))
                .categoryId(searchedInfo.get("categoryId"))
                .totalPrice(Long.parseLong(searchedInfo.get("totalPrice")))
                .build();
        log.info("got information => {}", result);
        return result;
    }

    @Override
    public void disableQRCode(String QRCode) {
        redisRepository.disableQRCodeInfo(QRCode);
    }

    @Override
    public GetBarcodeResponseDto getBarcode(GetBarcodeRequestDto dto) {
        // 정보를 등록하기 전, 정보 유효성 검사를 시행
        if(dto.getType() == CardSelectionType.FIX) {
            MyCard myCard = myCardRepository.findByCardNumber(dto.getCardNumber())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
            if(!myCard.getCvc().equals(dto.getCvc()) || !dto.getMemberId().equals(myCard.getMemberId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
            }
        }
        int barcode;
        while(true) {
            barcode = (int)(Math.random() * 100000000);
            if(redisRepository.barcodeRegistTest(barcode)) break; // 성공적으로 등록되었다면 return
        }
        String code = redisRepository.registBarcodeInfo(barcode, dto);
        return GetBarcodeResponseDto.builder()
                .barcode(code)
                .build();
    }

    @Override
    public GetBarcodeInfoResponseDto getBarcodeInfo(String barcode) {
        log.info("get barcode info");
        HashMap<String, String> searchedInfo = redisRepository.findBarcodeInfo(barcode);
        UUID memberId = UUID.fromString(searchedInfo.get("memberId"));
        CardSelectionType type = CardSelectionType.valueOf(searchedInfo.get("type"));
        String cardNumber = null;
        String cvc = null;
        if(type == CardSelectionType.FIX) {
            cardNumber = searchedInfo.get("cardNumber");
            cvc = searchedInfo.get("cvc");
        }
        return GetBarcodeInfoResponseDto.builder()
                .memberId(memberId)
                .type(type)
                .cardNumber(cardNumber)
                .cvc(cvc)
                .build();
    }
}
