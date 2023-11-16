package com.personal.smartbudgetcraft.domain.expenditure.dao.querydsl;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

/**
 * 지출 관련 QueryDsl을 사용할 DAO 인터페이스
 */
public interface ExpenditureRepositoryCustom {

  // 지출 목록 필터링 및 검색
  Page<Expenditure> searchExpenditures(
      Pageable pageable,
      @Param("member") Member member,
      @Param("category") CostCategory category,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("minCost") Integer minCost,
      @Param("maxCost") Integer maxCost
  );
}
