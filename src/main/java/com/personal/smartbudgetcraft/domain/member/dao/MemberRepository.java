package com.personal.smartbudgetcraft.domain.member.dao;

import com.personal.smartbudgetcraft.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
