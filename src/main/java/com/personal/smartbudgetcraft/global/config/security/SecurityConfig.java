package com.personal.smartbudgetcraft.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 시큐리티에 대한 전반적인 설정
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  /**
   * 비밀번호 암호화 방식을 `BCrypt`로 설정
   *
   * @return BCrypt 빈 등록
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 시큐리티 필터 설정
   *
   * @param http http 시큐리티
   * @return 커스텀 http 시큐리티
   * @throws Exception 모든 예외
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    return http
        .httpBasic(AbstractHttpConfigurer::disable) // Http basic Auth 기반 로그인 인증창 사용 X
        .csrf(AbstractHttpConfigurer::disable) // Rest api 이므로, csrf 보안 사용 X
        .cors(AbstractHttpConfigurer::disable) // cors 보안 사용 X
        .formLogin(AbstractHttpConfigurer::disable) // 로그인 폼 화면 사용 X
        
        .build();
  }
}