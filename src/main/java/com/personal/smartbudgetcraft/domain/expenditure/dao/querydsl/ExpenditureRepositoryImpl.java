package com.personal.smartbudgetcraft.domain.expenditure.dao.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

/**
 * 지출 관련 QueryDsl을 사용할 DAO 구현체
 */
public class ExpenditureRepositoryImpl implements ExpenditureRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public ExpenditureRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }
}
