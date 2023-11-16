package com.personal.smartbudgetcraft.domain.expenditure.dao;

import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
