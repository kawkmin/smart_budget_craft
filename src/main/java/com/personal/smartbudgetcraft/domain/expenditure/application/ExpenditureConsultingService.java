package com.personal.smartbudgetcraft.domain.expenditure.application;

import static com.personal.smartbudgetcraft.domain.deposit.application.DepositService.MIN_COST_UNIT;
import static com.personal.smartbudgetcraft.domain.deposit.application.DepositService.PERCENT_UNIT;

import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpenditureRecommendTodayResDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpendituresRecommendTodayResDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenditureConsultingService {

  private final CostCategoryRepository categoryRepository;

  /**
   * 오늘의 추천 지출을 카테고리별로 추천
   *
   * @param member 회원
   * @return 추천된 카테고리별 지출
   */
  public ExpendituresRecommendTodayResDto recommendTodayExpenditure(Member member) {
    // 이번달의 남은 날짜 구하기
    int remainingDays = calculatorRemainingDays();
    // 회원의 하루 예산 구하기
    int dayOfDeposit = calculatorDayOfDeposit(member);
    // 회원의 남은 총 예산 돈 (남은 날짜 * 하루 예산)
    int remainTotalCost = remainingDays * dayOfDeposit;
    // 회원의 하루에 써야할 예산 돈
    int remainDayOfDeposit = remainTotalCost / remainingDays;
    // 회원의 총 예산
    int totalDepositCost = member.getBudgetTracking().getTotalDepositCost();

    Map<Long, Integer> categoryRecommendCostMap = new HashMap<>();

    // 회원이 가진 예산의 카테고리 별 총 합이 담긴 map
    Map<Long, Integer> categoryCostSumMap = new HashMap<>();
    // 회원이 가진 예산의 카테고리 별 총 합 계산
    member.getDeposits().stream()
        .forEach(deposit ->
            categoryCostSumMap.put(deposit.getCategory().getId(),
                categoryCostSumMap.getOrDefault(deposit.getCategory().getId(), 0))
        );

    // 회원이 가진 예산의 카테고리 별 총 합의 퍼센트가 담긴 map
    Map<Long, Integer> categoryCostPercentMap = new HashMap<>();
    // 회원이 가진 예산의 카테고리 별 총 합의 퍼센트 계산
    // (${카테고리의 예산 금액 합} / ${총 예산 금액 합}) * 100
    for (Long id : categoryCostSumMap.keySet()) {
      int cost = categoryCostSumMap.get(id);
      int percent = (int) ((long) cost / totalDepositCost) * PERCENT_UNIT;
      categoryCostPercentMap.put(id, percent);
    }

    // 카테고리 별 추천 금액 계산. 최소 단위는 100이다.
    // 반올림(${카테고리의 예산 금액 합} * (${카테고리의 예산 퍼센트} / 100)/ 최소 단위) *최소 단위
    for (Long id : categoryCostPercentMap.keySet()) {
      Integer percent = categoryCostPercentMap.get(id);
      Integer cost = categoryCostSumMap.get(id);
      double categoryCost =
          Math.round(cost * ((long) percent / PERCENT_UNIT) / MIN_COST_UNIT) * MIN_COST_UNIT;

      categoryRecommendCostMap.put(id, (int) categoryCost);
    }

    List<ExpenditureRecommendTodayResDto> recommendTodayExpenditure = new ArrayList<>();
    for (Long id : categoryRecommendCostMap.keySet()) {
      int recommendCost = categoryRecommendCostMap.get(id);
      CostCategory category = categoryRepository.findById(id).orElseThrow();

      ExpenditureRecommendTodayResDto data = ExpenditureRecommendTodayResDto.builder()
          .categoryName(category.getName())
          .cost(recommendCost)
          .build();

      recommendTodayExpenditure.add(data);
    }

    ExpendituresRecommendTodayResDto resDto = new ExpendituresRecommendTodayResDto(
        recommendTodayExpenditure, remainTotalCost);

    return resDto;
  }

  /**
   * 회원의 하루 예산 구하기
   *
   * @param member 회원
   * @return 하루 예산
   */
  private int calculatorDayOfDeposit(Member member) {
    // 현재 날짜를 가져오기
    LocalDate currentDate = LocalDate.now();

    // 이번 달의 마지막 날짜 가져오기
    LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

    Integer totalDepositCost = member.getBudgetTracking().getTotalDepositCost();

    // 회원이 하루 사용해야할 예산 금액
    int dayOfDeposit = totalDepositCost / lastDayOfMonth.getDayOfMonth();
    return dayOfDeposit;
  }

  /**
   * 이번달의 남은 날짜 계산
   *
   * @return 이번달의 남은 날짜
   */
  public int calculatorRemainingDays() {
    // 현재 날짜를 가져오기
    LocalDate currentDate = LocalDate.now();

    // 이번 달의 마지막 날짜 가져오기
    LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

    // 현재 날짜부터 이번 달의 마지막 날짜까지의 남은 일 수 계산 (마지막 일 수 포함)
    int remainingDays = (int) ChronoUnit.DAYS.between(currentDate, lastDayOfMonth) + 1;

    return remainingDays;
  }
}
