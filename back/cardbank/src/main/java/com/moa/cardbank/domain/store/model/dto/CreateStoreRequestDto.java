package com.moa.cardbank.domain.store.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateStoreRequestDto {
    private String name;
    private String accountNumber;
    private String categoryId;
}
