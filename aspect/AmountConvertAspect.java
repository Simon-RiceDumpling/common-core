package com.aspect;
import com.annotation.Amount;
import com.annotation.AmountConvert;
import com.enums.MeasureEnum;
import com.utils.AmountChangeUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 项目名称: interactive-adserver
 *
 * @ClassName AmountConvertAspect
 * @Description
 * @Date 2022年8月25日13:29:33
 */
@Aspect
@Component
@Slf4j
public class AmountConvertAspect {


    public static List<String> baseTypes = new ArrayList<>();

    static {
        baseTypes.add("java.lang.String");
        baseTypes.add("java.lang.Character");
        baseTypes.add("java.lang.Byte");
        baseTypes.add("java.lang.Boolean");
        baseTypes.add("java.lang.Integer");
        baseTypes.add("java.lang.Long");
        baseTypes.add("java.lang.Float");
        baseTypes.add("java.lang.Double");
        baseTypes.add("java.lang.Void");
    }

    /**
     * 拦截所有带有@AmountConvert注解的方法
     */
    @Pointcut("@annotation(com.annotation.AmountConvert)")
    private void serviceAspect() {
    }

    /**
     * 拦截逻辑
     */
    @SneakyThrows
    @Around(value = "serviceAspect()")
    public Object around(ProceedingJoinPoint point) {
        //定义初始化返回数据
        Object resObj = point.proceed();
        try {
            //获取方法
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            //获取注解@AmountConvert
            AmountConvert convert = methodSignature.getMethod().getAnnotation(AmountConvert.class);
            //执行金额转换 需要传递注解AmountConvert中的参数
            doConvertAmount(resObj, convert.originMeasure(), convert.targetMeasure());
            //转换完成之后直接返回
            return resObj;
        } catch (Exception e) {
            //金额转换抛出异常 直接返回原始返回结果集
            log.error("金额转化异常直接返回原始数据:【{}】", e.getMessage());
            return resObj;
        }
    }

    /**
     * 执行金额转换
     */
    @SneakyThrows
    private static void doConvertAmount(Object resObj, MeasureEnum originMeasure, MeasureEnum targetMeasure) {
        Arrays.stream(resObj.getClass().getDeclaredFields())
                .forEach(declaredField -> {
                    try {
                        declaredField.setAccessible(true);
                        if (baseTypes.contains(declaredField.getType().getName())) {
                            //如果是基本数据类型
                            if (declaredField.isAnnotationPresent(Amount.class)) {
                                //该属性带有@Amount 证明该属性是个金额字段 根据属性转换
                                declaredField.set(resObj, AmountChangeUtils.convert(declaredField.get(resObj), originMeasure, targetMeasure));
                            }
                        } else if (Collection.class.isAssignableFrom(declaredField.getType())) {
                            //如果是集合属性
                            ParameterizedType listGenericType = (ParameterizedType) declaredField.getGenericType();
                            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                            if (baseTypes.contains(listActualTypeArguments[listActualTypeArguments.length - 1].getTypeName())) {
                                //基本数据类型不需要转换
                                return;
                            }
                            //遍历 递归
                            Collection data = (Collection) declaredField.get(resObj);
                            Iterator iterator = data.iterator();
                            while (iterator.hasNext()) {
                                Object next = iterator.next();
                                //递归
                                doConvertAmount(next, originMeasure, targetMeasure);
                            }
                            return;
                        }
                        //不是集合数据 直接获取
                        Object obj = declaredField.get(resObj);
                        //针对POJO 直接递归获取子属性
                        doConvertAmount(obj, originMeasure, targetMeasure);
                    } catch (Exception e) {

                    }
                });


    }
}
