package com.annotation;

import java.lang.annotation.*;

/**
 * @ClassNAME SpecialHandler
 * @Description TODO
 * @Author renbo.ren@cyberklick.com
 * @Date 2023/2/15 13:36
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpecialHandler {


    boolean encode() default false;
    boolean base64() default false;
    //后续可再次扩展 需要对返回值做特殊处理的话 比如金额转换等需求

}
