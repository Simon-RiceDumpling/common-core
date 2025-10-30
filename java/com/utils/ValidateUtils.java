package com.utils;


import com.exception.AssemblyCatServiceException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 项目名称: pay_core
 *
 * @ClassName ValidateUtils
 * @Description
 * @Author renBo renbo@chinasofti.com
 * @Date 2022/4/27 16:21
 */
public class ValidateUtils {
    /**
     * 校验对象<p/>
     * 根据对象中的@NotNull注解来判断
     *
     * @param object
     * @return
     */
    public static void validate(Object object) {

        List<Field> fields = ClassUtils.getAllField(object.getClass());
        validateFields(fields, object);
    }

    /**
     * 校验属性中是否存在空的数据
     *
     * @param list
     * @param data
     * @return
     */
    public static void validateFields(List<Field> list, Object data) {
        if (CollectionUtils.isEmpty(list)) {
            throw new AssemblyCatServiceException("获取不到对象的属性");
        }
        for (Field field : list) {
            if (!field.isAnnotationPresent(NotNull.class)) {
                continue;
            }
            boolean isAccessible = field.isAccessible();
            try {
                field.setAccessible(Boolean.TRUE);
                Object value = field.get(data);
                if (StringUtils.isEmpty(value)) {
                    throw new AssemblyCatServiceException("");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                field.setAccessible(isAccessible);
            }
        }
    }
}