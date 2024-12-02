package com.moa.member.domain.member.controller;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moa.member.domain.member.model.Member;
import com.moa.member.domain.member.repository.EmptyCredentialRepository;
import com.moa.member.domain.member.repository.MemberRepository;
import com.moa.member.domain.member.security.JwtTokenProvider;
import com.moa.member.global.exception.BusinessException;
import com.moa.member.global.response.ResultResponse;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.COSEAlgorithmIdentifier;
import com.yubico.webauthn.data.ClientExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialParameters;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.data.PublicKeyCredentialType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/moapay/member/authn/certify")
public class WebAuthnCertifyController {

	private final RelyingParty relyingParty;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public WebAuthnCertifyController(MemberRepository memberRepository, EmptyCredentialRepository credentialRepository,
		JwtTokenProvider jwtTokenProvider) {
		// RelyingParty 설정
		this.relyingParty = RelyingParty.builder()
			.identity(RelyingPartyIdentity.builder()
				.id("moapay-7e24e.web.app")  // 포트 번호를 포함하여 설정
				.name("moapay")    // 서버 이름
				.build())
			.credentialRepository(credentialRepository)  // 빈 CredentialRepository 주입
			.build();
		this.memberRepository = memberRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@GetMapping("/options")
	public PublicKeyCredentialRequestOptions getAuthenticationOptions(HttpServletRequest request, HttpServletResponse response) {
		// JWT 토큰을 사용하여 사용자 정보 가져오기
		String token = jwtTokenProvider.getJwtTokenFromRequestHeader(request);
		String uuid = jwtTokenProvider.getUuidFromToken(token);
		Member member = memberRepository.findByUuid(UUID.fromString(uuid))
				.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));

		// 랜덤 챌린지 생성
		byte[] challengeBytes = new byte[32];
		new SecureRandom().nextBytes(challengeBytes);
		ByteArray challenge = new ByteArray(challengeBytes);

		// 사용자의 등록된 자격 증명 ID를 가져와 허용된 자격 증명 목록에 추가
		List<PublicKeyCredentialDescriptor> allowCredentials = Arrays.asList(
				PublicKeyCredentialDescriptor.builder()
						.id(new ByteArray(member.getCredentialId().getBytes()))  // 실제로 저장된 자격 증명 ID
						.type(PublicKeyCredentialType.PUBLIC_KEY)
						.build()
		);

		// 인증 옵션 생성
		PublicKeyCredentialRequestOptions options = PublicKeyCredentialRequestOptions.builder()
				.challenge(challenge)  // 랜덤 챌린지
				.rpId("moapay-7e24e.web.app")  // 인증 요청을 처리할 서버 도메인
				.allowCredentials(allowCredentials)  // 허용된 자격 증명 (유저의 자격 증명 ID)
				.userVerification(UserVerificationRequirement.PREFERRED)  // 사용자 인증 요구 수준
				.timeout(60000)  // 60초로 설정
				.build();

		// 세션에 인증 요청 옵션 저장
		request.getSession().setAttribute("authnRequestOptions", options);

		// 쿠키 설정
		ResponseCookie jsessionCookie = ResponseCookie.from("JSESSIONID", request.getSession().getId())
				.httpOnly(true)
				.path("/")
				.secure(true)  // HTTPS 환경에서만 동작
				.sameSite("None")  // Cross-site 요청을 허용
				.build();

		response.addHeader("Set-Cookie", jsessionCookie.toString());  // 쿠키를 응답에 추가
		System.out.println(options);
		return options;  // 클라이언트로 인증 옵션 반환
	}

	@PostMapping("/verify")
	public ResponseEntity<ResultResponse> verifyAuthentication(@RequestBody Map<String, Object> responseData, HttpServletRequest request) {
		try {
			// 세션에서 챌린지를 가져옴 (인증에 사용되는 PublicKeyCredentialRequestOptions)
			PublicKeyCredentialRequestOptions options = (PublicKeyCredentialRequestOptions) request.getSession().getAttribute("authnRequestOptions");
			ByteArray challenge = options.getChallenge();

			if (challenge == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(ResultResponse.of(HttpStatus.BAD_REQUEST, "챌린지가 유효하지 않습니다."));
			}

			// 클라이언트로부터 전달받은 데이터에서 자격 증명 ID와 응답 데이터 추출
			String credentialId = (String) responseData.get("id");
			Map<String, Object> response = (Map<String, Object>) responseData.get("response");

			// 필요한 필드 추출 (authenticatorData, clientDataJSON, signature)
			byte[] authenticatorDataBytes = Base64.getUrlDecoder().decode((String)response.get("authenticatorData"));
			byte[] clientDataJSONBytes = Base64.getUrlDecoder().decode((String)response.get("clientDataJSON"));
			byte[] signatureBytes = Base64.getUrlDecoder().decode((String)response.get("signature"));

			// AuthenticatorAssertionResponse 생성
			AuthenticatorAssertionResponse assertionResponse = AuthenticatorAssertionResponse.builder()
					.authenticatorData(new ByteArray(authenticatorDataBytes))
					.clientDataJSON(new ByteArray(clientDataJSONBytes))
					.signature(new ByteArray(signatureBytes))
					.build();

			// 빈 확장 결과 생성
			ClientExtensionOutputs clientExtensions = null;

			// PublicKeyCredential 생성
			PublicKeyCredential credential = PublicKeyCredential.builder()
					.id(new ByteArray(credentialId.getBytes()))
					.response(assertionResponse)
					.clientExtensionResults(clientExtensions)
					.type(PublicKeyCredentialType.PUBLIC_KEY)
					.build();

			// AssertionRequest 생성
			AssertionRequest assertionRequest = AssertionRequest.builder()
					.publicKeyCredentialRequestOptions(options)
					.username("testUser")  // 사용자의 이름
					.build();

			// 서버에서 사용자 인증 정보 검증
			var authenticationResult = relyingParty.finishAssertion(
					FinishAssertionOptions.builder()
							.request(assertionRequest)
							.response(credential)
							.build()
			);

			if (authenticationResult.isSuccess()) {
				ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "인증 성공");
				return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
			} else {
				ResultResponse resultResponse = ResultResponse.of(HttpStatus.UNAUTHORIZED, "인증 실패");
				return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
			}
		} catch (Exception e) {
			log.error("인증 과정에서 오류 발생: ", e);
			ResultResponse resultResponse = ResultResponse.of(HttpStatus.BAD_REQUEST, "인증 오류 발생");
			return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
		}
	}


}
