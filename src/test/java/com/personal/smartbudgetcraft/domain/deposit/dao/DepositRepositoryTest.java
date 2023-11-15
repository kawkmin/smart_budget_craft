package com.personal.smartbudgetcraft.domain.deposit.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.personal.smartbudgetcraft.domain.category.CostCategoryTestHelper;
import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.DepositTestHelper;
import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.domain.member.dao.MemberRepository;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class DepositRepositoryTest {

  @Autowired
  private DepositRepository depositRepository;
  @Autowired
  private CostCategoryRepository categoryRepository;
  @Autowired
  private MemberRepository memberRepository;

  private Deposit deposit;
  private CostCategory category;
  private Member member;
  private BudgetTracking budgetTracking;

  @BeforeEach
  void setUp() {
    category = CostCategoryTestHelper.CreateCategory(1L);
    budgetTracking = BudgetTrackingTestHelper.createBudgetTracking(1L);
    member = MemberTestHelper.createMember(1L, budgetTracking);
    deposit = DepositTestHelper.createDepositWithMember(1L, category, member);

    categoryRepository.save(category);
    memberRepository.save(member);
    depositRepository.save(deposit);
  }

  @Test
  @DisplayName("findByCategory 테스트")
  void findByCategory_테스트() {
    Long id = depositRepository.findByCategory(category).get().getId();
    assertThat(id).isEqualTo(1L);
  }
}