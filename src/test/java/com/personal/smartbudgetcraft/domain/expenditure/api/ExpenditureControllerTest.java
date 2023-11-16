package com.personal.smartbudgetcraft.domain.expenditure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.personal.smartbudgetcraft.config.restdocs.AbstractRestDocsTests;
import com.personal.smartbudgetcraft.domain.category.CostCategoryTestHelper;
import com.personal.smartbudgetcraft.domain.expenditure.ExpenditureTestHelper;
import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.request.ExpenditureWriteReqDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpenditureDetailResDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpendituresResDto;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.MemberTestHelper;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = ExpenditureController.class)
class ExpenditureControllerTest extends AbstractRestDocsTests {

  private final static String EXPENDITURE_URL = "/api/v1/expenditure";

  @MockBean
  private ExpenditureService expenditureService;

  @Nested
  @DisplayName("지출 생성 관련 컨트롤러 테스트")
  class writeExpenditure {

    @Test
    @DisplayName("정상적으로 입력될 때, 지출 작성에 성공한다.")
    void 정상적으로_입력될_때_지출_작성에_성공한다_201() throws Exception {
      int normalCost = 10000; // 정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(normalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();

      given(expenditureService.writeExpenditure(any(), any())).willReturn(1L);

      mockMvc.perform(post(EXPENDITURE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("지출 돈이 100원 단위가 아닐때, 지출 작성에 실패한다.")
    void 지출_돈이_100원_단위가_아닐때_지출_작성에_실패한다_400() throws Exception {
      int abnormalCost = 109009; // 비정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(abnormalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();

      given(expenditureService.writeExpenditure(any(), any())).willReturn(1L);

      mockMvc.perform(post(EXPENDITURE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리가 없으면, 지출 작성에 실패한다.")
    void 카테고리가_없으면_지출_작성에_실패한다_404() throws Exception {
      int normalCost = 10000; // 정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(normalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();

      given(expenditureService.writeExpenditure(any(), any())).willThrow(
          new BusinessException(55L, "categoryId", ErrorCode.COST_CATEGORY_NOT_FOUND)
      );

      mockMvc.perform(post(EXPENDITURE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("지출 조회 관련 컨트롤러 테스트")
  class readExpenditure {

    @Test
    @DisplayName("지출 상세 조회가 정상적으로 성공한다.")
    void 지출_상세_조회가_정상적으로_성공한다_200() throws Exception {
      Expenditure expenditure = ExpenditureTestHelper.createExpenditure(
          1L, CostCategoryTestHelper.CreateCategory(1L), MemberTestHelper.createMember(1L, null)
      );
      ExpenditureDetailResDto resDto = new ExpenditureDetailResDto(expenditure);

      given(expenditureService.readDetailExpenditure(any(), any())).willReturn(resDto);

      mockMvc.perform(get(EXPENDITURE_URL + "/1"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("지출을 찾을 수 없으면, 지출 조회에 실패한다.")
    void 지출을_찾을_수_없으면_지출_조회에_실패한다_404() throws Exception {
      given(expenditureService.readDetailExpenditure(any(), any())).willThrow(
          new BusinessException(12L, "expenditureId", ErrorCode.EXPENDITURE_NOT_FOUND)
      );

      mockMvc.perform(get(EXPENDITURE_URL + "/1"))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원이 작성하지 않은 지출을 상세 조회하면, 실패한다.")
    void 회원이_작성하지_않은_지출을_상세_조회하면_실패한다_403() throws Exception {

      doThrow(new BusinessException(43, "depositId", ErrorCode.ACCESS_DENIED_EXCEPTION))
          .when(expenditureService)
          .readDetailExpenditure(any(), any());

      mockMvc.perform(get(EXPENDITURE_URL + "/1"))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("모든 파라미터가 없어도, 지출 목록 조회에 성공한다.")
    void 모든_파라미터가_없어도_지출_목록_조회에_성공한다_200() throws Exception {
      ExpendituresResDto resDto = getExpendituresResDto();

      given(expenditureService.readSearchExpenditures(any(), any())).willReturn(resDto);

      mockMvc.perform(get(EXPENDITURE_URL))
          .andExpect(status().isOk());
    }

    /**
     * 지출 목록 조회 데이터 정보 만들기
     *
     * @return 지출 목록 조회 데이터 정보
     */
    private ExpendituresResDto getExpendituresResDto() {
      List<Expenditure> expenditures = new ArrayList<>();
      for (int i = 1; i < 5; i++) {
        expenditures.add(
            ExpenditureTestHelper.createExpenditure((long) i,
                CostCategoryTestHelper.CreateCategory((long) i),
                MemberTestHelper.createMember(1L, null)
            )
        );
      }
      Pageable pageable = PageRequest.of(0, 10);
      int start = (int) pageable.getOffset();
      int end = Math.min((start + pageable.getPageSize()), expenditures.size());

      Page<Expenditure> expenditurePage = new PageImpl<>(expenditures.subList(start, end), pageable,
          expenditures.size());
      ExpendituresResDto resDto = new ExpendituresResDto(expenditurePage, 40000);
      return resDto;
    }
  }

  @Nested
  @DisplayName("지출 수정 관련 컨트롤러 테스트")
  class updateExpenditure {

    @Test
    @DisplayName("지출 수정이 정상적으로 성공한다.")
    void 지출_수정이_정상적으로_성공한다_201() throws Exception {
      int normalCost = 10000; // 정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(normalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();
      given(expenditureService.updateExpenditure(any(), any(), any())).willReturn(1L);

      mockMvc.perform(put(EXPENDITURE_URL + "/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("지출 돈이 100원 단위가 아닐때, 지출 수정에 실패한다.")
    void 지출_돈이_100원_단위가_아닐때_지출_수정에_실패한다_400() throws Exception {
      int abnormalCost = 109; // 비정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(abnormalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();

      given(expenditureService.updateExpenditure(any(), any(), any())).willReturn(1L);

      mockMvc.perform(put(EXPENDITURE_URL + "/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리가 없으면, 지출 수정에 실패한다.")
    void 카테고리가_없으면_지출_수정에_실패한다_404() throws Exception {
      int normalCost = 10000; // 정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(normalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();

      given(expenditureService.updateExpenditure(any(), any(), any())).willThrow(
          new BusinessException(55L, "categoryId", ErrorCode.COST_CATEGORY_NOT_FOUND)
      );

      mockMvc.perform(put(EXPENDITURE_URL + "/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원이 작성하지 않은 지출을 수정하면, 실패한다.")
    void 회원이_작성하지_않은_지출을_수정하면_실패한다_403() throws Exception {
      int normalCost = 10000; // 정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(normalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();

      given(expenditureService.updateExpenditure(any(), any(), any())).willThrow(
          new BusinessException(43, "expenditureId", ErrorCode.ACCESS_DENIED_EXCEPTION)
      );

      mockMvc.perform(put(EXPENDITURE_URL + "/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("지출을 찾을 수 없으면, 지출 수정에 실패한다.")
    void 지출을_찾을_수_없으면_지출_수정에_실패한다_404() throws Exception {
      int normalCost = 10000; // 정상 값

      ExpenditureWriteReqDto reqDto = ExpenditureWriteReqDto.builder()
          .categoryId(1L)
          .cost(normalCost)
          .isExcluded(true)
          .memo("테스트 메모")
          .time(LocalDateTime.now())
          .build();

      given(expenditureService.updateExpenditure(any(), any(), any())).willThrow(
          new BusinessException(12L, "expenditureId", ErrorCode.EXPENDITURE_NOT_FOUND)
      );

      mockMvc.perform(put(EXPENDITURE_URL + "/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("지출의 합계 제외 수정에 성공한다.")
    void 지출의_합계_제외_수정에_성공한다_200() throws Exception {

      mockMvc.perform(patch(EXPENDITURE_URL + "/1/exclude")
              .param("isExclude", String.valueOf(true)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원이 작성하지 않은 지출의 합계 제외를 수정하면, 실패한다.")
    void 회원이_작성하지_않은_지출의_합계_제외를_수정하면_실패한다_403() throws Exception {

      doThrow(new BusinessException(43, "depositId", ErrorCode.ACCESS_DENIED_EXCEPTION))
          .when(expenditureService)
          .updateExclude(any(), any(), any());

      mockMvc.perform(patch(EXPENDITURE_URL + "/1/exclude")
              .param("isExclude", String.valueOf(true)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("지출 삭제 관련 컨트롤러 테스트")
  class deleteExpenditure {

    @Test
    @DisplayName("정상적으로 지출 삭제에 성공한다.")
    void 정상적으로_지출_삭제에_성공한다_204() throws Exception {
      mockMvc.perform(delete(EXPENDITURE_URL + "/1"))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원이 작성하지 않은 지출을 삭제하면, 실패한다.")
    void 회원이_작성하지_않은_지출을_삭제하면_실패한다_403() throws Exception {

      doThrow(new BusinessException(43, "depositId", ErrorCode.ACCESS_DENIED_EXCEPTION))
          .when(expenditureService)
          .deleteExpenditure(any(), any());

      mockMvc.perform(delete(EXPENDITURE_URL + "/1"))
          .andExpect(status().isForbidden());
    }
  }
}