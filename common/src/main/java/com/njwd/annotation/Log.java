package com.njwd.annotation;

import java.lang.annotation.*;

/**
 * 日志
 * @author XiaFq
 * @date 2020/1/7 11:06 上午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**模块*/
    String module() default "";

    /**描述*/
    String description() default "";
}
