package com.moa.store.domain.store.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moa.store.domain.store.model.Store;
import com.moa.store.domain.store.model.dto.CreateMerchantRequestDto;
import com.moa.store.domain.store.model.dto.MerchantDto;
import com.moa.store.domain.store.model.dto.UpdateMerchantRequestDto;
import com.moa.store.domain.store.repository.StoreRepository;
import com.moa.store.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StoreServiceImpl implements StoreService{

	private final StoreRepository storeRepository;

	@Override
	@Transactional
	public Store createStore(CreateMerchantRequestDto createMerchantRequestDto) {
		Store store = Store.builder()
			.name(createMerchantRequestDto.getMerchantName())
			.categoryId(createMerchantRequestDto.getCategoryId())
			.categoryName(createMerchantRequestDto.getCategoryName())
			.adminId(createMerchantRequestDto.getAdminId())
			.adminPassword(createMerchantRequestDto.getAdminPassword())
			.merchantUrl(createMerchantRequestDto.getMerchantUrl())
			.build();
		return storeRepository.save(store);
	}

	@Override
	@Transactional
	public Store updateStore(UUID merchantUuid, UpdateMerchantRequestDto updateMerchantRequestDto) {
		Store searchedStore = storeRepository.findByUuid(merchantUuid)
			.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "UUID를 확인해주세요."));
		searchedStore.updateStoreInfo(updateMerchantRequestDto);
		return searchedStore;
	}

	@Override
	public Store getStore(UUID storeUuid) {
		return storeRepository.findByUuid(storeUuid)
			.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "UUID를 확인해주세요."));
	}

	@Override
	@Transactional
	public void deleteStore(UUID storeUuid) {
		if (!storeRepository.existsByUuid(storeUuid)) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "UUID를 확인해주세요.");
		}
		storeRepository.deleteByUuid(storeUuid);
	}

	@Override
	public List<MerchantDto> getStores() {
		return storeRepository.findAll().stream().map(MerchantDto::new).toList();
	}
}
