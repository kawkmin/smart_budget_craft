package com.personal.smartbudgetcraft.domain.expenditure.application;

import com.personal.smartbudgetcraft.domain.expenditure.dao.ExpenditureRepository;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.StatisticsResDto;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.dao.MemberRepository;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenditureStatisticsService {

  private final ExpenditureRepository expenditureRepository;
  private final MemberRepository memberRepository;

  /**
   * 지난 달의 오늘 일차까지 해당하는 과거 모든 데이터 기록 대비 소비율 구하기
   *
   * @param member 회원
   * @return 지난 달의 오늘 일차까지 해당하는 과거 모든 데이터 기록 소비율 대비
   */
  public StatisticsResDto statisticsLastMonth(Member member) {

    // 현재 날짜와 시간 정보 구하기
    LocalDateTime now = LocalDateTime.now();
    // 이번 달의 1일을 구하기
    LocalDateTime firstDayOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0)
        .withSecond(0).withNano(0);
    // 이전 달의 1일 구하기
    LocalDateTime firstDayOfLastMonth = firstDayOfThisMonth.minusMonths(1);
    // 저번달의 같은 날짜 구하기
    LocalDateTime sameDayLastMonth = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
        .minusMonths(1);

    // 소비율 계산
    StatisticsResDto resDto = calculateExpenditureCostSum(
        member, firstDayOfThisMonth, now, firstDayOfLastMonth, sameDayLastMonth);

    return resDto;
  }

  /**
   * 지난달의 오늘 요일의 오늘 대비 소비율 구하기
   *
   * @param member 회원
   * @return 지난달의 오늘 요일의 오늘 대비 소비율
   */
  public StatisticsResDto statisticsLastDay(Member member) {
    // 현재 날짜와 시간 정보를 구하기
    LocalDateTime now = LocalDateTime.now();
    // 오늘의 00시 00분 구하기
    LocalDateTime todayMidnight = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
    // 저번달의 오늘 날짜의 같은 시간 구하기
    LocalDateTime sameDayLastMonth = now.minusMonths(1);
    // 저번달의 오늘 날짜의 00시 00분 구하기
    LocalDateTime sameDayLastMonthMidnight = todayMidnight.minusMonths(1);

    // 소비율 계산
    StatisticsResDto resDto = calculateExpenditureCostSum(
        member, todayMidnight, now, sameDayLastMonthMidnight, sameDayLastMonth);

    return resDto;
  }

  /**
   * 특정 기간의 지출액과 다른 특정 기간의 지출액을 비교하여, 소비율 계산
   *
   * @param member     회원
   * @param startDate1 기간1의 시작일
   * @param endDate1   기간1의 마지막일
   * @param startDate2 기간2의 시작일
   * @param endDate2   기간2의 마지막일
   * @return 계산된 소비율
   */
  private StatisticsResDto calculateExpenditureCostSum(Member member, LocalDateTime startDate1,
      LocalDateTime endDate1, LocalDateTime startDate2, LocalDateTime endDate2
  ) {
    // 기간1 동안의 지출 계산
    List<Expenditure> Expenditures1 = expenditureRepository.findAllByTimeBetweenAndMember(
        startDate1, endDate1, member);
    // 기간2 동안의 지출 계산
    List<Expenditure> Expenditures2 = expenditureRepository.findAllByTimeBetweenAndMember(
        startDate2, endDate2, member);

    // 기간1의 총 지출액
    int sumCost1 = Expenditures1.stream().mapToInt(Expenditure::getCost).sum();
    // 기간2의 총 지출액
    int sumCost2 = Expenditures2.stream().mapToInt(Expenditure::getCost).sum();
    // 이번달과 저번달의 지출 차액
    int prepareCostSum = sumCost1 - sumCost2;

    StatisticsResDto resDto = StatisticsResDto.builder()
        .prepareCostSum(prepareCostSum)
        .build();
    return resDto;
  }

  /**
   * 다른 유저들의 평균 지출 대비 나의 지출 대비 소비율 계산
   *
   * @param member 회원 (나)
   * @return 다른 유저들의 평균 지출 대비 나의 지출 대비 소비율
   */
  public StatisticsResDto statisticsOtherMember(Member member) {
    // 나를 제외한 모든 회원들의 총 지출 list 구하기
    List<Integer> allExpenditureCostWithoutMe = memberRepository.findAllExpenditureCostWithoutMe(
        member);

    // 나를 제외한 모든 회원들의 총 지출 합 구하기
    int totalExpenditureCost = allExpenditureCostWithoutMe.stream().mapToInt(i -> i).sum();
    // 나를 제외한 모든 회원들의 총 지출의 평균 구하기
    int averageExpenditureCost = Math.round(
        (float) totalExpenditureCost / allExpenditureCostWithoutMe.size());

    // 나의 총 지출 구하기
    int myTotalExpenditureCost = member.getBudgetTracking().getTotalExpenditureCost();

    // 나의 총 지출과 나를 제외한 모든 회원들의 총 지출의 평균의 빼기
    int prepareCostSum = myTotalExpenditureCost - averageExpenditureCost;

    StatisticsResDto resDto = StatisticsResDto.builder()
        .prepareCostSum(prepareCostSum)
        .build();
    return resDto;
  }
}
