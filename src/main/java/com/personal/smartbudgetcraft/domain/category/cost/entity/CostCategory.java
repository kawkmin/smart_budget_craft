package com.personal.smartbudgetcraft.domain.category.cost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
}
