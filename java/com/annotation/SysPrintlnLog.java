package com.annotation;

import java.lang.annotation.*;

/**
 * @Author RenBo
 * @Date 2024-06-12 16:38
 * @PackageName:com.customize.core.annotation
 * @ClassName: SysPrientLog
 * @Description: 日志打印
 * @Version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysPrintlnLog {

}
