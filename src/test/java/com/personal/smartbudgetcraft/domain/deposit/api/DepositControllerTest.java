package com.personal.smartbudgetcraft.domain.deposit.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.personal.smartbudgetcraft.config.restdocs.AbstractRestDocsTests;
import com.personal.smartbudgetcraft.domain.deposit.application.DepositService;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositCreateReqDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositRecommendReqDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.response.DepositRecommendResultResDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.response.DepositResultResDto;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = DepositController.class)
class DepositControllerTest extends AbstractRestDocsTests {

  private final static String DEPOSIT_URL = "/api/v1/deposit";

  @MockBean
  private DepositService depositService;

  @Nested
  @DisplayName("예산 작성 관련 컨트롤러 테스트")
  class writeDeposit {

    @Test
    @DisplayName("정상적으로 입력될 때, 예산 작성에 성공한다.")
    void 정상적으로_입력될_때_예산_작성에_성공한다_201() throws Exception {
      int normalCost = 10000; // 정상 값

      DepositCreateReqDto reqDto = DepositCreateReqDto.builder()
          .categoryId(1L)
          .cost(normalCost)
          .build();

      given(depositService.writeDeposit(any(), any())).willReturn(1L);

      mockMvc.perform(post(DEPOSIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("예산 돈이 100원 단위가 아닐때, 예산 작성에 실패한다.")
    void 예산_돈이_100원_단위가_아닐때_예산_작성에_실패한다_400() throws Exception {
      int abnormalCost = 54; // 비정상 값

      DepositCreateReqDto reqDto = DepositCreateReqDto.builder()
          .categoryId(1L)
          .cost(abnormalCost)
          .build();

      given(depositService.writeDeposit(any(), any())).willReturn(1L);

      mockMvc.perform(post(DEPOSIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리가 없으면, 예산 작성에 실패한다.")
    void 카테고리가_없으면_예산_작성에_실패한다_404() throws Exception {
      int normalCost = 10000; // 정상 값

      DepositCreateReqDto reqDto = DepositCreateReqDto.builder()
          .categoryId(55L)
          .cost(normalCost)
          .build();

      given(depositService.writeDeposit(any(), any())).willThrow(
          new BusinessException(55L, "categoryId", ErrorCode.COST_CATEGORY_NOT_FOUND)
      );

      mockMvc.perform(post(DEPOSIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("예산 추천 시스템 관련 컨트롤러 테스트")
  class recommendDeposit {

    @Test
    @DisplayName("정상적으로 입력될 때, 예산 추천에 성공한다.")
    void 정상적으로_입력될_때_예산_추천에_성공한다_200() throws Exception {
      int normalCost = 101000; // 정상 값

      DepositRecommendReqDto reqDto = DepositRecommendReqDto.builder()
          .cost(normalCost)
          .build();

      List<DepositResultResDto> resultResDtoList = new ArrayList<>();
      resultResDtoList.add(new DepositResultResDto("외식", 19200));
      resultResDtoList.add(new DepositResultResDto("의료/건강", 78000));
      resultResDtoList.add(new DepositResultResDto("기타", 3800));
      int sumDepositMoney = 101000;

      DepositRecommendResultResDto resDto = new DepositRecommendResultResDto(sumDepositMoney,
          resultResDtoList);

      given(depositService.calculateRecommendDeposit(any())).willReturn(resDto);

      mockMvc.perform(get(DEPOSIT_URL + "/recommend")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예산 돈이 100원 단위가 아닐때, 예산 추천에 실패한다.")
    void 예산_돈이_100원_단위가_아닐때_예산_추천에_실패한다_400() throws Exception {
      int abnormalCost = 140040; // 비정상 값

      DepositRecommendReqDto reqDto = DepositRecommendReqDto.builder()
          .cost(abnormalCost)
          .build();

      int sumDepositMoney = 10100;
      List<DepositResultResDto> resultResDtoList = new ArrayList<>();
      resultResDtoList.add(new DepositResultResDto("외식", 19200));
      resultResDtoList.add(new DepositResultResDto("의료/건강", 78000));
      resultResDtoList.add(new DepositResultResDto("기타", 3800));

      DepositRecommendResultResDto resDto = new DepositRecommendResultResDto(sumDepositMoney,
          resultResDtoList);

      given(depositService.calculateRecommendDeposit(any())).willReturn(resDto);

      mockMvc.perform(get(DEPOSIT_URL + "/recommend")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(reqDto)))
          .andExpect(status().isBadRequest());
    }
  }
}