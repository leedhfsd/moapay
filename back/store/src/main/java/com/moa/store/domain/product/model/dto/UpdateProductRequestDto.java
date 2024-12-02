package com.moa.store.domain.product.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequestDto {
	private String productName;
	private long price;
	private String imageUrl;
}
