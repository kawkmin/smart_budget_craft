package com.personal.smartbudgetcraft.global.config.security.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 정보 Dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {

  // 권한 부여 코드 승인 타입
  private String grantType;
  // Access 토큰
  private String accessToken;
  // Refresh 토큰
  private String refreshToken;
  // Access 토큰 만료 시간
  private Long accessTokenExpiresIn;
}