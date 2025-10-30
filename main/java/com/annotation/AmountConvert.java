package com.annotation;



import com.enums.MeasureEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AmountConvert {

    /**
     * 原始单位 默认为元
     */
    MeasureEnum originMeasure() default MeasureEnum.YUAN;

    /**
     * 需要转换的单位 默认为分
     */
    MeasureEnum targetMeasure() default MeasureEnum.CENT;
}
