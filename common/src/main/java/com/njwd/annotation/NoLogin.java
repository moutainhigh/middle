package com.njwd.annotation;

import java.lang.annotation.*;

/**
 * 登录
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLogin {
}

