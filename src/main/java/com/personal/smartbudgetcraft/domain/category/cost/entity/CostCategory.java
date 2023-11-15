package com.personal.smartbudgetcraft.domain.category.cost.entity;

import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비용 카테고리
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "cost_category")
public class CostCategory {

  // 카테고리 id
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cost_category_id", nullable = false)
  private Long id;

  // 카테고리 이름
  @Column(name = "name", nullable = false)
  private String name;

  // 예산 (N:1)
  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
  private List<Deposit> deposits = new ArrayList<>();

  @Builder
  public CostCategory(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}