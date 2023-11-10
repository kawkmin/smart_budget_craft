package com.personal.smartbudgetcraft.domain.member.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.personal.smartbudgetcraft.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

}