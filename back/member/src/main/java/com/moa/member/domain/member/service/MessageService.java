package com.moa.member.domain.member.service;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface MessageService {
	String generateCode();
	String generateMessage(String code);
	SingleMessageSentResponse sendSMS(String to);
	boolean verifySMS(String to, String code);
}
