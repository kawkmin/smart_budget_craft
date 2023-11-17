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

  // 최소 추천 금액
  public static final int MIN_RECOMMEND_COST = 1000;
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
    // 회원의 하루에 사용 해야할 예산
    int remainDayOfDeposit = remainTotalCost / remainingDays;

    // 회원의 예산들의 카테고리 분석을 통해, 추천 지출 계산
    // 추천 지출을 저장할 List
    List<ExpenditureRecommendTodayResDto> recommendTodayExpenditure = new ArrayList<>();
    // 계산하면서, List에 저장
    calculatorRecommendTodayCost(member, remainDayOfDeposit, recommendTodayExpenditure);

    // 오늘의 추천 총 금액 계산
    int recommendTodayCost = recommendTodayExpenditure.stream()
        .mapToInt(ExpenditureRecommendTodayResDto::getCost)
        .sum();

    // Res Dto 생성
    ExpendituresRecommendTodayResDto resDto = ExpendituresRecommendTodayResDto.builder()
        .recommendExpenditures(recommendTodayExpenditure)
        .remainDays(remainingDays)
        .recommendCost(recommendTodayCost)
        .remainTotalCost(remainTotalCost)
        .build();

    return resDto;
  }

  /**
   * 종합 계산기
   * 회원의 예산들의 카테고리 분석을 통해, 오늘의 추천 지출 계산
   *
   * @param member                    회원
   * @param remainDayOfDeposit        회원의 하루에 사용 해야할 예산
   * @param recommendTodayExpenditure 추천 지출을 저장할 List
   */
  private void calculatorRecommendTodayCost(
      Member member,
      int remainDayOfDeposit,
      List<ExpenditureRecommendTodayResDto> recommendTodayExpenditure
  ) {
    // 회원의 총 예산
    int totalDepositCost = member.getBudgetTracking().getTotalDepositCost();
    // 카테고리 별 총 합을 계산할 Map
    Map<Long, Integer> categoryRecommendCostMap = new HashMap<>();
    // 회원이 가진 예산의 카테고리 별 총 합이 담긴 map
    Map<Long, Integer> categoryCostSumMap = new HashMap<>();
    // 회원이 가진 예산의 카테고리 별 총 합의 퍼센트가 담긴 map
    Map<Long, Integer> categoryCostPercentMap = new HashMap<>();

    // 1. 회원이 가진 예산의 카테고리 별 총 합 계산
    calculatorCategoryCostSum(member, categoryCostSumMap);
    // 2. 회원이 가진 예산의 카테고리 별 총 합의 퍼센트 계산
    calculatorCategoryCostPercent(categoryCostSumMap, totalDepositCost, categoryCostPercentMap);
    // 3. 카테고리 별 추천 금액 계산.
    calculatorRecommendCost(remainDayOfDeposit, categoryCostPercentMap, categoryRecommendCostMap);

    // 4. 계산된 데이터 정보로 list에 dto로 변환 후 추가
    for (Long id : categoryRecommendCostMap.keySet()) {
      int recommendCost = categoryRecommendCostMap.get(id);
      CostCategory category = categoryRepository.findById(id).orElseThrow();

      ExpenditureRecommendTodayResDto data = ExpenditureRecommendTodayResDto.builder()
          .categoryName(category.getName())
          .cost(recommendCost)
          .build();

      recommendTodayExpenditure.add(data);
    }
  }

  /**
   * 계산기.
   * 카테고리 별 추천 금액 계산
   * 계산식 = 반올림(${하루에 사용 해야할 예산} * (${지출의 카테고리 예산 퍼센트} / 100)/ 최소 단위) *최소 단위
   *
   * @param remainDayOfDeposit       하루에 사용 해야할 예산
   * @param categoryCostPercentMap   회원이 가진 예산의 카테고리 별 총 합이 담긴 map
   * @param categoryRecommendCostMap 회원이 가진 예산의 카테고리 별 총 합의 퍼센트가 담긴 map
   */
  private void calculatorRecommendCost(
      int remainDayOfDeposit,
      Map<Long, Integer> categoryCostPercentMap,
      Map<Long, Integer> categoryRecommendCostMap
  ) {
    for (Long id : categoryCostPercentMap.keySet()) {
      Integer percent = categoryCostPercentMap.get(id);
      double categoryCost =
          Math.round(
              remainDayOfDeposit * ((double) percent / (double) PERCENT_UNIT) / MIN_COST_UNIT
          ) * MIN_COST_UNIT;

      // 최소 추천 금액보다 작으면, 최소 금액으로 설정
      if (categoryCost <= MIN_RECOMMEND_COST) {
        categoryCost = MIN_RECOMMEND_COST;
      }

      categoryRecommendCostMap.put(id, (int) categoryCost);
    }
  }

  /**
   * 계산기
   * 회원이 가진 예산의 카테고리 별 총 합의 퍼센트 계산
   * 계산식 = (${카테고리의 예산 금액 합} / ${총 예산 금액 합}) * 100
   *
   * @param categoryCostSumMap     회원이 가진 예산의 카테고리 별 총 합이 담긴 map
   * @param totalDepositCost       회원의 총 예산
   * @param categoryCostPercentMap 회원이 가진 예산의 카테고리 별 총 합의 퍼센트가 담긴 map
   */
  private void calculatorCategoryCostPercent(
      Map<Long, Integer> categoryCostSumMap,
      int totalDepositCost,
      Map<Long, Integer> categoryCostPercentMap
  ) {
    for (Long id : categoryCostSumMap.keySet()) {
      int cost = categoryCostSumMap.get(id);
      int percent = (int) (((double) cost / totalDepositCost) * PERCENT_UNIT);
      categoryCostPercentMap.put(id, percent);
    }
  }

  /**
   * 계산기
   * 회원이 가진 예산의 카테고리 별 총 합 계산
   *
   * @param member             회원
   * @param categoryCostSumMap 카테고리 별 총 합을 계산할 Map
   */
  private void calculatorCategoryCostSum(Member member, Map<Long, Integer> categoryCostSumMap) {
    member.getDeposits()
        .forEach(deposit ->
            categoryCostSumMap.put(deposit.getCategory().getId(),
                categoryCostSumMap.getOrDefault(deposit.getCategory().getId(), 0)
                    + deposit.getCost())
        );
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
