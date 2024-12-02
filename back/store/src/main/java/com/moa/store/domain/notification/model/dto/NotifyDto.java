package com.moa.store.domain.notification.model.dto;

import com.moa.store.domain.notification.model.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyDto {
    private UUID orderId;
    private ResultStatus status; // SUCCEED, FAILED
}
