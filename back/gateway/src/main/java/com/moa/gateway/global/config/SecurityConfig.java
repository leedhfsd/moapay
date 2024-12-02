package com.moa.gateway.global.config;

import com.moa.gateway.global.exception.BusinessException;
import com.moa.gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    private AuthenticationWebFilter authenticationWebFilter() {
        ReactiveAuthenticationManager authenticationManager = Mono::just;

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        return authenticationWebFilter;
    }

    private ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> {
            String token = jwtUtil.resolveToken(exchange.getRequest());
            try {
                if (!Objects.isNull(token) && jwtUtil.validateJwtToken(token)) {
                    return Mono.justOrEmpty(jwtUtil.getAuthentication(token));
                }
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
            }
            return Mono.empty();
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(Customizer.withDefaults()) // CORS 설정
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //session stateless 설정
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화
                .logout(logout -> logout.disable()) // 로그아웃 비활성화
                .authorizeExchange(exchanges -> exchanges.pathMatchers(
                                        "/api/moapay/member/login", "/moapay/member/login",
                                        "/api/moapay/member/join", "/moapay/member/join",
                                        "/api/moapay/member/sendSMS", "/moapay/member/sendSMS",
                                        "/api/moapay/member/verification", "/moapay/member/verification",
                                        "/api/moapay/member/isMember", "/moapay/member/isMember",
                                        "/api/moapay/core/code/QRcode", "/moapay/core/code/QRcode",
                                        "/api/moapay/pay/analysis/history", "/moapay/pay/analysis/history",
                                        "/api/moapay/core/generalpay/offline", "/moapay/core/generalpay/offline",
                                        "/api/moapay/pay/notification/subscribe/**", "/moapay/pay/notification/subscribe/**",
                                        "/api/moapay/core/card/getMyCardIds", "/moapay/core/card/getMyCardIds",
                                        "/api/moapay/pay/charge/getPaymentLog", "/moapay/pay/charge/getPaymentLog",
                                        "/api/moapay/member/getMember", "/moapay/member/getMember",
                                        "/api/moapay/pay/analysis/saveAverage", "/moapay/pay/analysis/saveAverage",
                                        "/api/moapay/member/getMember", "/api/moapay/core/card/getMemberId",
                                        "/api/moapay/core/card/getMemberCard", "/moapay/member/getMember",
                                        "/moapay/core/card/getMemberId", "/moapay/core/card/getMemberCard",
                                        "/moapay/core/ws/dutchpay", "/moapay/core/dutchpay/createRoom",
                                        "/api/moapay/core/ws/dutchpay", "/api/moapay/core/dutchpay/createRoom",
                                        "/ws/**"
                                )
                                .permitAll()
                                .anyExchange()
                                .authenticated() //나머지 경로는 인증 필요
                ).addFilterBefore(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION).build();
    }

//    // CorsConfigurationSource 메서드 (SecurityConfig 클래스 내부에 추가)
//    @Bean
//    public org.springframework.web.cors.reactive.CorsConfigurationSource corsConfigurationSource() {
//        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source =
//                new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(Arrays.asList(
//                "https://localhost:8765", "http://localhost:8765", "https://localhost", "http://localhost",
//                "http://localhost:5173", "https://localhost:5173", "https://moapay-7e24e.web.app",
//                "https://j11c201.p.ssafy.io", "https://j11c201.p.ssafy.io/api", "http://j11c201.p.ssafy.io"
//        ));
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//        config.setAllowedHeaders(Arrays.asList("*"));
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }

}