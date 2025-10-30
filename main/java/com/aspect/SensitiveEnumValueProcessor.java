package com.aspect;


import com.annotation.SensitiveEnum;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author renbo
 */
@Component
public class SensitiveEnumValueProcessor {

    public Object process(Object object) throws Exception {
        if (object == null) {
            return null;
        }
        if (object instanceof Collection<?>) {
            for (Object item : (Collection<?>) object) {
                process(item);
            }
        } else if (object instanceof Map<?, ?>) {
            for (Object entry : ((Map<?, ?>) object).entrySet()) {
                Map.Entry<?, ?> mapEntry = (Map.Entry<?, ?>) entry;
                process(mapEntry.getValue());
            }
        } else {
            processFields(object);
        }
        return object;
    }

    private void processFields(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(SensitiveEnum.class)) {
                SensitiveEnum sensitiveEnum = field.getAnnotation(SensitiveEnum.class);
                Field codeField = clazz.getDeclaredField(sensitiveEnum.codeField());
                codeField.setAccessible(true);
                Object codeValue = codeField.get(object);
                if (codeValue != null) {
                    Object enumValue = getEnumValueByCode(sensitiveEnum, codeValue);
                    field.set(object, enumValue);
                }
            } else if (field.getType().isAssignableFrom(Collection.class) || field.getType().isAssignableFrom(Map.class)) {
                process(field.get(object));
            } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                process(field.get(object));
            }
        }
    }


    /**
     * 根据 code 获取对应的枚举值
     * @param sensitiveEnum 注解实例
     * @param code 用于匹配的 code
     * @return 对应的枚举值
     * @throws Exception 反射调用过程中可能抛出的异常
     */
    private Object getEnumValueByCode(SensitiveEnum sensitiveEnum, Object code) throws Exception {
        // 获取枚举类
        Class<? extends Enum<?>> enumClass = sensitiveEnum.enumClass();
        // 获取枚举类中的 getCode 和 getValue 方法
        Method getCodeMethod = enumClass.getMethod(
                "get" + Character.toUpperCase(sensitiveEnum.codeFieldForEnum().charAt(0)) + sensitiveEnum.codeFieldForEnum().substring(1)
        );
        Method getValueMethod = enumClass.getMethod(
                "get" + Character.toUpperCase(sensitiveEnum.valueFieldForEnum().charAt(0)) + sensitiveEnum.valueFieldForEnum().substring(1)
        );
        // 使用 Stream API 进行枚举常量的过滤和映射
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumConstant -> {
                    try {
                        // 检查枚举常量的 code 是否匹配
                        return code.equals(getCodeMethod.invoke(enumConstant));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .map(enumConstant -> {
                    try {
                        // 返回枚举常量的 value
                        return getValueMethod.invoke(enumConstant);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(null);
    }
}
