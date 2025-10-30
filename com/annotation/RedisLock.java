package com.annotation;

import java.lang.annotation.*;

/**
 * redis作为分布式锁注解使用 引入Redission
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
    * key 如需要动态配置  #参数.属性: #Param.filed1.filed2
    */
    String key();
    /**
    * 等待锁时间 单位 默认为秒
    */
    long waitTime() default 90;
    /**
    * 租界/使用锁的时间 单位 默认为秒
    */
    long leaseTime() default 60;
}
