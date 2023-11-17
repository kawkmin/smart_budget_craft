package com.personal.smartbudgetcraft.domain.expenditure.constant;

import lombok.Getter;

/**
 * 유저 상황에 맞는 멘트를 관리하는 enum
 */
@Getter
public enum AdviceExpenditureComment {
  SAVING_WELL(1.3, "잘 아끼고 있어요. 현명한 소비를 하고 계시네요"), // 1.3배 이상
  SPENDING_APPROPRIATELY(1.0, "적당히 사용 중이시네요. 조절된 소비는 중요해요."), // 1.3 미만 1.0 이상
  EXCEEDING_STANDARD(0.001, "기준을 조금 넘었어요. 지출을 다시 검토해보는 것도 좋겠어요."), // 1.0 미만 0.001 이상
  EXCEEDED_BUDGET(0.0, "예산을 초과했어요. 소비 패턴을 다시 살펴보는 게 좋겠어요."); // 0.001 미만

  // 기준치
  private final double standard;
  // 멘트
  private final String comment;

  AdviceExpenditureComment(double standard, String comment) {
    this.standard = standard;
    this.comment = comment;
  }
}
