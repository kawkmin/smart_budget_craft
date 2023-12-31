package com.personal.smartbudgetcraft.domain.deposit.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.personal.smartbudgetcraft.domain.category.cost.entity.CostCategory;
import com.personal.smartbudgetcraft.domain.deposit.dto.request.DepositCreateReqDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예산
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "deposit")
public class Deposit {

  // 예산 id
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "deposit_id", nullable = false)
  private Long id;

  // 비용 카테고리 (N:1)
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "cost_category_id", nullable = false)
  private CostCategory category;

  // 회원 (N:1)
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 예산 금액
  @Column(name = "cost", nullable = false)
  private Integer cost;

  @Builder
  public Deposit(Long id, CostCategory category, Member member, Integer cost) {
    this.id = id;
    this.category = category;
    this.member = member;
    this.cost = cost;
  }

  /**
   * 금액을 추가
   *
   * @param cost 추가할 금액
   */
  public void addCost(Integer cost) {
    this.cost += cost;
  }

  /**
   * 예산 수정
   *
   * @param reqDto   수정할 데이터 정보
   * @param category 수정할 카테고리
   */
  public void update(DepositCreateReqDto reqDto, CostCategory category) {
    this.cost = reqDto.getCost();
    this.category = category;
  }
}
