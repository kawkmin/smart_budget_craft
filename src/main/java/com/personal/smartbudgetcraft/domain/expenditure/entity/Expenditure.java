package com.personal.smartbudgetcraft.domain.expenditure.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지출
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "expenditure")
public class Expenditure {

  // 지출 id
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "expenditure_id", nullable = false)
  private Long id;

  // 비용 카테고리 (N:1)
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "cost_category_id", nullable = false)
  private CostCategory category;

  // 회원 (N:1)
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 지출 금액
  @Column(name = "cost", nullable = false)
  private Integer cost;

  // 메모
  @Column(name = "memo", length = 100, nullable = false)
  private String memo;

  // 합계 제외 여부
  @Column(name = "is_excluded", nullable = false)
  private Boolean isExcluded;

  // 지출 시간
  @Column(name = "time", nullable = false)
  private LocalDateTime time;
}
