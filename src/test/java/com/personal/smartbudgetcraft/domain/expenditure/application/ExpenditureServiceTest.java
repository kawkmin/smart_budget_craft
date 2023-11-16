package com.personal.smartbudgetcraft.domain.expenditure.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.personal.smartbudgetcraft.domain.category.CostCategoryTestHelper;
import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.ExpenditureTestHelper;
import com.personal.smartbudgetcraft.domain.expenditure.dao.ExpenditureRepository;
import com.personal.smartbudgetcraft.domain.expenditure.dto.request.ExpenditureWriteReqDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpenditureDetailResDto;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
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
class ExpenditureServiceTest {

  @InjectMocks
  private ExpenditureService expenditureService;
  @Mock
  private ExpenditureRepository expenditureRepository;
  @Mock
  private CostCategoryRepository categoryRepository;

  private Member member;
  private CostCategory category;
  private BudgetTracking budgetTracking;
  private Expenditure expenditure;

  @BeforeEach
  void setUp() {
    category = CostCategoryTestHelper.CreateCategory(1L);
    budgetTracking = BudgetTrackingTestHelper.createBudgetTracking(1L);
    member = MemberTestHelper.createMember(1L, budgetTracking);
    expenditure = ExpenditureTestHelper.createExpenditure(1L, category, member);
  }

  @Nested
  @DisplayName("지출 작성 관련 서비스 테스트")
  class writeExpenditure {

    @Test
    @DisplayName("정상적으로 지출 작성에 성공한다.")
    void 정상적으로_지출_작성에_성공한다() {
      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(category.getId())
          .time(expenditure.getTime())
          .memo(expenditure.getMemo())
          .isExcluded(expenditure.getIsExcluded())
          .cost(expenditure.getCost())
          .build();

      given(categoryRepository.findById(any())).willReturn(Optional.of(category));
      given(expenditureRepository.save(any())).willReturn(expenditure);

      Long wroteExpenditureId = expenditureService.writeExpenditure(member, reqDto);

      assertThat(wroteExpenditureId).isEqualTo(1L);
    }

    @Test
    @DisplayName("카테고리가 없으면 지출 작성에 실패한다.")
    void 카테고리가_없으면_지출_작성에_실패한다() {
      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(999L)
          .time(expenditure.getTime())
          .memo(expenditure.getMemo())
          .isExcluded(expenditure.getIsExcluded())
          .cost(expenditure.getCost())
          .build();

      given(categoryRepository.findById(any())).willThrow(
          new BusinessException(999L, "categoryId", ErrorCode.COST_CATEGORY_NOT_FOUND)
      );

      assertThatThrownBy(
          () -> expenditureService.writeExpenditure(member, reqDto)
      );
    }
  }

  @Nested
  @DisplayName("지출 조회 관련 서비스 테스트")
  class readExpenditure {

    @Test
    @DisplayName("지출 상세 조회에 성공한다.")
    void 지출_상세_조회에_성공한다() {
      Member havePrevExpenditureMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          expenditure);

      given(expenditureRepository.findById(1L)).willReturn(Optional.of(expenditure));

      ExpenditureDetailResDto resDto = expenditureService.readDetailExpenditure(
          havePrevExpenditureMember, expenditure.getId());

      assertThat(resDto.getCategoryName()).isEqualTo(expenditure.getCategory().getName());
      assertThat(resDto.getCost()).isEqualTo(expenditure.getCost());
      assertThat(resDto.getTime()).isEqualTo(expenditure.getTime());
      assertThat(resDto.getMemo()).isEqualTo(expenditure.getMemo());
    }

    @Test
    @DisplayName("회원이 작성한 지출이 아니면, 지출 상세 조회에 실패한다.")
    void 회원이_작성한_지출이_아니면_지출_상세_조회에_실패한다() {

      // 다른 지출만 가진 회원
      Expenditure anotherExpenditure = ExpenditureTestHelper.createExpenditure(66L, category,
          member);
      Member haveAnotherExpenditureMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          anotherExpenditure);

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(10000)
          .build();

