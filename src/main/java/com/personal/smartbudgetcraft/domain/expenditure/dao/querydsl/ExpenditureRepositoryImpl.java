package com.personal.smartbudgetcraft.domain.expenditure.dao.querydsl;

import static com.personal.smartbudgetcraft.domain.expenditure.entity.QExpenditure.expenditure;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * 지출 관련 QueryDsl을 사용할 DAO 구현체
 */
public class ExpenditureRepositoryImpl implements ExpenditureRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public ExpenditureRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  /**
   * 지출 필터링 및 목록 조회의 동적 쿼리
   *
   * @param pageable  페이지 정보
   * @param member    회원
   * @param category  카테고리
   * @param startDate 시작일
   * @param endDate   마지막일
   * @param minCost   최소 금액
   * @param maxCost   최대 금액
   * @return 필터링 된 지출 목록
   */
  @Override
  public Page<Expenditure> searchExpenditures(
      Pageable pageable,
      Member member,
      CostCategory category,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer minCost,
      Integer maxCost
  ) {
    List<Expenditure> expenditures = queryFactory
        .select(expenditure)
        .from(expenditure)
        .where(
            expenditure.member.eq(member),
            categoryEq(category),
            startDateEq(startDate),
            endDateEq(endDate),
            minCostEq(minCost),
            maxCostEq(maxCost)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(expenditure.count())
        .from(expenditure)
        .where(
            expenditure.member.eq(member),
            categoryEq(category),
            startDateEq(startDate),
            endDateEq(endDate),
            minCostEq(minCost),
            maxCostEq(maxCost)
        )
        .fetchOne();

    return new PageImpl<>(expenditures, pageable, total);
  }

  private BooleanExpression categoryEq(CostCategory category) {
    return category != null ? expenditure.category.eq(category) : null;
  }

  private BooleanExpression startDateEq(LocalDateTime startDate) {
    return startDate != null ? expenditure.time.after(startDate) : null;
  }

  private BooleanExpression endDateEq(LocalDateTime endDate) {
    return endDate != null ? expenditure.time.before(endDate) : null;
  }

  private BooleanExpression minCostEq(Integer minCost) {
    return minCost != null ? expenditure.cost.goe(minCost) : null;
  }

  private BooleanExpression maxCostEq(Integer maxCost) {
    return maxCost != null ? expenditure.cost.loe(maxCost) : null;
  }
}
