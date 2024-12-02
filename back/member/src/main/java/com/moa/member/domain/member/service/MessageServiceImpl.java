package com.moa.member.domain.member.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import com.moa.member.domain.member.repository.MessageRepository;
import com.moa.member.global.exception.BusinessException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@PropertySource("classpath:coolsms.properties")
public class MessageServiceImpl implements MessageService{

	@Value("${coolsms.apikey}")
	private String apiKey;

	@Value("${coolsms.apisecret}")
	private String apiSecret;

	@Value("${coolsms.fromnumber}")
	private String fromNumber;

	@Autowired
	MessageRepository messageRepository;
	DefaultMessageService defaultMessageService;

	public MessageServiceImpl(DefaultMessageService defaultMessageService) {
		this.defaultMessageService = defaultMessageService;
	}


	// 빈 초기화 후에 NurigoApp을 초기화하는 메서드
	@PostConstruct
	public void initializeMessageService() {
		this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
	}

	/**
	 * 6자리 랜덤 인증번호 생성
	 * @return 인증번호 6자리
	 */
	@Override
	public String generateCode(){
		Random random=new Random();
		int code=random.nextInt(888888)+111111;
		return Integer.toString(code);
	}

	/**
	 * 문자가 올 메세지 내용을 생성
	 * @param code 인증번호 6자리
	 * @return 인증번호 6자리가 포함된 메세지
	 */
	@Override
	public String generateMessage(String code){
		final String provider="모아페이";
		return "["+provider+"]\n"+"본인확인 인증번호 ["+code+"] 를 입력해주세요.";
	}

	@Override
	public SingleMessageSentResponse sendSMS(String to){

		String code=generateCode();
		String text=generateMessage(code);

		Message message=new Message();
		message.setFrom(fromNumber);
		message.setTo(to);
		message.setText(text);

		//저장된 인증번호가 남아있다면 삭제하고 다시 저장해야한다
		if(messageRepository.hasKey(to)){
			messageRepository.deleteSmsCertification(to);
		}
		//db에 발송한 번호와 코드 저장
		messageRepository.createSmsCertification(to,code);

		SingleMessageSentResponse response = defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
		System.out.println(response);

		return response;

	}

	@Override
	public boolean verifySMS(String to, String code){
		boolean pass=(messageRepository.hasKey(to) && messageRepository.getSmsCertification(to).equals(code));

		if(!pass){
			throw new BusinessException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다.");
		}
		//인증완료하면 인증번호 redis에서 삭제
		messageRepository.deleteSmsCertification(to);

		return true;
	}

}
