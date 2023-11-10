package com.personal.smartbudgetcraft.domain.auth.api;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.smartbudgetcraft.config.restdocs.AbstractRestDocsTests;
import com.personal.smartbudgetcraft.domain.auth.application.AuthService;
import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberSignUpReqDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest extends AbstractRestDocsTests {

  private static final String AUTH_URL = "/api/v1/auth";

  @Autowired
  private final ObjectMapper objectMapper = new ObjectMapper();

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
}