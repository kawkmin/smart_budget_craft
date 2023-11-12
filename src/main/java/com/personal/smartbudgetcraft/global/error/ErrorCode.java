package com.personal.smartbudgetcraft.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 오류 메시지와 상태를 쉽게 추가하기 위한 Enum
 */
@Getter
public enum ErrorCode {
  // Member
  MEMBER_WRONG_PASSWORD_CONFIRM("비밀번호가 서로 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  MEMBER_ACCOUNT_DUPLICATE("중복된 아이디 입니다.", HttpStatus.BAD_REQUEST),

  // Security
  ACCESS_DENIED_EXCEPTION("필요한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
  ACCESS_AUTH_ENTRY_EXCEPTION("유요한 자격이 없습니다.", HttpStatus.UNAUTHORIZED);

  //오류 메시지
  private final String message;
  //오류 상태코드
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }

}
