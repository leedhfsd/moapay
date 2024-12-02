package com.moa.member.domain.member.security;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

	private final RedisTemplate<String, String> redisTemplate;
	//private final MemberDetailsServiceImpl memberDetailsService;

	@Value("${spring.jwt.token.access-expiration-time}")
	private long accessExpirationTime;

	@Value("${spring.jwt.token.refresh-expiration-time}")
	private long refreshExpirationTime;

	private SecretKey secretKey;

	@Value("${spring.jwt.secret}")
	private String secretKeyString; // 비밀 키 문자열

	@PostConstruct
	protected void init() {
		// 비밀 키 문자열이 Base64로 인코딩된 것이 아니라면, 이를 사용하여 SecretKey 생성
		// 비밀 키는 최소 512비트(64바이트) 이상이어야 합니다.
		if (secretKeyString.length() < 64) {
			throw new IllegalArgumentException("비밀 키는 최소 512비트(64바이트)이어야 합니다.");
		}

		// 비밀 키를 안전하게 생성
		secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
	}

	public String generateAccessToken(Authentication authentication) {
		MemberPrincipalDetails userDetails = (MemberPrincipalDetails) authentication.getPrincipal();
		Claims claims = Jwts.claims().setSubject(userDetails.getMember().getUuid().toString()); //email 역할 = uuid
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + accessExpirationTime);

		return Jwts.builder()
			.setClaims(claims)
			.claim("uuid", userDetails.getMember().getUuid().toString())
			.claim("type", "access")
			.setIssuedAt(now)
			.setExpiration(expireDate)
			.signWith(SignatureAlgorithm.HS512, secretKey)
			.compact();
	}

	public String generateRefreshToken(Authentication authentication) {
		MemberPrincipalDetails userDetails = (MemberPrincipalDetails) authentication.getPrincipal();
		Claims claims = Jwts.claims().setSubject(userDetails.getMember().getUuid().toString()); //email 역할 = uuid
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + refreshExpirationTime);

		String refreshToken = Jwts.builder()
			.setClaims(claims)
			.claim("uuid", userDetails.getMember().getUuid().toString())
			.setIssuedAt(now)
			.setExpiration(expireDate)
			.signWith(SignatureAlgorithm.HS512, secretKey)
			.compact();
		String key = "refresh-token:" + userDetails.getMember().getUuid().toString();

		//redis에 저장
		redisTemplate.opsForValue().set(
			key,
			refreshToken,
			refreshExpirationTime,
			TimeUnit.MILLISECONDS
		);
		return refreshToken;
	}

	public Claims decodeJwt(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}


	//header에서 Access Bearer 토큰 가져오기
	public String getJwtTokenFromRequestHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String getRefreshTokenByUuid(String uuid) {
		// Redis에서 리프레시 토큰 조회
		return redisTemplate.opsForValue().get("refresh-token:" + uuid);
	}

	// 토큰에서 Uuid 가져오기. String 형태로.
	public String getUuidFromToken(String token) {
		return decodeJwt(token).get("uuid", String.class);
	}
}
