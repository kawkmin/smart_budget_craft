package com.personal.smartbudgetcraft.global.config.security.annotation;

import com.personal.smartbudgetcraft.domain.member.dao.MemberRepository;
import com.personal.smartbudgetcraft.global.config.security.TokenProvider;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * JWT 토큰으로, 회원의 Entity 정보를 가져오는 어노테이션 설정
 */
@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

  private static final String AUTHORIZATION_HEADER = "authorization";

  private final MemberRepository memberRepository;
  private final TokenProvider tokenProvider;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LoginMember.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory
  ) {
    HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

    // 헤더의 token 정보
    String token = request.getHeader(AUTHORIZATION_HEADER);
    if (token == null) {
      throw new BusinessException(null, null, ErrorCode.ACCESS_DENIED_EXCEPTION);
    }

    Long memberId = tokenProvider.getIdFromToken(token);

    return memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(memberId, "JWT", ErrorCode.MEMBER_NOT_FOUND));
  }
}