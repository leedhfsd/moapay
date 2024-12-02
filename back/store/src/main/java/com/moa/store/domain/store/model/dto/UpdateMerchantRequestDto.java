package com.moa.store.domain.store.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMerchantRequestDto {
	private String merchantName;
	private String categoryId;
	private String categoryName;
	private String merchantUrl;
}
