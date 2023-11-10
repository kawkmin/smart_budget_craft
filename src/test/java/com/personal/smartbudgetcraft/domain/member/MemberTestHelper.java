package com.personal.smartbudgetcraft.domain.member;

import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.domain.member.entity.Role;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;

public class MemberTestHelper {

  public static Member createMember(Long id, BudgetTracking budgetTracking) {
    return Member.builder()
        .id(id)
        .account("test123")
        .password("test123*")
        .role(Role.USER)
        .budgetTracking(budgetTracking)
        .build();
  }
}
