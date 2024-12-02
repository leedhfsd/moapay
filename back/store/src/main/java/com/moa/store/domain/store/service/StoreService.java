package com.moa.store.domain.store.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import com.moa.store.domain.store.model.Store;
import com.moa.store.domain.store.model.dto.CreateMerchantRequestDto;
import com.moa.store.domain.store.model.dto.MerchantDto;
import com.moa.store.domain.store.model.dto.UpdateMerchantRequestDto;

public interface StoreService {
	Store createStore(CreateMerchantRequestDto createMerchantRequestDto);
	Store updateStore(UUID merchantUuid, UpdateMerchantRequestDto updateMerchantRequestDto);
	Store getStore(UUID storeUuid);
	void deleteStore(UUID storeUuid);
	List<MerchantDto> getStores();
}

