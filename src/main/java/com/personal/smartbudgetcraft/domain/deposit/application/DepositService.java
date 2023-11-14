package com.personal.smartbudgetcraft.domain.deposit.application;

import com.personal.smartbudgetcraft.domain.category.cost.dao.CostCategoryRepository;
import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.dao.DepositRepository;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositCreateReqDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositRecommendReqDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.response.DepositRecommendResultResDto;
import com.personal.smartbudgetcraft.domain.deposit.dto.response.DepositResultResDto;
import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import java.util.ArrayList;
import java.util.List;
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
    // 이전 카테고리에 대한 예산 optional
    Optional<Deposit> optionalPrevDeposit = member.getDeposits().stream()
        .filter(deposit -> deposit.getCategory().getId().equals(reqDto.getCategoryId()))
        .findFirst();

    // 이전의 카테고리에 대한 금액이 있을 때, 업데이트 로직 수행
    if (optionalPrevDeposit.isPresent()) {
      return updateDepositCost(reqDto.getCost(), optionalPrevDeposit.get());
    }

    // 이전의 카테고리에 대한 금액이 없을 때
    // 카테고리 찾기
    CostCategory category = getCategoryById(reqDto.getCategoryId());
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

  public DepositRecommendResultResDto calculateRecommendDeposit(DepositRecommendReqDto reqDto) {
    int money = reqDto.getCost();

    // 1. 모든 카테고리 찾기
    List<CostCategory> allCategories = categoryRepository.findAll();

    // 2. 모든 카테고리의 예산 금액을 합친 총합 계산.
    int totalSum = allCategories.stream()
        .map(CostCategory::getDeposits)
        .mapToInt(deposits ->
            deposits.stream()
                .mapToInt(Deposit::getCost)
                .sum()
        ).sum();

    // 3. 카테고리별 통계 퍼센트를 계산하여 List에 저장
    List<Double> percentageResult = allCategories.stream()
        .map(CostCategory::getDeposits)
        .map(deposits ->
            deposits.stream()
                .mapToDouble(deposit -> ((double) deposit.getCost() / totalSum) * 100)
                .sum())
        .toList();

    // 4. 카테고리별 통계 퍼센트가 계산된 List로 예산 금액 추천을 List로 저장
    List<Integer> recommendResult = percentageResult.stream()
        .map(percent -> (int) (money * (percent / 100)))
        .toList();

    // 5. Res dto로 변경
    List<DepositResultResDto> depositResultResDtoList = new ArrayList<>();
    int etcSum = 0;
    for (CostCategory category : allCategories) {
      int idx = Math.toIntExact(category.getId()) - 1;
      String categoryName = allCategories.get(idx).getName();
      Integer recommendSum = recommendResult.get(idx);
      if (percentageResult.get(idx) < 10) {
        etcSum += recommendSum;
        continue;
      }
      DepositResultResDto depositResultResDto = DepositResultResDto.builder()
          .categoryName(categoryName)
          .cost(recommendSum)
          .build();
      depositResultResDtoList.add(depositResultResDto);
    }

    if (etcSum != 0) {
      DepositResultResDto etcDepositDto = DepositResultResDto.builder()
          .categoryName("기타")
          .cost(etcSum)
          .build();
      depositResultResDtoList.add(etcDepositDto);
    }

    return new DepositRecommendResultResDto(money, depositResultResDtoList);
  }
}
