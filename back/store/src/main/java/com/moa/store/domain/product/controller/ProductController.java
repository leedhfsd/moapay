package com.moa.store.domain.product.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moa.store.domain.product.model.dto.CreateProductRequestDto;
import com.moa.store.domain.product.model.dto.ProductDto;
import com.moa.store.domain.product.model.dto.UpdateProductRequestDto;
import com.moa.store.domain.product.service.ProductService;
import com.moa.store.global.response.ResultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store/product")
public class ProductController {

	private final ProductService productService;

	// 상품 전체 조회, API 명세서 15행
	@GetMapping("/list")
	public ResponseEntity<ResultResponse> getProductsByPaging
			(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int page_row) {
		Page<ProductDto> products = productService.getProducts(page, page_row);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "상품 조회를 완료했습니다.", products.getContent());
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 상품 등록, API 명세서 16행
	@PostMapping
	public ResponseEntity<ResultResponse> createProduct(@RequestBody CreateProductRequestDto createProductRequestDto) {
		ProductDto product = new ProductDto(productService.createProduct(createProductRequestDto));
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.CREATED, "상품 등록을 완료했습니다.", product);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 상품 삭제, API 명세서 17행
	@DeleteMapping("/{uuid}")
	public ResponseEntity<ResultResponse> deleteProduct(@PathVariable UUID uuid) {
		productService.deleteProduct(uuid);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "상품 삭제를 완료했습니다.");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 상품 수정, API 명세서 18행
	@PutMapping("/{uuid}")
	public ResponseEntity<ResultResponse> updateProduct(@PathVariable UUID uuid, @RequestBody UpdateProductRequestDto updateProductRequestDto) {
		ProductDto product = new ProductDto(productService.updateProduct(uuid, updateProductRequestDto));
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "상품 수정을 완료했습니다.", product);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 상품 단건 조회, API 명세서 19행
	@GetMapping("/{uuid}")
	public ResponseEntity<ResultResponse> getProductByUuid(@PathVariable UUID uuid) {
		ProductDto product = new ProductDto(productService.getProduct(uuid));
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "상품 단건 조회를 완료했습니다.", product);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
}
