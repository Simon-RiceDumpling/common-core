package com.annotation;


import com.constants.CrpConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Functional description:  责任链元注解信息
 * 带有次注解的类必须实现基础接口 {@link ChainOfResponsibilityPattern}
 *
 * @author renBo renbo@chinasofti.com
 * @date 2022/6/13 9:03
 * @return
 * @throws
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@Component
public @interface WalterChainOfResponsibilityPatternAnnotation {
    /**
     * 责任链标识 唯一
     */
    String chainId();

    //String value() default "";
    /**
    * value() 是否为正则 默认为false
    */
    boolean isPattern() default false;

    /**
     * 组信息 若需要自己指定 同一组链执行一组业务
     */
    String[] group() default CrpConstant.DEFAULT_GROUP;

    /**
     * 排序 若需要 指定  值越大越优先
     */
    int order() default 0;
    /**
    * 是否需要抛出异常到顶层 若为true 可能会中断程序活其他责任链
    */
    boolean needThrowException() default false;
}
