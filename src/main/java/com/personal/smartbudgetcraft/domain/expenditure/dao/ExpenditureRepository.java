package com.personal.smartbudgetcraft.domain.expenditure.dao;

import com.personal.smartbudgetcraft.domain.expenditure.dao.querydsl.ExpenditureRepositoryCustom;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long>,
    ExpenditureRepositoryCustom {

  /**
   * 회원의 특정 기간동안의 지출 구하기
   *
   * @param startDate 시작 일
   * @param endDate   마지막 일
   * @param member    회원
   * @return 회원의 특정 기간동안의 지출
   */
  List<Expenditure> findAllByTimeBetweenAndMember(LocalDateTime startDate, LocalDateTime endDate,
      Member member);
}
