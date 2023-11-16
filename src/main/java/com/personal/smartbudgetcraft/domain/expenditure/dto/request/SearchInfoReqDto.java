package com.personal.smartbudgetcraft.domain.expenditure.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

/**
 * 필터링 정보를 담는 데이터 정보
 */
@Getter
public class SearchInfoReqDto {

  // 페이지 정보
  private Pageable pageable;
  // 시작 날짜
  private LocalDateTime startDate;
  // 마지막 날짜
  private LocalDateTime endDate;
  // 카테고리 Id
  private Long categoryId;
  // 최소 금액
  private Integer minCost;
  // 최대 금액
  private Integer maxCost;

  @Builder
  public SearchInfoReqDto(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate,
      Long categoryId, Integer minCost, Integer maxCost) {
    this.pageable = pageable;
    this.startDate = startDate;
    this.endDate = endDate;
    this.categoryId = categoryId;
    this.minCost = minCost;
    this.maxCost = maxCost;
  }
}
