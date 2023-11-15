package com.personal.smartbudgetcraft.domain.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.personal.smartbudgetcraft.config.restdocs.AbstractRestDocsTests;
import com.personal.smartbudgetcraft.domain.auth.application.AuthService;
import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberLoginReqDto;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberSignUpReqDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import com.personal.smartbudgetcraft.global.config.security.data.TokenDto;
import com.personal.smartbudgetcraft.global.config.security.data.TokenReqDto;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest extends AbstractRestDocsTests {

  private static final String AUTH_URL = "/api/v1/auth";


  @MockBean
  private AuthService authService;

  private Member member;
  private BudgetTracking budgetTracking;

  @BeforeEach
  void setUp() {
    budgetTracking = BudgetTrackingTestHelper.createBudgetTracking(1L);
    member = MemberTestHelper.createMember(1L, budgetTracking);
  }

  @Nested
  @DisplayName("회원 가입 관련 컨트롤러 테스트")
  class signup {

    @Test
    @DisplayName("회원 가입이 정상적으로 성공한다.")
    void 회원_가입이_정상적으로_성공한다() throws Exception {
      MemberSignUpReqDto reqDto =
          MemberSignUpReqDto.builder()
              .account(member.getAccount())
              .password(member.getPassword())
              .passwordConfirm(member.getPassword())
              .build();

      given(authService.signup(reqDto)).willReturn(1L);

      mockMvc.perform(post(AUTH_URL + "/signup")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원 계정명이 올바르지 않으면, 회원 가입에 실패한다.")
    void 회원_계정명이_올바르지_않으면_회원_가입에_실패한다() throws Exception {
      MemberSignUpReqDto reqDto =
          MemberSignUpReqDto.builder()
              .account("t1") // 짧은 account
              .password(member.getPassword())
              .passwordConfirm(member.getPassword())
              .build();

      given(authService.signup(reqDto)).willReturn(1L);

      mockMvc.perform(post(AUTH_URL + "/signup")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 비밀번호가 올바르지 않으면, 회원 가입에 실패한다.")
    void 회원_비밀번호가_올바르지_않으면_회원_가입에_실패한다() throws Exception {
      MemberSignUpReqDto reqDto =
          MemberSignUpReqDto.builder()
              .account(member.getAccount())
              .password("asdfasdfasdf12") // 특수문자가 없는 비밀번호.
              .passwordConfirm("asdfasdfasdf12")
              .build();

      given(authService.signup(reqDto)).willReturn(1L);

      mockMvc.perform(post(AUTH_URL + "/signup")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호랑 비밀번호 확인이 같지 않으면, 회원 가입에 실패한다.")
    void 비밀번호랑_비밀번호_확인이_같지_않으면_회원_가입에_실패한다() throws Exception {
      MemberSignUpReqDto reqDto =
          MemberSignUpReqDto.builder()
              .account(member.getAccount())
              .password(member.getPassword())
              .passwordConfirm(member.getPassword() + "11") // 비밀번호 확인 다름
              .build();

      given(authService.signup(reqDto)).willReturn(1L);

      mockMvc.perform(post(AUTH_URL + "/signup")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("중복된 계정명을 가진 회원이 없으면, 중복 확인에 성공한다.")
    void 중복된_계정명을_가진_회원이_없으면_중복_확인에_성공한다() throws Exception {
      String account = member.getAccount();

      doNothing().when(authService).checkDuplicateAccount(account);

      mockMvc.perform(get(AUTH_URL + "/signup/exists/account")
              .param("account", account))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("중복된 계정명을 가진 회원이 있으면, 중복 확인에 실패한다.")
    void 중복된_계정명을_가진_회원이_있으면_중복_확인에_실패한다() throws Exception {
      String account = member.getAccount();

      doThrow(new BusinessException(account, "account", ErrorCode.MEMBER_ACCOUNT_DUPLICATE))
          .when(authService)
          .checkDuplicateAccount(account);

      mockMvc.perform(get(AUTH_URL + "/signup/exists/account")
              .param("account", account))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("로그인 관련 컨트롤러 테스트")
  class login {

    @Test
    @WithMockUser
    @DisplayName("아이디 비밀번호가 일치하면, 토큰 반환이 성공한다.")
    void 아이디_비밀번호가_일치하면_토큰_반환이_성공한다() throws Exception {
      MemberLoginReqDto reqDto = MemberLoginReqDto.builder()
          .account(member.getAccount())
          .password(member.getPassword())
          .build();

      TokenDto tokenDto =
          TokenDto.builder()
              .grantType("Bearer")
              .accessToken("새로운 Access 토큰")
              .refreshToken("새로운 Refresh 토큰")
              .accessTokenExpiresIn(1234567L)
              .build();

      given(authService.login((any()))).willReturn(tokenDto);

      mockMvc.perform(post(AUTH_URL + "/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto))
              .header(HttpHeaders.AUTHORIZATION, "JWT_TOKEN"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("아이디가 존재하지 않으면, 토큰 반환이 실패한다.")
    void 아이디가_존재하지_않으면_토큰_반환이_실패한다() throws Exception {
      MemberLoginReqDto reqDto = MemberLoginReqDto.builder()
          .account(member.getAccount())
          .password(member.getPassword())
          .build();

      given(authService.login((any()))).willThrow(
          new BusinessException(11L, "account", ErrorCode.MEMBER_ACCOUNT_NOT_FOUND));

      mockMvc.perform(post(AUTH_URL + "/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto))
              .header(HttpHeaders.AUTHORIZATION, "JWT_TOKEN"))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면, 토큰 반환이 실패한다.")
    void 비밀번호가_일치하지_않으면_토큰_반환이_실패한다() throws Exception {
      MemberLoginReqDto reqDto = MemberLoginReqDto.builder()
          .account(member.getAccount())
          .password(member.getPassword())
          .build();

      given(authService.login((any()))).willThrow(
          new BusinessException(null, "password", ErrorCode.MEMBER_PASSWORD_BAD_REQUEST));

      mockMvc.perform(post(AUTH_URL + "/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto))
              .header(HttpHeaders.AUTHORIZATION, "JWT_TOKEN"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("로그아웃 관련 테스트")
  class logout {

    @Test
    @DisplayName("정상적으로 로그아웃에 성공한다.")
    void 정상적으로_로그아웃에_성공한다() throws Exception {
      mockMvc.perform(post(AUTH_URL + "/logout")
              .header(HttpHeaders.AUTHORIZATION, "JWT_TOKEN"))
          .andExpect(status().isNoContent());
    }
  }

  @Nested
  @DisplayName("토큰 재발급 관련 테스트")
  class reissue {

    @Test
    @DisplayName("Refresh 토큰이 올바를 때, 재발급에 성공한다.")
    void Refresh_토큰이_올바를_때_재발급에_성공한다() throws Exception {
      TokenReqDto reqDto =
          TokenReqDto.builder()
              .accessToken("올바른 Access 토큰")
              .refreshToken("올바른 Refresh 토큰")
              .build();

      TokenDto tokenDto =
          TokenDto.builder()
              .grantType("Bearer")
              .accessToken("새로운 Access 토큰")
              .refreshToken("새로운 Refresh 토큰")
              .accessTokenExpiresIn(1234567L)
              .build();

      given(authService.reissue(any(), any())).willReturn(tokenDto);

      mockMvc.perform(post(AUTH_URL + "/reissue")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Refresh 토큰이 올바르지 않을 때, 재발급에 실패한다.")
    void 올바르지_않은_토큰일_경우_토큰_재발급에_실패한다() throws Exception {
      TokenReqDto reqDto =
          TokenReqDto.builder()
              .accessToken("올바르지 않은 Access 토큰")
              .refreshToken("올바르지 않은 Refresh 토큰")
              .build();

      given(authService.reissue(any(), any())).willThrow(
          new BusinessException(null, "refreshToken", ErrorCode.REFRESH_TOKEN_MISMATCH)
      );

      mockMvc.perform(post(AUTH_URL + "/reissue")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }
  }
}