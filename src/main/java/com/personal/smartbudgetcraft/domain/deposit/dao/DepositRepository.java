package com.personal.smartbudgetcraft.domain.deposit.dao;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

  Optional<Deposit> findByCategory(CostCategory category);
}
