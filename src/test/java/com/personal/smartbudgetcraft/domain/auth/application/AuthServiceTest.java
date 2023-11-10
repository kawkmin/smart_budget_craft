package com.personal.smartbudgetcraft.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.domain.member.dao.MemberRepository;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberSignUpReqDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @InjectMocks
  private AuthService authService;

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  private Member member;
  private BudgetTracking budgetTracking;

  @BeforeEach
  void setUp() {
    budgetTracking = BudgetTrackingTestHelper.createBudgetTracking(1L);
    member = MemberTestHelper.createMember(1L, budgetTracking);
  }

  @Nested
  @DisplayName("회원가입 관련 서비스 테스트")
  class signup {

    @Test
    @DisplayName("회원가입이 정상적으로 성공하여, 생성된 Id를 반환한다.")
    void 회원가입이_정상적으로_성공하여_생성된_Id를_반환한다() {
      MemberSignUpReqDto reqDto = MemberSignUpReqDto.builder()
          .account("test123")
          .password("test123*")
          .passwordConfirm("test123*")
          .build();

      given(memberRepository.findByAccount("test123")).willReturn(Optional.empty()); //중복 안된 아이디
      given(memberRepository.save(any())).willReturn(member);

      Long memberId = authService.signup(reqDto);
      assertThat(memberId).isEqualTo(1L);
    }

    @Test
    @DisplayName("아이디가 중복될 경우, 예외가 발생한다.")
    void 아이디가_중복될_경우_예외가_발생한다() {
      MemberSignUpReqDto reqDto = MemberSignUpReqDto.builder()
          .account("test123")
          .password("test123*")
          .passwordConfirm("test123*")
          .build();

      given(memberRepository.findByAccount("test123")).willReturn(Optional.of(member)); // 중복된 아이디

      assertThatThrownBy(
          () -> authService.checkDuplicateAccount(reqDto.getAccount())
      );
    }
  }
}