package com.utils;


import com.exception.AssemblyCatServiceException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名称: pay_core
 *
 * @ClassName ClassUtils
 * @Description
 * @Author renBo renbo@chinasofti.com
 * @Date 2022/4/27 16:22
 */
public class ClassUtils {
    /**
     * 获取类中所有的field
     * @param clazz
     * @return
     */
    public static List<Field> getAllField(Class clazz) {
        if (StringUtils.isEmpty(clazz)) {
            throw new AssemblyCatServiceException("");
        }
        Field[] fields = FieldUtils.getAllFields(clazz);
        if (fields != null && fields.length > 0) {
            return Arrays.asList(fields);
        }
        return new ArrayList<>();
    }


    /**
     * 获取Annotation
     * @param handlerMethod HandlerMethod
     * @param annotationType 注解类
     * @param <A> 泛型标记
     * @return {Annotation}
     */
    public static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        // 先找方法，再找方法上的类
        A annotation = handlerMethod.getMethodAnnotation(annotationType);
        if (null != annotation) {
            return annotation;
        }
        // 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
        Class<?> beanType = handlerMethod.getBeanType();
        return AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType);
    }
}