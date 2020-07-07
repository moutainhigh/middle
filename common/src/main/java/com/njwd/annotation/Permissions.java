package com.njwd.annotation;

import java.lang.annotation.*;

/**
 * 权限
 * @author XiaFq
 * @date 2020/1/7 11:06 上午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permissions {
    /**
     * 应用标识
     */
    String appSign() default "";

    /**
     * 菜单编码
     */
    String menuCode() default "";

    /**
     * 按钮权限
     */
    String buttonCode() default "";
}
