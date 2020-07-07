package com.njwd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
@MapperScan({"com.njwd.**.mapper*"})
public class ReportDataApplication {

    public static void main(String[] args) {

        SpringApplication.run(ReportDataApplication.class, args);
    }

}
