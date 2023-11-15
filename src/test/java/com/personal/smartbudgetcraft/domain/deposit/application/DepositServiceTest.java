package com.personal.smartbudgetcraft.domain.deposit.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.personal.smartbudgetcraft.domain.category.CostCategoryTestHelper;
import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.DepositTestHelper;
import com.personal.smartbudgetcraft.domain.deposit.dao.DepositRepository;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositCreateReqDto;
import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
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

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

  @InjectMocks
  private DepositService depositService;
  @Mock
  private DepositRepository depositRepository;
  @Mock
  private CostCategoryRepository categoryRepository;

  private Member member;
  private BudgetTracking budgetTracking;
  private Deposit deposit;
  private CostCategory category;

  @BeforeEach
  void setUp() {
    category = CostCategoryTestHelper.CreateCategory(1L);
    budgetTracking = BudgetTrackingTestHelper.createBudgetTracking(1L);
    member = MemberTestHelper.createMember(1L, budgetTracking);
    deposit = DepositTestHelper.createDeposit(1L, category);
  }

  @Nested
  @DisplayName("예산 작성 관련 서비스 테스트")
  class writeDeposit {

    @Test
    @DisplayName("카테고리에 대해 처음 예산 작성일 때, 예산 작성에 성공한다.")
    void 카테고리에_대해_처음_예산_작성일_때_예산_작성에_성공한다() {
      DepositCreateReqDto reqDto = DepositCreateReqDto.builder()
          .categoryId(1L)
          .cost(10000)
          .build();

      given(categoryRepository.findById(any())).willReturn(Optional.of(category));
      given(depositRepository.save(any())).willReturn(deposit);

      Long writeDepositId = depositService.writeDeposit(member, reqDto);

      assertThat(writeDepositId).isEqualTo(1L);
    }

    @Test
    @DisplayName("카테고리에 대한 기존 예산 작성이 있을 때, 업데이트 로직이 발생한다.")
    void 카테고리에_대한_기존_예산_작성이_있을_때_업데이트_로직이_발생한다() {
      Member havePrevDepositMember = MemberTestHelper.createMemberWithDeposit(2L, budgetTracking,
          deposit);

      DepositCreateReqDto reqDto = DepositCreateReqDto.builder()
          .categoryId(1L)
          .cost(10000)
          .build();

      // 다음이 실행이 안된다.
//      given(categoryRepository.findById(any())).willReturn(Optional.of(category));
//      given(depositRepository.save(any())).willReturn(deposit);
      Long writeDepositId = depositService.writeDeposit(havePrevDepositMember, reqDto);

      assertThat(writeDepositId).isEqualTo(1L);
    }
  }

  @Nested
  @DisplayName("예산 수정 관련 서비스 테스트")
  class updateDeposit {

    @Test
    @DisplayName("정상적으로 예산 수정에 성공한다.")
    void 정상적으로_예산_수정에_성공한다() {
      Member havePrevDepositMember = MemberTestHelper.createMemberWithDeposit(2L, budgetTracking,
          deposit);
      DepositCreateReqDto reqDto = DepositCreateReqDto.builder()
          .categoryId(1L)
          .cost(10000)
          .build();

      given(categoryRepository.findById(any())).willReturn(Optional.of(category));
      given(depositRepository.findById(any())).willReturn(Optional.of(deposit));

      Long updateDepositId = depositService.updateDeposit(havePrevDepositMember, deposit.getId(),
          reqDto);
      assertThat(updateDepositId).isEqualTo(deposit.getId());
    }

    @Test
    @DisplayName("회원이 작성한 예산이 아니면, 예산 수정에 실패한다.")
    void 회원이_작성한_예산이_아니면_예산_수정에_실패한다() {

      // 다른 예산만 가진 회원
      Deposit anotherDeposit = DepositTestHelper.createDepositWithMember(66L, category, member);
      Member haveAnotherDepositMember = MemberTestHelper.createMemberWithDeposit(2L, budgetTracking,
          anotherDeposit);

      DepositCreateReqDto reqDto = DepositCreateReqDto.builder()
          .categoryId(1L)
          .cost(10000)
          .build();

      assertThatThrownBy(
          () -> depositService.updateDeposit(haveAnotherDepositMember, deposit.getId(), reqDto)
      );
    }
  }
}