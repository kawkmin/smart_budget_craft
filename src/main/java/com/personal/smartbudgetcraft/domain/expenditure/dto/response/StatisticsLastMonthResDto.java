package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 지난 달 대비 총액, 카테고리 별 소비율 통계 데이터 정보
 */
@Getter
public class StatisticsLastMonthResDto {

  // 대비 총액
  private Integer prepareCostSum;

  @Builder
  public StatisticsLastMonthResDto(Integer prepareCostSum) {
    this.prepareCostSum = prepareCostSum;
  }
}
