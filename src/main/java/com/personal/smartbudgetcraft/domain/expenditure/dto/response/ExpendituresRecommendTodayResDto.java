package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 모든 오늘 지출 가능한 금액 추천에 관한 데이터 정보
 */
@Getter
public class ExpendituresRecommendTodayResDto {

  //오늘 지출 가능한 금액 추천 데이터 정보 리스트
  private List<ExpenditureRecommendTodayResDto> recommendExpenditures = new ArrayList<>();
  // 남은 총 예산 금액
  private Integer remainTotalCost;

  public ExpendituresRecommendTodayResDto(
      List<ExpenditureRecommendTodayResDto> recommendExpenditures,
      Integer remainTotalCost) {
    this.recommendExpenditures = recommendExpenditures;
    this.remainTotalCost = remainTotalCost;
  }
}
