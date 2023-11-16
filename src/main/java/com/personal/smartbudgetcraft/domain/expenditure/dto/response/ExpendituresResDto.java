package com.personal.smartbudgetcraft.domain.expenditure.dto.response;

import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * 지출 목록 결과을 전달하는 데이터 정보
 */
@Getter
public class ExpendituresResDto {

  // 지출 목록
  private List<ExpenditureResDto> expenditures;
  // 총 금액
  private Integer totalCost;
  // 총 검색 개수
  private Long totalElements;
  // 총 페이지 수
  private Integer totalPages;
  // 현재 페이지
  private Integer currentPage;
  
  public ExpendituresResDto(Page<Expenditure> foundExpenditures, int totalCost) {
    List<ExpenditureResDto> expenditureResDtos = foundExpenditures.getContent().stream()
        .map(ExpenditureResDto::new)
        .collect(Collectors.toList());

    this.expenditures = expenditureResDtos;
    this.totalCost = totalCost;
    this.totalElements = foundExpenditures.getTotalElements();
    this.totalPages = foundExpenditures.getTotalPages();
    this.currentPage = foundExpenditures.getPageable().getPageNumber();
  }
}
