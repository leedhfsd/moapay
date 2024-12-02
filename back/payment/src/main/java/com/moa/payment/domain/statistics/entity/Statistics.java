package com.moa.payment.domain.statistics.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "statistics")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Statistics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
	private UUID uuid;

	@NotNull
	@Column(name = "month", nullable = false)
	private int month;

	@NotNull
	@Column(name = "year", nullable = false)
	private int year;

	@NotNull
	@Column(name = "member_id", columnDefinition= "binary(16)", nullable = false)
	private UUID memberId;

	@NotNull
	@Column(name = "category_id", columnDefinition = "char(5)", nullable = false)
	private String categoryId;

	@NotNull
	@Column(name = "total_amount", nullable = false)
	private long totalAmount;

	@NotNull
	@Column(name = "total_benefit_balance", nullable = false)
	private long totalBenefitBalance;

	@NotNull
	@Column(name = "create_time")
	private LocalDateTime createTime;

	@NotNull
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@PrePersist
	private void prePersist() {
		this.uuid = Generators.timeBasedEpochGenerator().generate();
		LocalDateTime now = LocalDateTime.now();
		this.createTime = now;
		this.updateTime = now;
	}
	@PreUpdate
	private void preUpdate() {
		this.updateTime = LocalDateTime.now();
	}

}
