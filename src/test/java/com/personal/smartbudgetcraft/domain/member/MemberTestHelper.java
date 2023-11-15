package com.personal.smartbudgetcraft.domain.member;

import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.Role;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import java.util.ArrayList;
import java.util.List;

public class MemberTestHelper {

  public static Member createMember(Long id, BudgetTracking budgetTracking) {
    return Member.builder()
        .id(id)
        .account("test123")
        .password("test123*")
        .role(Role.USER)
        .budgetTracking(budgetTracking)
        .deposits(new ArrayList<>())
        .build();
  }
  
  public static Member createMemberWithDeposit(Long id, BudgetTracking budgetTracking,
      Deposit deposit) {
    return Member.builder()
        .id(id)
        .account("test123")
        .password("test123*")
        .role(Role.USER)
        .budgetTracking(budgetTracking)
        .deposits(List.of(deposit))
        .build();
  }
}
