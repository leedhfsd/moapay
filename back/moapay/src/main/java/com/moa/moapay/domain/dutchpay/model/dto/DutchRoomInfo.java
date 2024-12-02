package com.moa.moapay.domain.dutchpay.model.dto;

import com.moa.moapay.domain.dutchpay.entity.DutchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DutchRoomInfo {
    // 더치페이 정보
    private UUID dutchUuid;
    private long memberCnt;
    // 가맹점, 상품 정보
    private DutchStatus statusRoom;
    private UUID orderId;
    private UUID merchantId;
    private String merchantName;
    private String categoryId;
    private long totalPrice;

    // 더치페이 리스트
    List<DutchPayDto> dutchPayList;

    @Override
    public String toString() {
        return "DutchRoomInfoDto{" +
                "DutchUuid=" + dutchUuid +
                ", memberCnt=" + memberCnt +
                ", orderId=" + orderId +
                ", merchantId=" + merchantId +
                ", merchantName='" + merchantName + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
