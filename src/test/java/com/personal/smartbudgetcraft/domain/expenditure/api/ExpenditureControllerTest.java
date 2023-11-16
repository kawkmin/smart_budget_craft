package com.personal.smartbudgetcraft.domain.expenditure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.personal.smartbudgetcraft.config.restdocs.AbstractRestDocsTests;
import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.request.ExpenditureWriteReqDto;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    void 지출을_찾을_수_없으면_지출_지출에_실패한다_404() throws Exception {
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
}