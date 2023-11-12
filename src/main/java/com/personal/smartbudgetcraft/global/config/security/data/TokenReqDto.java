package com.personal.smartbudgetcraft.global.config.security.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenReqDto {

  private String accessToken;
  private String refreshToken;
}