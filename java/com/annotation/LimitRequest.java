package com.annotation;

import java.lang.annotation.*;

/**
 * @ClassNAME LimitRequest
 * @Description TODO
 * @Author renbo.ren@cyberklick.com
 * @Date 2023/2/10 11:23
 */

@Documented
@Target(ElementType.METHOD) // 说明该注解只能放在方法上面
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRequest {
    long time() default 6000; // 限制时间 单位：毫秒
    int count() default 1; // 允许请求的次数
}
