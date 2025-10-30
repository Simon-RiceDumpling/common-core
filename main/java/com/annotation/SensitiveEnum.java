package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author admin
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveEnum {
    Class<? extends Enum<?>> enumClass();
    String codeField();
    String codeFieldForEnum();
    String valueFieldForEnum();
}
