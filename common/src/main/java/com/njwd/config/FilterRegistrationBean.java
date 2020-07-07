package com.njwd.config;

import com.njwd.aspect.WebServletRequestReplacedFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @Author Chenfulian
 * @Description FilterRegistrationBean TODO
 * @Date 2019/12/16 19:58
 * @Version 1.0
 */
@Configuration
public class FilterRegistrationBean {
    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean httpServletRequestReplacedRegistration() {
        org.springframework.boot.web.servlet.FilterRegistrationBean registration = new org.springframework.boot.web.servlet.FilterRegistrationBean();
        registration.setFilter(new WebServletRequestReplacedFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("httpServletRequestReplacedFilter");
//        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);//最早加载
        registration.setOrder(1);
        return registration;

    }
}
