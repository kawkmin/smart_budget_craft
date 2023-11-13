package com.personal.smartbudgetcraft.global.config.valid.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 돈 단위를 확인하는 어노테이션
 * 100원 단위로 설정
 */
@Documented
@Constraint(validatedBy = MoneyUnitValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MoneyUnit {

  String message() default "금액은 100원 단위로 입력해야 합니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
