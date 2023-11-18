package com.personal.smartbudgetcraft.domain.member.dao;

import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  /**
   * 계정 아이디로 회원 찾기
   *
   * @return Optional 회원
   */
  Optional<Member> findByAccount(String account);


  /**
   * 나를 제외한 모든 회원들의 총 지출 list 구하기
   *
   * @param member 제외할 회원 (나)
   * @return 나를 제외한 모든 회원들의 총 지출 List
   */
  @Query("SELECT b.totalExpenditureCost "
      + "FROM Member m "
      + "JOIN FETCH BudgetTracking b "
      + "ON m.budgetTracking.id = b.id "
      + "WHERE m != :member"
  )
  List<Integer> findAllExpenditureCostWithoutMe(@Param("member") Member member);
}
