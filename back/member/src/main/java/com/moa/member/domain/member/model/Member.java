package com.moa.member.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import com.fasterxml.uuid.Generators;
import com.moa.member.global.exception.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatus;

@Entity
@Table(name="member")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@NotNull
	@Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
	private UUID uuid; // 고유 id는 수정 불가

	@NotNull
	@Column(name = "name", length = 10, nullable=false)
	private String name;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name="birth_date",nullable=false)
	private LocalDate birthDate;

	@Column(name="gender",nullable=false)
	private String gender;

	@NotNull
	@Column(name="phone_number",nullable=false)
	private String phoneNumber;

	@NotNull
	@Column(name="password",nullable=false)
	private String password; //전화번호를 암호화 한 비밀번호

	@Column(name="simple_password")
	private String simplePassword;

	@Temporal(TemporalType.TIMESTAMP) //날짜와 시간 저장
	@Column(name="create_time",nullable=false)
	private LocalDateTime createTime;

	@Temporal(TemporalType.TIMESTAMP) //날짜와 시간 저장
	@Column(name="update_time",nullable=false)
	private LocalDateTime updateTime;

	@Column(name="email",nullable=false)
	private String email;

	@Column(name="address",nullable=false)
	private String address;

	@NotNull
	@Column(name = "public_key", nullable = true)
	private String publicKey; //공개키

	@NotNull
	@Column(name = "credential_id", nullable = true)
	private String credentialId; //WebAuthn 장치의 Credential ID

	@Lob
	@NotNull
	@Column(name = "authenticator_data", nullable = true)
	private byte[] authenticatorData; //인증 장치 데이터


	@Enumerated(EnumType.STRING)
	@Column(name="payment_type")
	private PaymentType paymentType;


	@PrePersist
	private void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.uuid = Generators.timeBasedGenerator().generate();
		this.createTime = now;
		this.updateTime = now;
	}

	@PreUpdate
	private void preUpdate() {
		this.updateTime = LocalDateTime.now();
	}

	// Builder를 통한 공개키 및 관련 정보 설정 메서드 추가
	public static MemberBuilder builderWithPublicKey(String publicKey, String credentialId, byte[] authenticatorData) {
		return Member.builder()
			.publicKey(publicKey)
			.credentialId(credentialId)
			.authenticatorData(authenticatorData);
	}

	public void updateSimplePassword(String simplePassword) {
		this.simplePassword = simplePassword;
	}

	public void updatePaymentType(String type) {
		if("benefit".equals(type)){
			this.paymentType=PaymentType.BENEFIT;
		}else if("perform".equals(type)){
			this.paymentType=PaymentType.PERFORM;
		}else{
			throw new BusinessException(HttpStatus.BAD_REQUEST,"존재하지 않는 결제 타입입니다.");
		}
	}




}
