package com.personal.smartbudgetcraft.global.schedule;

import com.personal.smartbudgetcraft.domain.member.dao.budgettracking.BudgetTrackingRepository;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스케줄을 전역으로 관리하는 스케줄러
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class globalScheduler {

  private final BudgetTrackingRepository budgetTrackingRepository;

  /**
   * 매달 1일에 회원의 재산 트래킹은 초기화 된다.
   */
  @Scheduled(cron = "0 0 0 1 * *")
  @Transactional
  public void resetBudget() {
    List<BudgetTracking> budgetTrackings = budgetTrackingRepository.findAll();
    // 모든 재산 트래킹 초기화
    budgetTrackings.forEach(BudgetTracking::resetBudget);
  }
}
