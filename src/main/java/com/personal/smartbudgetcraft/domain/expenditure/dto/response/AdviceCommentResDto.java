package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 유저 상황에 맞는 추천 멘트 데이터 정보
 */
@Getter
public class AdviceCommentResDto {

  // 이번달의 남은 날짜
  private Integer remainDays;
  // 이번달의 남은 예산
  private Integer remainDeposit;
  // 조언 멘트
  private String comment;

  @Builder
  public AdviceCommentResDto(Integer remainDays, Integer remainDeposit, String comment) {
    this.remainDays = remainDays;
    this.remainDeposit = remainDeposit;
    this.comment = comment;
  }
}
