package com.personal.smartbudgetcraft.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 유효한 자격증명을 제공하지 않고 접근하려 할 때 예외 핸들러 (인가)
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper mapper;

  /**
   * ApiResponse 양식에 맞게 Response 커스텀
   *
   * @param request       request
   * @param response      response
   * @param authException 유효하지 않은 자격 증명 예외
   * @throws IOException 입출력 예외
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json; charset=UTF-8");

    ApiResDto body = ApiResDto.toErrorForm(ErrorCode.ACCESS_AUTH_ENTRY_EXCEPTION.getMessage());

    response.getWriter().write(mapper.writeValueAsString(body));
  }
}