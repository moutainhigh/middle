package com.njwd.config;

import com.njwd.utils.idworker.IdWorker;
import com.njwd.utils.idworker.IdWorkerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
* @Description: 主键生成器
* @Author: LuoY
* @Date: 2019/12/10 10:26
*/
@SpringBootConfiguration
public class IdWorkerConfig {
    @Bean
    com.njwd.utils.idworker.IdWorker idworker()
    {
        IdWorker idworker = IdWorkerFactory.create(0);
        return idworker;
    }
}
