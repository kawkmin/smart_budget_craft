package com.personal.smartbudgetcraft.global.config.valid.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 돈 단위를 확인하는 validator 설정
 */
public class MoneyUnitValidator implements ConstraintValidator<MoneyUnit, Integer> {

  // 100원 단위로 확인
  public static final int MONEY_UNIT = 100;

  @Override
  public void initialize(MoneyUnit constraintAnnotation) {
    // 초기화 작업이 필요한 경우에 작성
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return value != null && value % MONEY_UNIT == 0;
  }
}