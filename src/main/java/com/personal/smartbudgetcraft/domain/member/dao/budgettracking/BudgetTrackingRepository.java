package com.personal.smartbudgetcraft.domain.member.dao.budgettracking;

import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetTrackingRepository extends JpaRepository<BudgetTracking, Long> {

}
