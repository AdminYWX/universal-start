package com.universal.common.excel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author laoge
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
