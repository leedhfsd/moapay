package com.moa.store.domain.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.moa.store.domain.product.model.dto.UpdateProductRequestDto;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @NotNull
    @Column(length = 100)
    private String name;

    @NotNull
    private long price;

    @NotNull
    @Column(name = "image_url", length = 200)
    private String imageUrl;

    @NotNull
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.uuid = Generators.timeBasedEpochGenerator().generate();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public void changeProductInfo(UpdateProductRequestDto product) {
        this.name = product.getProductName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
    }
}
