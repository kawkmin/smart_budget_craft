package com.personal.smartbudgetcraft.domain.expenditure.api;

import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.request.ExpenditureWriteReqDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.security.annotation.LoginMember;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenditure")
public class ExpenditureController {

  private final ExpenditureService expenditureService;

  /**
   * 지출 작성
   *
   * @param member 회원
   * @param reqDto 지출 작성에 필요한 데이터 정보
   * @return 201, 작성된 지출의 id
   */
  @PostMapping
  private ResponseEntity<ApiResDto> writeExpenditure(
      @LoginMember Member member,
      @Valid @RequestBody ExpenditureWriteReqDto reqDto
  ) {
    Long wroteExpenditureId = expenditureService.writeExpenditure(member, reqDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResDto.toSuccessForm(wroteExpenditureId));
  }

  /**
   * 지출 수정
   *
   * @param member        회원
   * @param expenditureId 수정할 지출 id
   * @param reqDto        수정할 지출 데이터 정보
   * @return 201, 수정된 지출 id
   */
  @PutMapping("/{expenditureId}")
  public ResponseEntity<ApiResDto> updateExpenditure(
      @LoginMember Member member,
      @PathVariable(name = "expenditureId") Long expenditureId,
      @Valid @RequestBody ExpenditureWriteReqDto reqDto
  ) {
    Long updatedExpenditureId = expenditureService.updateExpenditure(member, expenditureId, reqDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResDto.toSuccessForm(updatedExpenditureId));
  }

  /**
   * 지출 합계 제외 여부 변경
   *
   * @param member        회원
   * @param expenditureId 합계 제외를 변경할 지출
   * @param isExclude     합계 제외 여부
   * @return 200
   */
  @PatchMapping("/{expenditureId}/exclude")
  public ResponseEntity<ApiResDto> updateExclude(
      @LoginMember Member member,
      @PathVariable(name = "expenditureId") Long expenditureId,
      @NotNull @RequestParam Boolean isExclude
  ) {
    expenditureService.updateExclude(member, expenditureId, isExclude);

    return ResponseEntity.ok(ApiResDto.toSuccessForm(""));
  }

}
