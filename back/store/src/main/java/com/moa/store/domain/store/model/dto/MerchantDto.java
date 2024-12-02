package com.moa.store.domain.store.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.moa.store.domain.store.model.Store;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantDto {
	private UUID merchantId;
	private String merchantName;
	private String categoryId;
	private String categoryName;
	private String adminId;
	private String merchantUrl;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;

	public MerchantDto(Store store) {
		this.merchantId = store.getUuid();
		this.merchantName = store.getName();
		this.categoryId = store.getCategoryId();
		this.categoryName = store.getCategoryName();
		this.adminId = store.getAdminId();
		this.merchantUrl = store.getMerchantUrl();
		this.createTime = store.getCreateTime();
		this.updateTime = store.getUpdateTime();
	}
}
