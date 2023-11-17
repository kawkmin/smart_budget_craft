package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
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
  // 남은 일 수
  private Integer remainDays;
  // 하루에 사용할 추천 금액
  private Integer recommendCost;

  @Builder
  public ExpendituresRecommendTodayResDto(
      List<ExpenditureRecommendTodayResDto> recommendExpenditures,
      Integer remainTotalCost, Integer remainDays, Integer recommendCost) {
    this.recommendExpenditures = recommendExpenditures;
    this.remainTotalCost = remainTotalCost;
    this.remainDays = remainDays;
    this.recommendCost = recommendCost;
  }
}
