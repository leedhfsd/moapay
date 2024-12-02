package com.moa.member.domain.member.service;

import org.springframework.stereotype.Service;

public interface SimpleService {
	void register(String uuid, String simplePassword);
	void verify(String uuid, String simplePassword);
}
