package com.personal.smartbudgetcraft.domain.auth.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.personal.smartbudgetcraft.domain.auth.application.AuthService;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberLoginReqDto;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberSignUpReqDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.security.annotation.LoginMember;
import com.personal.smartbudgetcraft.global.config.security.data.TokenDto;
import com.personal.smartbudgetcraft.global.config.security.data.TokenReqDto;
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

  /**
   * 로그인 및 토큰 발급
   *
   * @param reqDto 로그인 입력 데이터
   * @return 200, JWT 토큰
   */
  @PostMapping("/login")
  public ResponseEntity<ApiResDto> login(
      @RequestBody MemberLoginReqDto reqDto
  ) {
    TokenDto jwtToken = authService.login(reqDto);
    
    return ResponseEntity.ok(ApiResDto.toSuccessForm(jwtToken));
  }

  /**
   * Access Token 재발급
   *
   * @param member 토큰의 회원
   * @param reqDto 기존 토큰 데이터 정보
   * @return 200, 재발급 된 JWT 토큰
   */
  @PostMapping("/reissue")
  public ResponseEntity<ApiResDto> reissue(
      @LoginMember Member member,
      @RequestBody TokenReqDto reqDto
  ) {
    TokenDto jwtToken = authService.reissue(member, reqDto);

    return ResponseEntity.ok(ApiResDto.toSuccessForm(jwtToken));
  }

  /**
   * 로그아웃 및 토큰 삭제
   *
   * @param member 로그인된 회원
   * @return 204, 토큰 삭제
   */
  @PostMapping("/logout")
  public ResponseEntity<ApiResDto> logout(
      @LoginMember Member member
  ) {
    authService.logout(member);

    return ResponseEntity.status(NO_CONTENT)
        .body(ApiResDto.toSuccessForm(""));
  }
}
