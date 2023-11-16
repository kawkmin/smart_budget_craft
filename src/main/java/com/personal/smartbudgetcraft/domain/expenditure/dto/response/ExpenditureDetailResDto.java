package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 지출의 상세 정보를 조회할 때, 사용하는 데이터 정보
 */
@Getter
public class ExpenditureDetailResDto {

  // 지출 카테고리 이름
  private String categoryName;
  // 지출 금액
  private Integer cost;
  // 메모
  private String memo;
  // 합계 제외 여부
  private boolean isExcluded;
  // 지출 시간
  private LocalDateTime time;

  public ExpenditureDetailResDto(Expenditure expenditure) {
    this.categoryName = expenditure.getCategory().getName();
    this.cost = expenditure.getCost();
    this.memo = expenditure.getMemo();
    this.isExcluded = expenditure.getIsExcluded();
    this.time = expenditure.getTime();
  }
}
