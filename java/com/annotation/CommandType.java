package com.annotation;

import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.annotation.*;

/**
 * @Author RenBo
 * @Date 2024-06-27 18:14
 * @PackageName:com.annotation
 * @ClassName: CommandType
 * @Description: TODO
 * @Version 1.0
 */
@Documented
@Target(ElementType.TYPE) // 说明该注解只能放在方法上面
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandType {
    SqlCommandType value();

}
