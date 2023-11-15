package com.personal.smartbudgetcraft.domain.expenditure.application;

import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.dao.ExpenditureRepository;
import com.personal.smartbudgetcraft.domain.expenditure.dto.request.ExpenditureWriteReqDto;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenditureService {

  private final ExpenditureRepository expenditureRepository;
  private final CostCategoryRepository categoryRepository;

  /**
   * 지출 작성
   *
   * @param member 회원
   * @param reqDto 지출 작성에 필요한 데이터 정보
   * @return 작성된 지출의 id
   */
  @Transactional
  public Long writeExpenditure(Member member, ExpenditureWriteReqDto reqDto) {
    // 카테고리 찾기
    CostCategory category = getCategoryById(reqDto.getCategoryId());
    // Entity 생성
    Expenditure expenditure = reqDto.toEntity(category, member);

    Long wroteExpenditureId = expenditureRepository.save(expenditure).getId();
    return wroteExpenditureId;
  }

  /**
   * Id로 비용 카테고리 찾기
   *
   * @param categoryId 카테고리 id
   * @return 찾은 카테고리
   */
  private CostCategory getCategoryById(Long categoryId) {
    CostCategory category = categoryRepository.findById(categoryId).orElseThrow(
        () -> new BusinessException(categoryId, "categoryId", ErrorCode.COST_CATEGORY_NOT_FOUND)
    );

    return category;
  }
}
