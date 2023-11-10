package com.personal.smartbudgetcraft.domain.auth.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.personal.smartbudgetcraft.domain.auth.application.AuthService;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberSignUpReqDto;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * 회원 가입
   *
   * @param reqDto 회원 가입 입력 데이터
   * @return 201, 생성된 회원의 ID
   */
  @PostMapping("/signup")
  public ResponseEntity<ApiResDto> signUp(
      @Valid @RequestBody MemberSignUpReqDto reqDto
  ) {
    reqDto.validPasswordConfirm(); // 비밀번호 확인 검증

    Long memberId = authService.signup(reqDto);
    return ResponseEntity.status(CREATED)
        .body(ApiResDto.toSuccessForm(memberId));
  }

  /**
   * 아이디 중복 확인
   *
   * @param account 확인할 아이디
   * @return 204
   */
  @GetMapping("/signup/exists/account")
  public ResponseEntity<ApiResDto> checkDuplicateAccount(
      @RequestParam @NotNull String account
  ) {
    authService.checkDuplicateAccount(account); // 아이디 중복 확인

    return ResponseEntity.status(NO_CONTENT)
        .body(ApiResDto.toSuccessForm(""));
  }

}
