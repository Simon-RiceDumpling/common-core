package com.annotation.pattern.validator;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.annotation.pattern.PatternForNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author admin
 */
public class PatternForNumberValidator implements ConstraintValidator<PatternForNumber, Number> {

    private long min;
    private long max;
    private Set<Long> allowedValues;

    @Override
    public void initialize(PatternForNumber annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
        this.allowedValues = new HashSet<>();
        Arrays.stream(annotation.allowedValues()).forEach(allowedValues::add);
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 可以根据需要调整，如果不允许为空则返回 false
        }
        if (CollectionUtil.isNotEmpty(allowedValues)) {
            return allowedValues.contains(value.longValue());
        }
        return value.longValue() >= min && value.longValue() <= max;
    }
}
