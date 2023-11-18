package com.personal.smartbudgetcraft.domain.expenditure.api;

import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureStatisticsService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.StatisticsLastMonthResDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.security.annotation.LoginMember;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 지출 통계 관련 컨트롤러
 */
@RestController
@RequestMapping("api/v1/expenditure/statistics")
@RequiredArgsConstructor
public class ExpenditureStatisticsController {

  private final ExpenditureStatisticsService expenditureStatisticsService;

  /**
   * 지난 달의 오늘 일차까지 해당하는 과거 모든 데이터 기록 대비 소비율 구하기
   * TODO 카테고리 별 소비율 비교 등 다양한 비교
   *
   * @param member 회원
   * @return 200, 지난 달의 오늘 일차까지 해당하는 과거 모든 데이터 기록 대비 소비율
   */
  @GetMapping("/last-month")
  public ResponseEntity<ApiResDto> statisticsLastMonth(
      @LoginMember Member member
  ) {
    StatisticsLastMonthResDto resDto = expenditureStatisticsService.statisticsLastMonth(member);

    return ResponseEntity.ok(ApiResDto.toSuccessForm(resDto));
  }
}
