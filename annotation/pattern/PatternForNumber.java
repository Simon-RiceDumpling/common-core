package com.annotation.pattern;

import com.annotation.pattern.validator.PatternForNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author RenBo
 * @Date 2024-06-14 18:03
 * @PackageName:com.annotation
 * @ClassName: Range
 * @Description: TODO
 * @Version 1.0
 */


@Documented
@Constraint(validatedBy = PatternForNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PatternForNumber {
    String message() default "数值不在允许的范围内";
    //在指定了值的情况下 范围值没有用
    long[] allowedValues() default {};
    //最小值
    long min() default Long.MIN_VALUE;
    //最大值
    long max() default Long.MAX_VALUE;

    //  ---------基礎字段
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

