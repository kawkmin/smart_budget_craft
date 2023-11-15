package com.personal.smartbudgetcraft.domain.deposit.dto.request;

import com.personal.smartbudgetcraft.global.config.valid.annotation.MoneyUnit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예산 설계 추천 시스템에 필요한 데이터 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRecommendReqDto {

  // 예산 금액
  @NotNull(message = "금액을 입력해주세요.")
  @Positive(message = "양수만 입력이 가능합니다.")
  @MoneyUnit(message = "100원 단위로 입력해주세요.")
  @Max(value = Integer.MAX_VALUE - 1, message = "올바른 금액을 입력해주세요.")
  private Integer cost;
}
