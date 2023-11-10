package com.personal.smartbudgetcraft.global.error;

import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 공통 예외 처리
 */
@RestControllerAdvice
public class ExceptionAdvice {

  /**
   * `BindException` 오류가 발생할 때, 핸들링
   *
   * @param e BindException 오류
   * @return 오류 내용을 담은 ResponseEntity
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResDto> bindException(BindException e) {
    // 오류로 원하는 문자열 포맷팅.
    String errorMessage = getErrorMessage(e);

    return ResponseEntity.badRequest()
        .body(ApiResDto.toErrorForm(errorMessage));
  }

  /**
   * BindingException의 bindingResult 분석 후, 오류 메시지 생성
   *
   * @param e BindException
   * @return 포멧팅된 오류 메시지
   */
  private static String getErrorMessage(BindException e) {
    BindingResult bindingResult = e.getBindingResult();

    return bindingResult.getFieldErrors().stream()
        .map(fieldError ->
            getErrorMessage(
                String.valueOf(fieldError.getRejectedValue()), // 값
                fieldError.getField(), // 필드명
                fieldError.getDefaultMessage() // 오류 메시지
            )
        )
        .collect(Collectors.joining(", "));
  }

  /**
   * `BusinessException` 오류가 발생할 때, 핸들링
   *
   * @param e BusinessException 오류
   * @return 오류 내용을 담은 ResponseEntity
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResDto> businessException(BusinessException e) {
    // 오류로 원하는 문자열 포맷팅.
    String errorMessage = getErrorMessage(e.getInvalidValue(), e.getFieldName(), e.getMessage());

    return ResponseEntity.status(e.getHttpStatus())
        .body(ApiResDto.toErrorForm(errorMessage));
  }


  /**
   * 메시지 포멧팅
   * "[{오류 값}] {필드명} : {오류 메시지}"
   */
  private static String getErrorMessage(String invalidValue, String errorField,
      String errorMessage) {
    return String.format("[%s] %s: %s", invalidValue, errorField, errorMessage);
  }
}
