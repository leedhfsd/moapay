package com.moa.payment.domain.statistics.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StatisticsRequestDto {
    private UUID memberId;
}
