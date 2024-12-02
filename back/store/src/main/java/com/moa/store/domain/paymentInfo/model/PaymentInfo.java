package com.moa.store.domain.paymentInfo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.moa.store.domain.order.model.Order;

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
import jakarta.persistence.PrePersist;
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
public class PaymentInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
	private UUID uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@NotNull
	@Column(columnDefinition = "char(16)", name = "card_number")
	private String cardNumber;

	@NotNull
	private long amount;

	@NotNull
	@Column(name = "actual_amount")
	private long actualAmount;

	@Enumerated(EnumType.STRING)
	private ProcessingStatus status;

	@NotNull
	private LocalDateTime paymentTime;

	@PrePersist
	public void prePersist() {
		this.uuid = Generators.timeBasedEpochGenerator().generate();
		this.paymentTime = LocalDateTime.now();
	}
}
