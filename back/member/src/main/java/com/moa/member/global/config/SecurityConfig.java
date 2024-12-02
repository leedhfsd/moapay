package com.moa.member.global.config;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.moa.member.domain.member.security.JwtAccessDeniedHandler;
import com.moa.member.domain.member.security.JwtAuthenticationEntryPoint;
//import com.moa.member.domain.member.security.JwtFilter;
//import com.moa.member.domain.member.security.JwtFilter;
import com.moa.member.domain.member.security.JwtTokenProvider;
import com.moa.member.domain.member.security.MemberDetailsServiceImpl;
//import com.moa.member.domain.member.security.MemberDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final MemberDetailsServiceImpl memberDetailsService;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider memberAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(memberDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public SecurityFilterChain memberFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(AbstractHttpConfigurer::disable)
			.authenticationProvider(memberAuthenticationProvider())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//			.cors(Customizer.withDefaults())
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.authorizeRequests()    // 다음 리퀘스트에 대한 사용권한 체크
			.requestMatchers("/**")
			.permitAll() // 모든 주소 허용
			//.requestMatchers("/moapay/member/login","/moapay/member/join","/moapay/member/sendSMS","/moapay/member/verification","/moapay/member/isMember").permitAll() // 허용된 주소
			//.anyRequest().authenticated() // Authentication 필요한 주소
			.and()
			.exceptionHandling(
				(exceptionHandling) -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
					.accessDeniedHandler(jwtAccessDeniedHandler));
		//.addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
//
//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration config = new CorsConfiguration();
//
//		config.setAllowCredentials(true);
//		config.setAllowedOrigins(
//			List.of("https://localhost:8765", "http://localhost:8765", "https://localhost", "http://localhost",
//				"http://localhost:5173", "https://localhost:5173", "https://moapay-7e24e.web.app",
//				"https://j11c201.p.ssafy.io", "https://j11c201.p.ssafy.io/api", "http://j11c201.p.ssafy.io"));
//		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//		config.setAllowedHeaders(List.of("*"));
//		config.setExposedHeaders(List.of("*"));
//
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", config);
//		return source;
//	}
}