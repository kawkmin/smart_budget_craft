package com.personal.smartbudgetcraft.domain.deposit.dto.request;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.valid.annotation.MoneyUnit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예산을 설정할 때, 사용하는 데이터 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositCreateReqDto {

  // 비용 카테고리
  @NotNull(message = "카테고리를 선택해주세요.")
  private Long categoryId;

  // 예산 금액
  @NotNull(message = "금액을 입력해주세요.")
  @Positive(message = "양수만 입력이 가능합니다.")
  @MoneyUnit(message = "100원 단위로 입력해주세요.")
  @Max(value = Integer.MAX_VALUE - 1, message = "올바른 금액을 입력해주세요.")
  private Integer cost;

  // Entity 로 변경
  public Deposit toEntity(Member member, CostCategory category) {
    return Deposit.builder()
        .member(member)
        .category(category)
        .cost(this.cost)
        .build();
  }
}
