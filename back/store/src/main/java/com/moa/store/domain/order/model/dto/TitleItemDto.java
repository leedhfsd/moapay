package com.moa.store.domain.order.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TitleItemDto {
    private UUID itemId;
    private String itemName;
}
