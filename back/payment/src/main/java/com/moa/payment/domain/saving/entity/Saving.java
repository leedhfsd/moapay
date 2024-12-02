package com.moa.payment.domain.saving.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="saving")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Saving {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name="member_id", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
	private UUID memberId;

	@NotNull
	@Column(name="amount")
	private Long amount;

	@NotNull
	@Column(name="limit_amount")
	private Long limitAmount;

	@Column(name="today_amount")
	private Long todayAmount;

	@NotNull
	@Column(name="daily")
	private String daily;

	public void updateLimit(Long limitAmount){
		this.limitAmount=limitAmount;
	}

	public void updateDaily(Long todayAmount){
		this.daily= daily+todayAmount+",";
	}

	public void resetTodayAmount(){
		this.todayAmount=0L;
	}

	public void updateTodayAmount(long amount){
		this.todayAmount+=amount;
	}

	public void updateAmount(long amount){
		this.amount+=amount;
	}

	public void resetSaving(){
		this.todayAmount=0L;
		this.amount = 0L;
		this.daily="";
	}
}
