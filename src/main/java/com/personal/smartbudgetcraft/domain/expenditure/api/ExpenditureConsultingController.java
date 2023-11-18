package com.personal.smartbudgetcraft.domain.expenditure.api;

import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureConsultingService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.AdviceCommentResDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpendituresRecommendTodayResDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.security.annotation.LoginMember;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 지출 컨설팅 관련 컨트롤러
 */
@RestController
@RequestMapping("api/v1/expenditure/consulting")
@RequiredArgsConstructor
public class ExpenditureConsultingController {

  private final ExpenditureConsultingService expenditureConsultingService;

  /**
   * 오늘의 추천 지출을 카테고리별로 추천
   *
   * @param member 회원
   * @return 200, 추천된 카테고리별 지출
   */
  @GetMapping("/recommend")
  public ResponseEntity<ApiResDto> recommendTodayExpenditure(
      @LoginMember Member member
  ) {
    ExpendituresRecommendTodayResDto resDto = expenditureConsultingService.recommendTodayExpenditure(
        member);

    return ResponseEntity.ok(ApiResDto.toSuccessForm(resDto));
  }

  /**
   * 회원 상황에 맞는 멘트 호출
   *
   * @param member 회원
   * @return 200, 회원 상황에 맞는 멘트
   */
  @GetMapping("/advice")
  public ResponseEntity<ApiResDto> adviceComment(
      @LoginMember Member member
  ) {
    AdviceCommentResDto resDto = expenditureConsultingService.adviceComment(member);

    return ResponseEntity.ok(ApiResDto.toSuccessForm(resDto));
  }
}
