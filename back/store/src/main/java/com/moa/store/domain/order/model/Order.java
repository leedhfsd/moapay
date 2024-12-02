package com.moa.store.domain.order.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.moa.store.domain.itemInfo.model.ItemInfo;
import com.moa.store.domain.paymentInfo.model.PaymentInfo;
import com.moa.store.domain.store.model.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id")
	private Store store;

	@NotNull
	@Column(columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
	private UUID uuid;

	@NotNull
	@Column(name = "customer_id", length = 30)
	private String customerId;

	@NotNull
	@Column(name = "total_price")
	private long totalPrice;

	// 주문접수, 결제대기, 결제완료, 주문완료, 주문취소
	@NotNull
	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private OrderStatus state;

	@OneToMany(mappedBy = "order")
	private List<ItemInfo> itemInfos = new ArrayList<>();

	@OneToMany(mappedBy = "order")
	private List<PaymentInfo> paymentInfos;

	@NotNull
	@Column(name = "create_time")
	private LocalDateTime createTime;

	@NotNull
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.uuid = Generators.timeBasedEpochGenerator().generate();
		this.createTime = now;
		this.updateTime = now;
	}

	@PreUpdate
	public void preUpdate() {
		this.updateTime = LocalDateTime.now();
	}

	public void updateState(String state) {
		this.state = OrderStatus.valueOf(state);
	}
}
