package com.moa.store.domain.itemInfo.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ItemInfoDto {
    private UUID itemId;
    private String itemName;
    private long quantity;
    private long totalPrice;
}
