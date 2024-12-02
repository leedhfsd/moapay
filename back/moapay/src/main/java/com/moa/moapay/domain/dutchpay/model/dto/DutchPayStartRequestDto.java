package com.moa.moapay.domain.dutchpay.model.dto;

import com.moa.moapay.domain.code.model.dto.GetQRCodeRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class DutchPayStartRequestDto {

    // 맴버 아이디
    private UUID memberId;
    // 총원 수
    private long maxMember;
    // 상품 정보

    //GetQRCodeRequestDto 와 같음
    private UUID orderId;
    private UUID merchantId;
    private String merchantName;
    private String categoryId;
    private long totalPrice;

    @Override
    public String toString() {
        return "DutchPayStartRequestDto{" +
                ", memberId=" + memberId +
                ", memberCnt=" + maxMember +
                ", orderId=" + orderId +
                ", merchantId=" + merchantId +
                ", merchantName='" + merchantName + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
