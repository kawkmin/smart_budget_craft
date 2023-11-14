package com.personal.smartbudgetcraft.domain.deposit.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 하나의 추천 예산 데이터 정보
 */
@Getter
public class DepositResultResDto {

  // 비용 카테고리 이름
  private String categoryName;
  // 비용
  private Integer cost;

  @Builder
  public DepositResultResDto(String categoryName, Integer cost) {
    this.categoryName = categoryName;
    this.cost = cost;
  }
}
