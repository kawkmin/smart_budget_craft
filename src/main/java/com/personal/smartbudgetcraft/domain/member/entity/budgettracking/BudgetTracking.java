package com.personal.smartbudgetcraft.domain.member.entity.budgettracking;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이번 달의 회원의 총 예산과 총 지출을 관리
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "budget_tracking")
public class BudgetTracking {

  // 재산 트래킹 아이디
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "budget_tracking_id", nullable = false)
  private Long id;

  // 총 예산 금액
  @Column(name = "total_deposit_cost", nullable = false)
  private Integer totalDepositCost;

  // 총 지출 금액
  @Column(name = "total_expenditure_cost", nullable = false)
  private Integer totalExpenditureCost;

  @Builder
  public BudgetTracking(Long id, Integer totalDepositCost, Integer totalExpenditureCost) {
    this.id = id;
    this.totalDepositCost = totalDepositCost;
    this.totalExpenditureCost = totalExpenditureCost;
  }

  /**
   * 처음 생성 하거나, 매달 처음 요일 일 때, 재산 초기화
   *
   * @return 초기화된 재산 트래킹
   */
  public static BudgetTracking resetBudget() {
    return BudgetTracking.builder()
        .totalDepositCost(0)
        .totalExpenditureCost(0)
        .build();
  }

  /**
   * 총 예산 금액 변동
   *
   * @param cost 금액
   */
  public void updateDepositCost(Integer cost) {
    this.totalDepositCost += cost;
  }

  /**
   * 총 지출 금액 변동
   *
   * @param cost 금액
   */
  public void updateExpenditureCost(Integer cost) {
    this.totalExpenditureCost += cost;
  }
}
