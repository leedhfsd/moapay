package com.moa.store.domain.product.service;

import com.moa.store.domain.product.model.Product;
import com.moa.store.domain.product.model.dto.CreateProductRequestDto;
import com.moa.store.domain.product.model.dto.ProductDto;
import com.moa.store.domain.product.model.dto.UpdateProductRequestDto;
import com.moa.store.domain.product.repository.ProductRepository;
import com.moa.store.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public Page<ProductDto> getProducts(int page, int row) {
        Pageable pageable = PageRequest.of(page, row, Sort.by("createTime").descending());
        return productRepository.findAll(pageable).map(ProductDto::new);
    }

    @Override
    public Product getProduct(UUID productUuid) {
        return productRepository.findByUuid(productUuid)
            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "UUID를 확인해주세요"));
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productUuid) {
        if (!productRepository.existsByUuid(productUuid)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "UUID를 확인해주세요.");
        }
        productRepository.deleteByUuid(productUuid);
    }

    @Override
    @Transactional
    public Product updateProduct(UUID productUuid, UpdateProductRequestDto updateProductRequestDto) {
        Product searchedProduct = productRepository.findByUuid(productUuid)
            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "UUID를 확인해주세요."));
        searchedProduct.changeProductInfo(updateProductRequestDto);
        return searchedProduct;
    }

    @Override
    @Transactional
    public Product createProduct(CreateProductRequestDto productDto) {
        Product product = Product.builder()
            .name(productDto.getProductName())
            .price(productDto.getPrice())
            .imageUrl(productDto.getImageUrl())
            .build();
        return productRepository.save(product);
    }
}
