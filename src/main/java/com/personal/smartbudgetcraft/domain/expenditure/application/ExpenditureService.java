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

  /**
   * 지출 수정
   *
   * @param member        회원
   * @param expenditureId 수정할 지출 id
   * @param reqDto        수정할 지출 데이터 정보
   * @return 수정된 지출 id
   */
  @Transactional
  public Long updateExpenditure(Member member, Long expenditureId, ExpenditureWriteReqDto reqDto) {
    // 권한 확인
    validUserAccessExpenditure(member, expenditureId);

    // 해당 지출 찾기
    Expenditure foundExpenditure = getExpenditureById(expenditureId);

    // 해당 카테고리 찾기
    CostCategory foundCategory = getCategoryById(reqDto.getCategoryId());

    // 수정
    foundExpenditure.update(reqDto, foundCategory);

    return foundExpenditure.getId();

  }

  /**
   * id로 지출 찾기. 없으면 예외
   *
   * @param expenditureId 지출 Id
   * @return 찾은 지출
   */
  private Expenditure getExpenditureById(Long expenditureId) {
    Expenditure foundExpenditure = expenditureRepository.findById(expenditureId).orElseThrow(
        () -> new BusinessException(expenditureId, "expenditureId", ErrorCode.EXPENDITURE_NOT_FOUND)
    );
    return foundExpenditure;
  }

  /**
   * 회원이 해당 지출을 바꿀 수 있는 권한이 있는지 확인. 없으면 예외
   *
   * @param member        회원
   * @param expenditureId 대상 지출 Id
   */
  private void validUserAccessExpenditure(Member member, Long expenditureId) {
    boolean isDepositMatchMember = member.getExpenditures().stream()
        .anyMatch(expenditure -> expenditure.getId().equals(expenditureId));

    if (!isDepositMatchMember) {
      throw new BusinessException(expenditureId, "expenditureId",
          ErrorCode.ACCESS_DENIED_EXCEPTION);
    }
  }
}
