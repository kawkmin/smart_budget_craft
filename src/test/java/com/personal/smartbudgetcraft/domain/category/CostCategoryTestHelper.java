package com.personal.smartbudgetcraft.domain.category;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;

public class CostCategoryTestHelper {

  public static CostCategory CreateCategory(Long id) {
    return CostCategory.builder()
        .id(id)
        .name("테스트 카테고리")
        .build();
  }
}
