package com.annotation.pattern;

import com.annotation.pattern.validator.PatternForEnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author admin
 */
@Constraint(validatedBy = PatternForEnumValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PatternForEnum {
    /**
     * 枚举 指的是用那个枚举
     */
    Class<? extends Enum<?>> enumClass();
    /**
     * 用枚举的那个字段作比较
     */
    String equalsValue();
    /**
     * 返回信息 不填会有默认信息
     */
    String message() default "";


    //  ---------基礎字段
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
