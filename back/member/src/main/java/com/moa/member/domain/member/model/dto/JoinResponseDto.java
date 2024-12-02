package com.moa.member.domain.member.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JoinResponseDto {

	private UUID id;
	private String name;
	private String gender; //1,3은  & 2,4는 F
	private LocalDate birthDate;
	private String phoneNumber;
	private String email;
	private String address;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;





}
