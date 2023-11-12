package com.personal.smartbudgetcraft.global.config.security.filter;

import com.personal.smartbudgetcraft.global.config.security.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Spring Request 앞단에 붙일 Custom Filter 이다.
 * <p>
 * OncePerRequestFilter - 한 번만 실행하는 필터
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final int BEARER_LENGTH = 7;

  private final TokenProvider tokenProvider;

  /**
   * Override doFilterInternal
   * JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
   *
   * @param request     request
   * @param response    response
   * @param filterChain 필터 체인
   * @throws IOException      입력 예외
   * @throws ServletException 서블릿 에외
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws IOException, ServletException {
    // 1. Request Header 에서 토큰을 꺼냄
    String jwt = resolveToken(request);

    // 2. validateToken 으로 토큰 유효성 검사
    // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
    if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
      // 토큰 복호화 기반 Authentication 생성
      Authentication authentication = tokenProvider.getAuthentication(jwt);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 다음 필터로 넘김
    filterChain.doFilter(request, response);
  }

  /**
   * Request Header 에서 토큰 정보를 꺼내오기
   *
   * @param request request
   * @return 정보
   */
  private String resolveToken(HttpServletRequest request) {
    //정보 꺼내기
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    // Bearer로 시작하는 정보가 있으면, 정보 반환
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_LENGTH);
    }

    return null;
  }
}