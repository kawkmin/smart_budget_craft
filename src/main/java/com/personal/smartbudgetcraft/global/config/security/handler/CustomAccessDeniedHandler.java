package com.personal.smartbudgetcraft.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 권한 없이 접근할 때, 예외 핸들러 (인증)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper mapper;

  /**
   * ApiResponse 양식에 맞게 Response 커스텀
   *
   * @param request               request
   * @param response              response
   * @param accessDeniedException 권한 없을 때 예외
   * @throws IOException 입출력 예외
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException
  ) throws IOException {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json; charset=UTF-8");

    ApiResDto body = ApiResDto.toErrorForm(ErrorCode.ACCESS_DENIED_EXCEPTION.getMessage());

    response.getWriter().write(mapper.writeValueAsString(body));
  }
}