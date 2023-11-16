package com.personal.smartbudgetcraft.domain.expenditure;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.time.LocalDateTime;

public class ExpenditureTestHelper {

  public static Expenditure createExpenditure(Long id, CostCategory category, Member member) {
    return Expenditure.builder()
        .id(id)
        .member(member)
        .category(category)
        .cost(10000)
        .time(LocalDateTime.now())
        .isExcluded(true)
        .memo("Test Memo")
        .build();
  }
}
