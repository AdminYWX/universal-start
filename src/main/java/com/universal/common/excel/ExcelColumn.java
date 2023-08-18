package com.universal.common.excel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yangwenxing
 * @date 2023/5/30 09:56
 * @description
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {
    String value() default "";

    String fontName() default "宋体";

    Class<? extends AbstractConverter> converter() default AbstractConverter.class;

    boolean bold() default false;

    short color() default Short.MAX_VALUE;
}
