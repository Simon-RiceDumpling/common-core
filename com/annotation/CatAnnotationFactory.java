package com.annotation;



import com.enums.FactoryObjectEnum;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CatAnnotationFactory {
    FactoryObjectEnum[] value();

}
