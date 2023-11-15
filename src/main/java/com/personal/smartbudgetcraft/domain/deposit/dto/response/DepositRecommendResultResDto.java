package com.personal.smartbudgetcraft.domain.deposit.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 예산 설계 추천의 결과 데이터 정보
 */
@Getter
public class DepositRecommendResultResDto {

  // 예산 금액
  private Integer sumDepositMoney;
  // 추천된 예산 정보들
  private List<DepositResultResDto> deposits = new ArrayList<>();

  public DepositRecommendResultResDto(Integer sumDepositMoney, List<DepositResultResDto> deposits) {
    this.sumDepositMoney = sumDepositMoney;
    this.deposits = deposits;
  }
}
