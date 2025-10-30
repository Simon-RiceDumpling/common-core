package com.aspect;

import cn.hutool.core.util.ObjectUtil;

import com.annotation.SpecialHandler;
import com.utils.UrlUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @ClassNAME CatSpecialHandlerResponse
 * @Description 对返回值做特殊处理
 * @Author renbo.ren@cyberklick.com
 * @Date 2023/2/15 13:41
 */

@ControllerAdvice
@Slf4j
public class SpecialHandlerResponseBody implements ResponseBodyAdvice {

    @Override
    public Object beforeBodyWrite(Object resBody,
                                  MethodParameter arg1,
                                  MediaType arg2,
                                  Class arg3,
                                  ServerHttpRequest req,
                                  ServerHttpResponse res) {
        if (ObjectUtil.isNotEmpty(resBody) && resBody.getClass().isAnnotationPresent(SpecialHandler.class)) {
            changeAttribute(resBody);
        }
        return resBody;
    }


    @SneakyThrows
    private static void changeAttribute(Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object value = declaredField.get(obj);
            if (ObjectUtil.isEmpty(value)) {
                continue;
            }
            if (isBaseType(value)) {
                continue;
            }
            //基本数据类型
            if (value instanceof String) {
                if (declaredField.isAnnotationPresent(SpecialHandler.class)) {
                    SpecialHandler annotation = declaredField.getAnnotation(SpecialHandler.class);
                    if (annotation.encode()) {
                        declaredField.set(obj, UrlUtils.encode(value.toString()));
                    }
                    if (annotation.base64()) {
                        declaredField.set(obj, Base64Util.encode((String) value));
                    }
                }
                continue;
            }
            //判断是否为集合类型
            if (value instanceof Collection) {
                ((Collection) value).stream().forEach(SpecialHandlerResponseBody::changeAttribute);
                continue;
            }
            //非基本数据类型 递归
            changeAttribute(value);
        }

    }

    @Override
    public boolean supports(MethodParameter arg0, Class arg1) {
        //这里直接返回true,表示对任何handler的responsebody都调用beforeBodyWrite方法
        return true;
    }


    /**
     * 判断object是否为基本类型
     *
     * @param object
     * @return
     */
    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        return (className.equals(Integer.class) ||
                className.equals(Byte.class) ||
                className.equals(Long.class) ||
                className.equals(Double.class) ||
                className.equals(Float.class) ||
                className.equals(Character.class) ||
                className.equals(Short.class) ||
                className.equals(Boolean.class));
    }

}
