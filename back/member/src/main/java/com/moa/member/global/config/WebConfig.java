package com.moa.member.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
				.allowedOriginPatterns("https://moapay-7e24e.web.app", "https://j11c201.p.ssafy.io",
						"j11c201.p.ssafy.io", "http://j11c201.p.ssafy.io","http://localhost:5173") // 허용할 프론트엔드 URL들
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
				.allowedHeaders("*") // 허용할 헤더
				.allowCredentials(true) // 쿠키 인증 요청 허용 여부
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials") // 클라이언트에서 접근 가능한 헤더 추가
				.maxAge(3600); // 프리플라이트 요청 캐시 시간 설정 (선택)
	}
}