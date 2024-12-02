package com.moa.member.domain.member.controller;

import com.moa.member.domain.member.security.JwtTokenProvider;
import com.moa.member.global.exception.BusinessException;
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moa.member.domain.member.model.Member;
import com.moa.member.domain.member.repository.EmptyCredentialRepository;
import com.moa.member.domain.member.repository.MemberRepository;
import com.moa.member.domain.member.security.JwtTokenProvider;
import com.moa.member.global.response.ResultResponse;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorResponse;
import com.yubico.webauthn.data.ClientExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialType; // PublicKeyCredentialType 임포트
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.COSEAlgorithmIdentifier;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialEntity;
import com.yubico.webauthn.data.PublicKeyCredentialParameters;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

@RestController
@Slf4j
@RequestMapping("/moapay/member/authn/register")
public class WebAuthnRegisterController {

	private final RelyingParty relyingParty;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public WebAuthnRegisterController(MemberRepository memberRepository,
		EmptyCredentialRepository credentialRepository, JwtTokenProvider jwtTokenProvider) {
		//////////////////////////////////꼭 !!!!!!!!!!!!!!!! id 값 서버 주소로 변경하기 ////////////////////////////////
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
	public PublicKeyCredentialCreationOptions getRegistrationOptions(
		HttpServletRequest request,
		HttpServletResponse response) {  // HttpServletResponse 추가
		String token = jwtTokenProvider.getJwtTokenFromRequestHeader(request);
		String uuid = jwtTokenProvider.getUuidFromToken(token);
		Member member = memberRepository.findByUuid(UUID.fromString(uuid))
			.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));

		// UserIdentity 생성
		UserIdentity userEntity = UserIdentity.builder()
			.name(member.getName())
			.displayName("moapay")
			.id(new ByteArray(member.getUuid().toString().getBytes()))
			.build();

		// 랜덤 챌린지 생성
		byte[] challengeBytes = new byte[32];
		new SecureRandom().nextBytes(challengeBytes);
		ByteArray challenge = new ByteArray(challengeBytes);

		PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.builder()
				.rp(RelyingPartyIdentity.builder()
						.id("moapay-7e24e.web.app")
						.name("moapay")
						.build())
				.user(userEntity)
				.challenge(challenge)
				.pubKeyCredParams(Arrays.asList(
						PublicKeyCredentialParameters.builder()
								.alg(COSEAlgorithmIdentifier.ES256)
								.type(PublicKeyCredentialType.PUBLIC_KEY)
								.build(),
						PublicKeyCredentialParameters.builder()
								.alg(COSEAlgorithmIdentifier.RS256)
								.type(PublicKeyCredentialType.PUBLIC_KEY)
								.build()
				))
				.authenticatorSelection(AuthenticatorSelectionCriteria.builder()
						.userVerification(UserVerificationRequirement.REQUIRED)
						.build())
				.attestation(AttestationConveyancePreference.INDIRECT)  // 필요시 증명 요구
				.build();


		// 쿠키 설정
		ResponseCookie jsessionCookie = ResponseCookie.from("JSESSIONID", request.getSession().getId())
			.httpOnly(true)
			.path("/")
			.secure(true)  // HTTPS 환경에서만 동작
			.sameSite("None")  // Cross-site 요청을 허용
			.build();

		response.addHeader("Set-Cookie", jsessionCookie.toString());  // 쿠키를 응답에 추가

		// 클라이언트에게 옵션 전송 (세션에 저장 가능)
		request.getSession().setAttribute("registrationOptions", options);
		PublicKeyCredentialCreationOptions save = (PublicKeyCredentialCreationOptions)request.getSession()
			.getAttribute("registrationOptions");

		return options;
	}

	@PostMapping("/verify")
	public ResponseEntity<ResultResponse> verifyRegistration(@RequestBody Map<String, Object> responseData,
		HttpServletRequest request) {
		try {
			String token = jwtTokenProvider.getJwtTokenFromRequestHeader(request);
			String uuid = jwtTokenProvider.getUuidFromToken(token);
			Member member = memberRepository.findByUuid(UUID.fromString(uuid))
				.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."));

			HttpSession session = request.getSession(false);


			// 세션에서 등록 옵션을 가져옴
			PublicKeyCredentialCreationOptions options = (PublicKeyCredentialCreationOptions)request.getSession()
				.getAttribute("registrationOptions");


			// 클라이언트로부터 전달받은 데이터에서 자격 증명 ID와 응답 데이터 추출
			String credentialId = (String)responseData.get("id");
			System.out.println("credentailId");
			System.out.println(credentialId);
			Map<String, Object> response = (Map<String, Object>)responseData.get("response");

			// AuthenticatorResponse는 별도의 형태로 변환해야 함 (AttestationObject와 ClientDataJSON 필요)
			byte[] attestationObjectBytes = Base64.getUrlDecoder().decode((String)response.get("attestationObject"));
			System.out.println("Attestation Object Size: " + attestationObjectBytes.length);  // 데이터 크기 출력
			byte[] clientDataJSONBytes = Base64.getUrlDecoder().decode((String)response.get("clientDataJSON"));

			// ByteArray 객체로 변환
			ByteArray attestationObject = new ByteArray(attestationObjectBytes);
			ByteArray clientDataJSON = new ByteArray(clientDataJSONBytes);

			// AuthenticatorAttestationResponse 생성
			AuthenticatorAttestationResponse attestationResponse = AuthenticatorAttestationResponse.builder()
				.attestationObject(attestationObject)
				.clientDataJSON(clientDataJSON)
				.build();

			// 빈 확장 결과 생성
			ClientRegistrationExtensionOutputs clientExtensions = ClientRegistrationExtensionOutputs.builder()
				.build();  // 빈 확장 객체

			// PublicKeyCredential 생성
			PublicKeyCredential credential = PublicKeyCredential.builder()
				.id(new ByteArray(credentialId.getBytes()))  // 자격 증명 ID
				.response(attestationResponse)  // 생성된 AuthenticatorAttestationResponse 객체
				.clientExtensionResults(clientExtensions)  // 빈 확장 객체 전달
				.type(PublicKeyCredentialType.PUBLIC_KEY)  // 자격 증명 타입
				.build();

			// 등록 검증 완료
			var registrationResult = relyingParty.finishRegistration(
				FinishRegistrationOptions.builder()
					.request(options)
					.response(credential)
					.build()
			);

			// Member 정보 업데이트 (기존 데이터 유지)
			Member updatedMember = Member.builder()
				.id(member.getId())
				.name(member.getName())
				.birthDate(member.getBirthDate())
				.gender(member.getGender())
				.phoneNumber(member.getPhoneNumber())
				.email(member.getEmail())
				.address(member.getAddress())
				.uuid(member.getUuid())  // UUID 유지
				.createTime(member.getCreateTime())
				.updateTime(member.getUpdateTime())
				.publicKey(registrationResult.getKeyId().getId().toString())  // 공개키 저장
				.credentialId(credentialId)  // 자격 증명 ID 저장
				.authenticatorData(attestationObject.getBytes())  // AttestationObject 저장
				.password(member.getPassword())
				.build();

			// Member 정보 저장
			memberRepository.save(updatedMember);

			ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "검증 성공: 일치");
			return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
		} catch (Exception e) {
			log.error("등록 검증 실패: ", e);
			ResultResponse resultResponse = ResultResponse.of(HttpStatus.BAD_REQUEST, "검증 실패: 불일치");
			return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
		}
	}



}

