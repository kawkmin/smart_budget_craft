package com.personal.smartbudgetcraft.domain.deposit.api;

import com.personal.smartbudgetcraft.domain.deposit.application.DepositService;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositCreateReqDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositRecommendReqDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.response.DepositRecommendResultResDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.security.annotation.LoginMember;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/deposit")
@RequiredArgsConstructor
public class DepositController {

  private final DepositService depositService;

  /**
   * 예산 설정
   *
   * @param member 회원
   * @param reqDto 예산 설정에 필요한 데이터 정보
   * @return 201, 생성된 예산의 Id
   */
  @PostMapping
  public ResponseEntity<ApiResDto> writeDeposit(
      @LoginMember Member member,
      @Valid @RequestBody DepositCreateReqDto reqDto
  ) {
    Long depositId = depositService.writeDeposit(member, reqDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResDto.toSuccessForm(depositId));
  }

  /**
   * 예산 설계 추천 시스템
   *
   * @param reqDto 예산 추천에 필요한 데이터 정보
   * @return 200, 추천된 예산 정보
   */
  @GetMapping("/recommend")
  public ResponseEntity<ApiResDto> recommendDeposit(
      @Valid @RequestBody DepositRecommendReqDto reqDto
  ) {
    DepositRecommendResultResDto resDto = depositService.calculateRecommendDeposit(reqDto);

    return ResponseEntity.ok(ApiResDto.toSuccessForm(resDto));
  }

  /**
   * 예산 수정
   *
   * @param member    회원
   * @param depositId 수정할 예산 아이디
   * @param reqDto    수정 데이터 정보
   * @return 201, 수정된 예산 아이디
   */
  @PutMapping("/{depositId}")
  public ResponseEntity<ApiResDto> updateDeposit(
      @LoginMember Member member,
      @PathVariable(name = "depositId") Long depositId,
      @Valid @RequestBody DepositCreateReqDto reqDto
  ) {
    depositService.updateDeposit(member, depositId, reqDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResDto.toSuccessForm(depositId));
  }
}
