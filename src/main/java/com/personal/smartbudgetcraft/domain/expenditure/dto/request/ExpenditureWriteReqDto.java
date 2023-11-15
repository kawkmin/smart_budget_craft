package com.personal.smartbudgetcraft.domain.expenditure.dto.request;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.valid.annotation.MoneyUnit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지출을 작성할 때 사용하는 데이터 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenditureWriteReqDto {

  // 카테고리 id
  @NotNull(message = "카테고리를 선택해주세요.")
  private Long categoryId;

  // 지출 금액
  @NotNull(message = "금액을 입력해주세요.")
  @Positive(message = "양수만 입력이 가능합니다.")
  @MoneyUnit(message = "100원 단위로 입력해주세요.")
  @Max(value = Integer.MAX_VALUE - 1, message = "올바른 금액을 입력해주세요.")
  private Integer cost;

  // 메모
  @NotNull(message = "메모를 입력해주세요.")
  private String memo;

  // 합계 제외 여부
  @NotNull(message = "합계 제외 여부에 대한 정보가 없습니다.")
  private Boolean isExcluded;

  // 지출 시간
  @NotNull(message = "지출 시간을 입력해 주세요.")
  private LocalDateTime time;

  /**
   * 지출 Entity로 변경
   *
   * @param category 카테고리
   * @param member   회원
   * @return 지출 Entity
   */
  public Expenditure toEntity(CostCategory category, Member member) {
    return Expenditure.builder()
        .category(category)
        .member(member)
        .cost(this.cost)
        .memo(this.memo)
        .isExcluded(this.isExcluded)
        .time(this.time)
        .build();
  }
}
