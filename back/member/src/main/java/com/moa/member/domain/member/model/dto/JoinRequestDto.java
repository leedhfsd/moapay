package com.moa.member.domain.member.model.dto;

import java.time.LocalDate;

import com.moa.member.domain.member.model.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDto {

	private String name;
	private int gender; //1,3은  & 2,4는 F
	private LocalDate birthDate;
	private String phoneNumber;
	private String email;
	private String address;

	public Member toEntity(String password){

		String genderChar;

		if(gender==1 || gender==3){
			genderChar="M";
		}else{
			genderChar="F";
		}

		return Member.builder()
			.name(name)
			.birthDate(birthDate)
			.gender(genderChar)
			.phoneNumber(phoneNumber)
			.password(password)
			.email(email)
			.address(address)
			.build();

	}
}
