package com.moa.gateway.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebFluxConfigurer {

//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//        .allowedOrigins("https://localhost:8765", "http://localhost:8765", "https://localhost", "http://localhost",
//            "http://localhost:5173", "https://localhost:5173", "https://moapay-7e24e.web.app",
//            "https://j11c201.p.ssafy.io", "https://j11c201.p.ssafy.io/api", "http://j11c201.p.ssafy.io")
//        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//        .allowedHeaders("*")
//        .allowCredentials(true)
//        .maxAge(3600);
//  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
//    corsConfig.setAllowedOrigins(Arrays.asList("https://localhost:8765", "http://localhost:8765", "https://localhost", "http://localhost",
//        "http://localhost:5173", "https://localhost:5173", "https://moapay-7e24e.web.app",
//        "https://j11c201.p.ssafy.io", "https://j11c201.p.ssafy.io/api", "http://j11c201.p.ssafy.io"));
    corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
    corsConfig.setMaxAge(3600L);
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    corsConfig.setAllowedHeaders(Arrays.asList("*"));
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }
}
