package com.personal.smartbudgetcraft.domain.deposit.application;

import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.dao.DepositRepository;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositCreateReqDto;
import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositService {

  private final DepositRepository depositRepository;
  private final CostCategoryRepository categoryRepository;

  /**
   * 예산 설정
   * 이전의 카테고리에 대한 금액이 있을 때, 금액을 합쳐야함.
   *
   * @param member 회원
   * @param reqDto 예산 설정에 필요한 데이터 정보
   * @return 생성된 예산의 Id
   */
  @Transactional
  public Long writeDeposit(Member member, DepositCreateReqDto reqDto) {
    // 카테고리 찾기
    CostCategory category = getCategoryById(reqDto.getCategoryId());

    // 이전의 카테고리에 대한 금액이 있을 때, 업데이트 로직 수행
    Optional<Deposit> optionalDeposit = depositRepository.findByCategory(category);
    if (optionalDeposit.isPresent()) {
      return updateDepositCost(reqDto.getCost(), optionalDeposit.get());
    }

    // 이전의 카테고리에 대한 금액이 없을 때
    // dto를 Entity로 변경
    Deposit deposit = reqDto.toEntity(member, category);

    // 저장 후 id 갖기
    Long depositId = depositRepository.save(deposit).getId();
    return depositId;
  }

  /**
   * 예산 금액 업데이트
   *
   * @param cost    추가할 금액
   * @param deposit 대상 예산
   * @return 업데이트 된 예산 id
   */
  private Long updateDepositCost(Integer cost, Deposit deposit) {
    deposit.addCost(cost);
    return deposit.getId();
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
