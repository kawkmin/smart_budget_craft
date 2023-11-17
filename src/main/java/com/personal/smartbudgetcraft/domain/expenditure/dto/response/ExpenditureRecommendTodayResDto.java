package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 오늘 지출 가능한 금액 추천 데이터 정보
 */
@Getter
public class ExpenditureRecommendTodayResDto {

  // 비용 카테고리 이름
  private String categoryName;
  // 비용
  private Integer cost;

  @Builder
  public ExpenditureRecommendTodayResDto(String categoryName, Integer cost) {
    this.categoryName = categoryName;
    this.cost = cost;
  }
}
