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
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
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

  public static final int PERCENT_UNIT = 100;
  public static final int MIN_PERCENTAGE = 10;
  public static final String ETC_NAME = "기타";
  public static final double MIN_COST_UNIT = 100.0;

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
      Long depositId = updateDepositCost(reqDto.getCost(), optionalPrevDeposit.get(),
          member.getBudgetTracking());
      return depositId;
    }

    // 이전의 카테고리에 대한 금액이 없을 때
    // 카테고리 찾기
    CostCategory category = getCategoryById(reqDto.getCategoryId());
    // dto를 Entity로 변경
    Deposit deposit = reqDto.toEntity(member, category);

    // 저장 후 id 갖기
    Long depositId = depositRepository.save(deposit).getId();
    // 재산 트래킹 업데이트
    member.getBudgetTracking().updateDepositCost(reqDto.getCost());
    return depositId;
  }

  /**
   * 예산 금액 업데이트
   *
   * @param cost    추가할 금액
   * @param deposit 대상 예산
   * @return 업데이트 된 예산 id
   */
  private Long updateDepositCost(Integer cost, Deposit deposit, BudgetTracking budgetTracking) {
    deposit.addCost(cost);
    // 재산 트래킹 업데이트
    budgetTracking.updateDepositCost(cost);
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

  /**
   * 예산 설계 추천 시스템
   * TODO List 보단 Map 이 안전해 보임
   *
   * @param reqDto 추천에 필요한 데이터 정보
   * @return 추천 예산 설계 결과
   */
  public DepositRecommendResultResDto calculateRecommendDeposit(DepositRecommendReqDto reqDto) {

    // 1. 모든 카테고리 찾기
    List<CostCategory> allCategories = categoryRepository.findAll();

    // 2. 모든 카테고리의 예산 금액을 합친 총합 계산. (모든 유저의 예산 총 금액)
    int usersTotalSum = getTotalSumByCategories(allCategories);

    // 3. 카테고리별 통계 퍼센트 결과를 List로 저장
    List<Double> percentageResult = getPercentageResult(allCategories, usersTotalSum);

    // 4. 모든 예산 금액 추천의 결과를 List로 저장
    List<Integer> recommendResult = getRecommendResult(reqDto.getCost(), percentageResult);

    // 5. 예산 추천 결과를 원하는 API 형식으로 포맷팅
    List<DepositResultResDto> depositResultResDtoList = formatResult(
        allCategories, recommendResult, percentageResult);

    // 6. 총 금액 계산.
    int recommendTotalSum = recommendResult.stream().mapToInt(Integer::intValue).sum();

    // 7. ResDto 형식으로 변환.
    DepositRecommendResultResDto depositRecommendResultResDto = new DepositRecommendResultResDto(
        recommendTotalSum, depositResultResDtoList);

    return depositRecommendResultResDto;
  }

  /**
   * 예산 금액 추천을 계산
   * 카테고리의 예산 금액 = 반올림((예산 금액) * (해당 카테고리의 퍼센테이지 / 100)) / 최소 단위 금액) * 최소 단위 금액
   *
   * @param cost             예산 총 금액
   * @param percentageResult 카테고리 별 금액 퍼센트
   * @return 카테고리별 예산 금액 리스트
   */
  private List<Integer> getRecommendResult(
      int cost,
      List<Double> percentageResult
  ) {
    return percentageResult.stream()
        .map(percent -> {
          double categoryCost = cost * (percent / PERCENT_UNIT);
          double roundedUnitCost = Math.round((categoryCost / MIN_COST_UNIT)) * MIN_COST_UNIT;
          return (int) roundedUnitCost;
        })
        .toList();
  }

  /**
   * 예산 추천 결과를 원하는 API 형식으로 포맷팅
   *
   * @param allCategories    모든 카테고리
   * @param recommendResult  카테고리별 예산 금액 List
   * @param percentageResult 계산된 카테고리별 통계 퍼센트 List
   * @return 포맷팅된 예산 추천 결과
   */
  private List<DepositResultResDto> formatResult(
      List<CostCategory> allCategories,
      List<Integer> recommendResult,
      List<Double> percentageResult
  ) {
    // Response 할 dto 리스트
    List<DepositResultResDto> depositResultResDtoList = new ArrayList<>();
    // 기타의 금액
    int etcSum = 0;

    //  예산 추천 결과를 원하는 API 형식으로 포맷팅
    for (CostCategory category : allCategories) {
      // 사용할 index
      int idx = Math.toIntExact(category.getId()) - 1;
      // 카테고리 명
      String categoryName = allCategories.get(idx).getName();
      // 카테고리의 추천된 금액
      Integer recommendSum = recommendResult.get(idx);

      // 특정 퍼센트 미만일 경우 기타로 빠짐
      if (percentageResult.get(idx) < MIN_PERCENTAGE) {
        etcSum += recommendSum;
        continue;
      }

      // 특정 퍼센트 이상일 경우, dto로 변환
      DepositResultResDto depositResultResDto = DepositResultResDto.builder()
          .categoryName(categoryName)
          .cost(recommendSum)
          .build();

      // Response 할 dto 리스트에 변환된 dto 담기
      depositResultResDtoList.add(depositResultResDto);
    }

    // 기타 카테고리 존재 유무
    boolean isExistEtc = etcSum != 0;

    // 기타 카테고리가 있으면, 추가
    if (isExistEtc) {
      DepositResultResDto etcDepositDto = DepositResultResDto.builder()
          .categoryName(ETC_NAME)
          .cost(etcSum)
          .build();
      depositResultResDtoList.add(etcDepositDto);
    }

    return depositResultResDtoList;
  }

  /**
   * 카테고리별 통계 퍼센트를 계산
   *
   * @param allCategories 모든 카테고리
   * @param usersTotalSum 유저들의 총 예산 금액
   * @return 계산된 카테고리별 통계 퍼센트 List
   */
  private List<Double> getPercentageResult(
      List<CostCategory> allCategories,
      int usersTotalSum
  ) {
    return allCategories.stream()
        .map(CostCategory::getDeposits)
        .map(deposits ->
            deposits.stream()
                .mapToDouble(deposit -> ((double) deposit.getCost() / usersTotalSum) * PERCENT_UNIT)
                .sum())
        .toList();
  }

  /**
   * 카테고리로 모든 유저의 예산 금액 계산
   *
   * @param allCategories 모든 카테고리
   * @return 모든 유저의 예산 금액
   */
  private int getTotalSumByCategories(List<CostCategory> allCategories) {
    return allCategories.stream()
        .map(CostCategory::getDeposits)
        .mapToInt(deposits ->
            deposits.stream()
                .mapToInt(Deposit::getCost)
                .sum()
        ).sum();
  }

  /**
   * 예산 수정
   *
   * @param member    회원
   * @param depositId 수정할 예산 아이디
   * @param reqDto    수정 데이터 정보
   * @return 수정된 예산 아이디
   */
  @Transactional
  public Long updateDeposit(Member member, Long depositId, DepositCreateReqDto reqDto) {
    // 권한 확인
    validUserAccessDeposit(member, depositId);

    // 해당 예산 찾기
    Deposit foundDeposit = getDepositById(depositId);

    // 해당 카테고리 찾기
    CostCategory foundCategory = getCategoryById(reqDto.getCategoryId());

    // 수정
    foundDeposit.update(reqDto, foundCategory);
    // 재산 트래킹 업데이트
    member.getBudgetTracking().updateDepositCost(reqDto.getCost() - foundDeposit.getCost());

    return foundDeposit.getId();
  }

  /**
   * 아이디로 예산 찾기. 없으면 예외
   *
   * @param depositId 찾을 id
   * @return 찾은 예산
   */
  private Deposit getDepositById(Long depositId) {
    return depositRepository.findById(depositId).orElseThrow(
        () -> new BusinessException(depositId, "depositId", ErrorCode.DEPOSIT_NOT_FOUND)
    );
  }


  /**
   * 회원이 해당 예산을 바꿀 수 있는 권한이 있는지 확인. 없으면 예외
   *
   * @param member    회원
   * @param depositId 대상 예산
   */
  private void validUserAccessDeposit(Member member, Long depositId) {
    boolean isDepositMatchMember = member.getDeposits().stream()
        .anyMatch(deposit -> deposit.getId().equals(depositId));

    if (!isDepositMatchMember) {
      throw new BusinessException(depositId, "depositId", ErrorCode.ACCESS_DENIED_EXCEPTION);
    }
  }

  /**
   * 예산 삭제
   *
   * @param member    회원
   * @param depositId 삭제할 예산
   */
  @Transactional
  public void deleteDeposit(Member member, Long depositId) {
    // 권한 확인
    validUserAccessDeposit(member, depositId);

    // 예산 찾기
    Deposit foundDeposit = getDepositById(depositId);

    depositRepository.delete(foundDeposit);
    // 재산 트래킹 업데이트
    member.getBudgetTracking().updateDepositCost(-foundDeposit.getCost());
  }
}
