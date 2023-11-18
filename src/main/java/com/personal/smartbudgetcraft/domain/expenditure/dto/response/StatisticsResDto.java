package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 소비율 통계 데이터 정보
 */
@Getter
public class StatisticsResDto {

  // 대비 총액
  private Integer prepareCostSum;

  @Builder
  public StatisticsResDto(Integer prepareCostSum) {
    this.prepareCostSum = prepareCostSum;
  }
}
