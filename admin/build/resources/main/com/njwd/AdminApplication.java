package com.njwd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
* @Author XiaFq
* @Description 中台后台管理启动类
* @Date  2019/12/2 3:04 下午
*/
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan({"com.njwd.**.mapper*"})
@EnableAspectJAutoProxy(exposeProxy=true)
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
