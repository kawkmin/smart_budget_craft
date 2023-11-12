package com.personal.smartbudgetcraft.domain.auth.application.security;

import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 비밀번호를 확인할 때, 원하는 예외 처리를 위해서 커스텀
 */
@Service
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final CustomUserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  /**
   * 아이디 비번 등 검증
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // 회원 아이디 얻기
    String userId = authentication.getName();
    // 회원 비밀번호 얻기
    String userPassword = (String) authentication.getCredentials();

    // 아이디로 회원 찾기
    UserDetails member = userDetailsService.loadUserByUsername(userId);

    // 비밀번호 확인
    if (!passwordEncoder.matches(userPassword, member.getPassword())) {
      throw new BusinessException(null, "password", ErrorCode.MEMBER_PASSWORD_BAD_REQUEST);
    }

    // 다시 만들어서 반환
    Authentication newAuth = new UsernamePasswordAuthenticationToken(
        userId, userPassword, member.getAuthorities()
    );

    return newAuth;
  }

  /**
   * 타입 체크
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
