package com.personal.smartbudgetcraft.domain.deposit;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.member.entity.Member;

public class DepositTestHelper {

  public static Deposit createDeposit(Long id, CostCategory category) {
    return Deposit.builder()
        .id(id)
        .cost(10000)
        .category(category)
        .build();
  }

  public static Deposit createDepositWithMember(Long id, CostCategory category, Member member) {
    return Deposit.builder()
        .id(id)
        .cost(10000)
        .category(category)
        .member(member)
        .build();
  }
}
