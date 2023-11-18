package com.personal.smartbudgetcraft.domain.expenditure.application;

import com.personal.smartbudgetcraft.domain.expenditure.dao.ExpenditureRepository;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.StatisticsLastMonthResDto;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
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

  /**
   * 지난 달의 오늘 일차까지 해당하는 과거 모든 데이터 기록 대비 소비율 구하기
   * TODO 카테고리 별 소비율 비교 등 다양한 비교
   *
   * @param member 회원
   * @return 지난 달의 오늘 일차까지 해당하는 과거 모든 데이터 기록 소비율 대비
   */
  public StatisticsLastMonthResDto statisticsLastMonth(Member member) {

    // 현재 날짜와 시간 정보를 가져옵니다
    LocalDateTime now = LocalDateTime.now();
    // 이번 달의 1일을 구하기
    LocalDateTime firstDayOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0)
        .withSecond(0).withNano(0);
    // 이전 달의 1일 구하기
    LocalDateTime firstDayOfLastMonth = firstDayOfThisMonth.minusMonths(1);
    // 저번달의 같은 날짜 구하기
    LocalDateTime sameDayLastMonth = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
        .minusMonths(1);

    // 이번달의 회원의 지출 구하기
    List<Expenditure> thisMonthExpenditure = expenditureRepository.findAllByTimeBetweenAndMember(
        firstDayOfThisMonth, now, member);
    // 저번달의 오늘 날짜에 해당하는 날 까지 지출 구하기
    List<Expenditure> lastMonthExpenditure = expenditureRepository.findAllByTimeBetweenAndMember(
        firstDayOfLastMonth, sameDayLastMonth, member);

    // 이번달의 총 지출액
    int thisMonthCostSum = thisMonthExpenditure.stream().mapToInt(Expenditure::getCost).sum();
    // 저번달의 총 지출액
    int lastMonthCostSum = lastMonthExpenditure.stream().mapToInt(Expenditure::getCost).sum();
    // 이번달과 저번달의 지출 차액
    int prepareCostSum = thisMonthCostSum - lastMonthCostSum;

    StatisticsLastMonthResDto resDto = StatisticsLastMonthResDto.builder()
        .prepareCostSum(prepareCostSum)
        .build();

    return resDto;
  }
}
