/**
 * u51.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.njwd.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* @Author XiaFq
* @Description Swagger API文档生成配置类
* @Date  2019/11/27 10:40 上午
*/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean swaggerEnable;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("middle-data-admin")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.njwd.admin.controller"))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .apiInfo(apiInfo())
                .enable(swaggerEnable);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("中台后台管理API")
                .version("1.0")
                .description("中台项目组·后台开发小组")
                .build();
    }
}
