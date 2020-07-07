package com.njwd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author luoY
 */
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
@ServletComponentScan
@MapperScan({"com.njwd.**.mapper*"})
@EnableFeignClients
@EnableAspectJAutoProxy(exposeProxy = true)
public class KettleJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(KettleJobApplication.class, args);
	}
}
