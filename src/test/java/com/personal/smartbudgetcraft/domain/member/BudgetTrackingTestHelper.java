package com.personal.smartbudgetcraft.domain.member;

import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;

public class BudgetTrackingTestHelper {

  public static BudgetTracking createBudgetTracking(Long id) {
    return BudgetTracking.builder()
        .id(id)
        .totalExpenditureCost(0)
        .totalDepositCost(0)
        .build();
  }
}
