/**
 * u51.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.njwd.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

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

        //添加head参数start
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar.name("authorization-token").description("请求头")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的ticket参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数
        //添加head参数end

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("middle-data-schedule")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.njwd.platform.controller"))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo())
                .enable(swaggerEnable)
                .ignoredParameterTypes(ApiIgnore.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("大平台项目")
                .version("1.0")
                .description("中台项目组·大平台项目")
                .build();
    }
}
