package com.moa.member.domain.member.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository {
	void createSmsCertification(String toNumber, String code);
	String getSmsCertification(String toNumber);
	void deleteSmsCertification(String toNumber);
	boolean hasKey(String toNumber);
}
