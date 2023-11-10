package com.personal.smartbudgetcraft.domain.member.dao;

import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  /**
   * 계정 아이디로 회원 찾기
   *
   * @return Optional 회원
   */
  Optional<Member> findByAccount(String account);
}
