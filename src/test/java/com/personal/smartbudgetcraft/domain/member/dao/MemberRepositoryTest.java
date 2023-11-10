package com.personal.smartbudgetcraft.domain.member.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.personal.smartbudgetcraft.domain.member.BudgetTrackingTestHelper;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.domain.member.dao.budgettracking.BudgetTrackingRepository;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BudgetTrackingRepository budgetTrackingRepository;

  private Member member;
  private BudgetTracking budgetTracking;
  private Long memberId;
  private Long budgetTrackingId;

  /**
   * member 객체 하나 저장
   */
  @BeforeEach
  void setUp() {
    budgetTracking = BudgetTrackingTestHelper.createBudgetTracking(1L);
    member = MemberTestHelper.createMember(1L, budgetTracking);
    memberRepository.save(member);

    memberId = member.getId();
    budgetTrackingId = budgetTracking.getId();
  }

  @Nested
  @DisplayName("회원 저장 관련 DB 테스트")
  class CreateMemberTest {

    @Test
    @DisplayName("회원이 저장될 때, 재산 트래킹도 저장이 되는지 테스트")
    void 회원이_저장될_때_재산_트래킹도_저장이_되는지_테스트() {
      Long budgetId = budgetTrackingRepository.findById(budgetTrackingId).get().getId();

      assertThat(budgetId).isEqualTo(budgetTrackingId);
    }
  }

  @Nested
  @DisplayName("회원 조회 관련 DB 테스트")
  class FindMemberTest {

    @Test
    @DisplayName("Id로 회원 조회 테스트")
    void Id로_회원_조회_테스트() {
      String account = memberRepository.findByAccount(member.getAccount()).orElseThrow()
          .getAccount();

      assertThat(account).isEqualTo(member.getAccount());
    }
  }
}