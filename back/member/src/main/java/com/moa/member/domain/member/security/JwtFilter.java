// package com.moa.member.domain.member.security;
//
// import java.io.IOException;
//
// import org.springframework.data.redis.RedisConnectionFailureException;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.filter.OncePerRequestFilter;
//
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.ExpiredJwtException;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @RequiredArgsConstructor
// public class JwtFilter extends OncePerRequestFilter {
//
// 	private final JwtTokenProvider jwtTokenProvider;
//
// 	@Override
// 	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
// 		FilterChain filterChain) throws ServletException, IOException {
// 		String token = jwtTokenProvider.getJwtTokenFromRequestHeader(request);
// 		System.out.println("토큰 값: " + token);
// 		try {
// 			if (token != null) {
// 				if (jwtTokenProvider.validateToken(token) && jwtTokenProvider.isTokenBlacklisted(token)) {
// 					Claims claims = jwtTokenProvider.decodeJwt(token);
// 					if (jwtTokenProvider.hasClaim(claims, "type", "access")) {
// 						Authentication auth = jwtTokenProvider.getAuthentication(token);
// 						SecurityContextHolder.getContext().setAuthentication(auth); // 정상 토큰이면 SecurityContext에 저장
// 						System.out.println("인증 성공: " + auth.getName());  // 인증 성공 로그
// 					} else {
// 						handleRefreshToken(request, response, filterChain, claims);
// 						return;
// 					}
// 				}else{
// 					System.out.println("토큰 검증 실패 또는 토큰이 없음");  // 검증 실패 로그
// 				}
// 			}
// 		} catch (ExpiredJwtException e) {
// 			log.error("token:{}",token);
// 			handleExpiredToken(request, response, token, e);
// 		} catch (RedisConnectionFailureException e) {
// 			SecurityContextHolder.clearContext();
// 		}
// 		filterChain.doFilter(request, response);
//
// 	}
//
// 	private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response,
// 		FilterChain filterChain, Claims claims) throws IOException, ServletException {
// 		String requestURI = request.getRequestURI();
// 		if ("/moapay/member/reissue".equals(requestURI)) {
// 			filterChain.doFilter(request, response);
// 		} else {
// 			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token use.");
// 		}
// 	}
//
// 	private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response,
// 		String token, ExpiredJwtException e) throws IOException {
// 		Claims claims = e.getClaims();
// 		log.error("expired token");
// 		if (claims != null && jwtTokenProvider.hasClaim(claims, "type", "access")) {
// 			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JwtFilter: handleExpiredToken 첫번째");
// 		} else {
// 			log.error("Refresh 토큰이 만료되었습니다.", e);
// 			SecurityContextHolder.clearContext();
// 			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWTFilter: handleExpiredToken 두번째");
// 		}
// 	}
// }
