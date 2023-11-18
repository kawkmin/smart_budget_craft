package com.personal.smartbudgetcraft.domain.expenditure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.personal.smartbudgetcraft.config.restdocs.AbstractRestDocsTests;
import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureStatisticsService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.StatisticsResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(controllers = ExpenditureStatisticsController.class)
class ExpenditureStatisticsControllerTest extends AbstractRestDocsTests {

  private final static String EXPENDITURE_STATISTICS_URL = "/api/v1/expenditure/statistics";

  @MockBean
  private ExpenditureStatisticsService expenditureStatisticsService;

  @Nested
  @DisplayName("저번달 지출 통계 관련 컨트롤러 테스트")
  class lastMonthExpenditure {

    @Test
    @DisplayName("저번달 지출 통계가 정상적으로 작동된다.")
    void 저번달_지출_통계가_정상적으로_작동된다() throws Exception {
      StatisticsResDto resDto = StatisticsResDto.builder()
          .prepareCostSum(17600)
          .build();

      given(expenditureStatisticsService.statisticsLastMonth(any())).willReturn(resDto);

      mockMvc.perform(get(EXPENDITURE_STATISTICS_URL + "/last-month"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("지난 요일 지출 통계 관련 컨트롤러 테스트")
  class lastDayExpenditure {

    @Test
    @DisplayName("저난 요일 지출 통계가 정상적으로 작동된다.")
    void 저난_요일_지출_통계가_정상적으로_작동된다() throws Exception {
      StatisticsResDto resDto = StatisticsResDto.builder()
          .prepareCostSum(4000)
          .build();

      given(expenditureStatisticsService.statisticsLastDay(any())).willReturn(resDto);

      mockMvc.perform(get(EXPENDITURE_STATISTICS_URL + "/last-day"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("다른 유저 대비 지출 통계 관련 컨트롤러 테스트")
  class otherMemberExpenditure {

    @Test
    @DisplayName("다른 유저 대비 지출 통계가 정상적으로 작동된다.")
    void 다른_유저_대비_지출_통계가_정상적으로_작동된다() throws Exception {
      StatisticsResDto resDto = StatisticsResDto.builder()
          .prepareCostSum(-22000)
          .build();

      given(expenditureStatisticsService.statisticsOtherMember(any())).willReturn(resDto);

      mockMvc.perform(get(EXPENDITURE_STATISTICS_URL + "/other-member"))
          .andExpect(status().isOk());
    }
  }
}