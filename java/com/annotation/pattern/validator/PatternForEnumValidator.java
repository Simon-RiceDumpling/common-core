package com.annotation.pattern.validator;

import com.annotation.pattern.PatternForEnum;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author admin
 */
public class PatternForEnumValidator implements ConstraintValidator<PatternForEnum, Object> {
    private Class<? extends Enum<?>> enumClass;

    private String message;

    private String equalsValue;

    @Override
    public void initialize(PatternForEnum patternForEnum) {
        this.enumClass = patternForEnum.enumClass();
        this.equalsValue = patternForEnum.equalsValue();
        this.message = patternForEnum.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // disallow null values
        }
        try {
            Method method = enumClass.getMethod("get" + Character.toUpperCase(equalsValue.charAt(0)) + equalsValue.substring(1));
            Object[] enumValues = enumClass.getEnumConstants();
            boolean isValid = Arrays.stream(enumValues)
                    .anyMatch(enumValue -> {
                        try {
                            return value.equals(method.invoke(enumValue));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            if (!isValid && StringUtils.isEmpty(message)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("---请检查输入值是否在输入范围以内" + getValidValues(enumValues, method))
                        .addConstraintViolation();
            }
            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    private String getValidValues(Object[] enumValues, Method getFieldMethod) throws Exception {
        return Arrays.stream(enumValues)
                .map(enumValue -> {
                    try {
                        return getFieldMethod.invoke(enumValue).toString();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

}
