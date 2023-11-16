package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 지출에 관한 정보를 전달하는, 데이터 정보
 */
@Getter
public class ExpenditureResDto {

  // 지출 id
  private Long expenditureId;
  // 지출 카테고리 이름
  private String categoryName;
  // 지출 금액
  private Integer cost;
  // 합계 제외 여부
  private boolean isExcluded;
  // 지출의 시간
  private LocalDateTime time;

  /**
   * 지출 Entity를 dto로 변경
   *
   * @param expenditure 지출 Entity
   */
  public ExpenditureResDto(Expenditure expenditure) {
    this.expenditureId = expenditure.getId();
    this.categoryName = expenditure.getCategory().getName();
    this.cost = expenditure.getCost();
    this.isExcluded = expenditure.getIsExcluded();
    this.time = expenditure.getTime();
  }
}
