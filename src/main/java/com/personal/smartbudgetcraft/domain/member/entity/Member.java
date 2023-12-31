package com.personal.smartbudgetcraft.domain.member.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.personal.smartbudgetcraft.domain.deposit.entity.Deposit;
import com.personal.smartbudgetcraft.domain.expenditure.entity.Expenditure;
import com.personal.smartbudgetcraft.domain.member.entity.budgettracking.BudgetTracking;
import com.personal.smartbudgetcraft.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원의 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class Member extends BaseEntity {

  public static final int MAX_ACCOUNT_LENGTH = 20;
  public static final int MAX_PASSWORD_LENGTH = 256;

  // 유저의 아이디
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "member_id", nullable = false)
  private Long id;

  // 재산 트래킹 (1:1)
  // 맨 처음 모든 속성 0으로 초기화
  @OneToOne(fetch = LAZY, cascade = ALL, orphanRemoval = true)
  @JoinColumn(name = "budget_tracking_id", nullable = false)
  private BudgetTracking budgetTracking;

  // 계정의 아이디
  @Column(name = "account", nullable = false, length = MAX_ACCOUNT_LENGTH)
  private String account;

  // 비밀번호
  @Column(name = "password", nullable = false, length = MAX_PASSWORD_LENGTH)
  private String password;

  // 권한
  @Enumerated(STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  // 예산 (1:N)
  @OneToMany(fetch = LAZY, mappedBy = "member")
  private List<Deposit> deposits = new ArrayList<>();

  // 지출 (1:N)
  @OneToMany(fetch = LAZY, mappedBy = "member")
  private List<Expenditure> expenditures = new ArrayList<>();

  @Builder

  public Member(Long id, BudgetTracking budgetTracking, String account, String password, Role role,
      List<Deposit> deposits, List<Expenditure> expenditures) {
    this.id = id;
    this.budgetTracking = budgetTracking;
    this.account = account;
    this.password = password;
    this.role = role;
    this.deposits = deposits;
    this.expenditures = expenditures;
  }
}