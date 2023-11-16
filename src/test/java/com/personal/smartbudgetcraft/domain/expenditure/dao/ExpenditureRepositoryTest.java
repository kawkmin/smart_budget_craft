package com.personal.smartbudgetcraft.domain.expenditure.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.personal.smartbudgetcraft.domain.category.CostCategoryTestHelper;
import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.ExpenditureTestHelper;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.domain.member.dao.MemberRepository;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class ExpenditureRepositoryTest {

  @Autowired
  private ExpenditureRepository expenditureRepository;
  @Autowired
  private CostCategoryRepository categoryRepository;
  @Autowired
  private MemberRepository memberRepository;

  private Expenditure expenditure;
  private CostCategory category;
  private Member member;
  private BudgetTracking budgetTracking;

  @BeforeEach
  void setUp() {
    category = CostCategoryTestHelper.CreateCategory(1L);
    budgetTracking = BudgetTrackingTestHelper.createBudgetTracking(1L);
    member = MemberTestHelper.createMember(1L, budgetTracking);
    expenditure = ExpenditureTestHelper.createExpenditure(1L, category, member);

    categoryRepository.save(category);
    memberRepository.save(member);
    expenditureRepository.save(expenditure);
  }

  @Nested
  @DisplayName("지출 필터링 및 목록 조회 DB 테스트")
  class searchExpendituresTest {

    @Test
    @DisplayName("검색 조건이 없을 때, 정상적으로 작동되는지 테스트")
    void 검색_조건이_없을_때_정상적으로_작동되는지_테스트() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<Expenditure> searchedExpenditures = expenditureRepository.searchExpenditures(pageable,
          member, null, null, null,
          null, null);

      assertThat(searchedExpenditures.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("카테고리 조건이 있을 때, 정상적으로 작동되는지 확인")
    void 카테고리_조건이_있을_때_정상적으로_작동되는지_확인() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<Expenditure> searchedExpenditures = expenditureRepository.searchExpenditures(pageable,
          member, category, null, null,
          null, null);

      assertThat(searchedExpenditures.getTotalElements()).isEqualTo(1);
    }
    //TODO 다양한 조건의 테스트 추가

    @Test
    @DisplayName("모든 조건이 있을 때, 정상적으로 작동이 되는지 확인")
    void 모든_조건이_있을_때_정상적으로_작동이_되는지_확인() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<Expenditure> searchedExpenditures = expenditureRepository.searchExpenditures(pageable,
          member, category, expenditure.getTime().minusDays(1), expenditure.getTime().plusHours(1),
          expenditure.getCost(), expenditure.getCost());

      assertThat(searchedExpenditures.getTotalElements()).isEqualTo(1);
    }
  }
}