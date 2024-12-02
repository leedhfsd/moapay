package com.moa.store.domain.product.service;

import com.moa.store.domain.product.model.Product;
import com.moa.store.domain.product.model.dto.CreateProductRequestDto;
import com.moa.store.domain.product.model.dto.ProductDto;
import com.moa.store.domain.product.model.dto.UpdateProductRequestDto;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProducts(int page, int row);
    Product getProduct(UUID productUuid);
    void deleteProduct(UUID productUuid);
    Product updateProduct(UUID productUuid, UpdateProductRequestDto updateProductRequestDto);
    Product createProduct(CreateProductRequestDto productDto);
}
