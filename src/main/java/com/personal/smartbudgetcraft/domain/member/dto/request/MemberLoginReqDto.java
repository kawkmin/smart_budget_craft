package com.personal.smartbudgetcraft.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 로그인 관련 정보 받는 dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLoginReqDto {

  // 계정 아이디
  private String account;
  // 비밀번호
  private String password;

  /**
   * `UsernamePasswordAuthenticationToken`로 변환
   *
   * @return 로그인 데이터 기반 `UsernamePasswordAuthenticationToken`
   */
  public UsernamePasswordAuthenticationToken toAuthentication() {
    return new UsernamePasswordAuthenticationToken(account, password);
  }
}
