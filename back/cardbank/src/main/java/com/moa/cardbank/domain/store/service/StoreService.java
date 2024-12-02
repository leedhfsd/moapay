package com.moa.cardbank.domain.store.service;

import com.moa.cardbank.domain.store.model.dto.CreateStoreRequestDto;
import com.moa.cardbank.domain.store.model.dto.CreateStoreResponseDto;

public interface StoreService {
    CreateStoreResponseDto createStore(CreateStoreRequestDto dto);
}
