package com.personal.smartbudgetcraft.domain.expenditure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.personal.smartbudgetcraft.config.restdocs.AbstractRestDocsTests;
import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureConsultingService;
import com.personal.smartbudgetcraft.domain.expenditure.constant.AdviceExpenditureComment;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.AdviceCommentResDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpenditureRecommendTodayResDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpendituresRecommendTodayResDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(controllers = ExpenditureConsultingController.class)
class ExpenditureConsultingControllerTest extends AbstractRestDocsTests {

  private final static String EXPENDITURE_CONSULTING_URL = "/api/v1/expenditure/consulting";

  @MockBean
  private ExpenditureConsultingService expenditureConsultingService;

  @Nested
  @DisplayName("오늘 지출 가능한 금액 추천 관련 컨트롤러 테스트")
  class recommendExpenditure {

    @Test
    @DisplayName("정상적으로 오늘 지출 가능한 금액 추천이 실행된다.")
    void 정상적으로_오늘_지출_가능한_금액_추천이_실행된다() throws Exception {
      List<ExpenditureRecommendTodayResDto> recommendTodayResDtoList = new ArrayList<>();
      recommendTodayResDtoList.add(new ExpenditureRecommendTodayResDto("쇼핑", 1000));
      recommendTodayResDtoList.add(new ExpenditureRecommendTodayResDto("의료/건강", 18200));
      recommendTodayResDtoList.add(new ExpenditureRecommendTodayResDto("주거/통신", 3900));

      ExpendituresRecommendTodayResDto resDto = ExpendituresRecommendTodayResDto.builder()
          .recommendExpenditures(recommendTodayResDtoList)
          .remainTotalCost(319200)
          .remainDays(14)
          .recommendCost(23100)
          .build();

      given(expenditureConsultingService.recommendTodayExpenditure(any())).willReturn(resDto);

      mockMvc.perform(get(EXPENDITURE_CONSULTING_URL + "/recommend"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("유저 상황에 맞는 멘트 관련 컨트롤러 테스트")
  class advice {

    @Test
    @DisplayName("정상적으로 멘트 호출이 실행된다.")
    void 정상적으로_멘트_호출이_실행된다() throws Exception {
      AdviceCommentResDto resDto = AdviceCommentResDto.builder()
          .comment(AdviceExpenditureComment.SAVING_WELL.getComment())
          .remainDays(14)
          .remainDeposit(638800)
          .build();

      given(expenditureConsultingService.adviceComment(any())).willReturn(resDto);

      mockMvc.perform(get(EXPENDITURE_CONSULTING_URL + "/advice"))
          .andExpect(status().isOk());
    }
  }
}