      assertThatThrownBy(
          () -> expenditureService.updateExpenditure(haveAnotherExpenditureMember,
              expenditure.getId(), reqDto)
      );
    }

  }

  @Nested
  @DisplayName("지출 수정 관련 서비스 테스트")
  class updateExpenditure {

    @Test
    @DisplayName("정상적으로 지출 수정에 성공한다.")
    void 정상적으로_지출_수정에_성공한다() {
      Member havePrevExpenditureMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          expenditure);

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(10000)
          .build();

      given(categoryRepository.findById(any())).willReturn(Optional.of(category));
      given(expenditureRepository.findById(any())).willReturn(Optional.of(expenditure));

      Long updateExpenditureId = expenditureService.updateExpenditure(havePrevExpenditureMember,
          expenditure.getId(),
          reqDto);
      assertThat(updateExpenditureId).isEqualTo(expenditure.getId());
    }

    @Test
    @DisplayName("회원이 작성한 지출이 아니면, 지출 수정에 실패한다.")
    void 회원이_작성한_지출이_아니면_지출_수정에_실패한다() {

      // 다른 지출만 가진 회원
      Expenditure anotherExpenditure = ExpenditureTestHelper.createExpenditure(66L, category,
          member);
      Member haveAnotherExpenditureMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          anotherExpenditure);

      assertThatThrownBy(
          () -> expenditureService.readDetailExpenditure(haveAnotherExpenditureMember,
              expenditure.getId())
      );
    }

    @Test
    @DisplayName("정상적으로 지출의 합계 제외 수정에 성공한다.")
    void 정상적으로_지출의_합계_제외_수정에_성공한다() {
      // 파라미터 값
      boolean param = true;

      Member havePrevExpenditureMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          expenditure);

      given(expenditureRepository.findById(any())).willReturn(Optional.of(expenditure));

      assertThatNoException().isThrownBy(
          () -> expenditureService.updateExclude(havePrevExpenditureMember, expenditure.getId(),
              param)
      );
    }

    @Test
    @DisplayName("회원이 작성한 지출이 아니면, 지출의 합계 제외 수정에 실패한다.")
    void 회원이_작성한_지출이_아니면_지출의_합계_제외_수정에_실패한다() {
      // 파라미터
      boolean param = true;
      // 다른 지출만 가진 회원
      Expenditure anotherExpenditure = ExpenditureTestHelper.createExpenditure(66L, category,
          member);
      Member haveAnotherExpenditureMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          anotherExpenditure);

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(10000)
          .build();

      assertThatThrownBy(
          () -> expenditureService.updateExclude(haveAnotherExpenditureMember,
              expenditure.getId(), param)
      );
    }
  }

  @Nested
  @DisplayName("지출 삭제 관련 서비스 테스트")
  class deleteDeposit {

    @Test
    @DisplayName("지출 삭제가 정상적으로 성공한다.")
    void 지출_삭제가_정상적으로_성공한다() {
      Member havePrevDepositMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          expenditure);

      given(expenditureRepository.findById(any())).willReturn(Optional.of(expenditure));

      assertThatNoException().isThrownBy(
          () -> expenditureService.deleteExpenditure(havePrevDepositMember, expenditure.getId())
      );
    }

    @Test
    @DisplayName("회원이 작성한 지출이 아니면, 지출 삭제에 실패한다.")
    void 회원이_작성한_지출이_아니면_지출_삭제에_실패한다() {
      // 다른 지출만 가진 회원
      Expenditure anotherExpenditure = ExpenditureTestHelper.createExpenditure(66L, category,
          member);
      Member haveAnotherExpenditureMember = MemberTestHelper.createMemberWithExpenditure(2L,
          budgetTracking,
          anotherExpenditure);

      assertThatThrownBy(
          () -> expenditureService.deleteExpenditure(haveAnotherExpenditureMember,
              expenditure.getId())
      );
    }
  }
}