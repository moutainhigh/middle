package com.njwd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Description: 自定义ThreadPoolTaskScheduler线程池
 * @Author LuoY
 * @Date 2019/11/25
 */
@SpringBootConfiguration
@RefreshScope
public class ThreadPoolTaskSchedulerConfig {
    @Value("${schedule.param.poolsize}")
    private volatile int poolSize;

    /**
     * @Author LuoY
     * @Description 自定义调度线程池设置
     * @Date 2019/11/26 11:49
     * @return org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
     **/
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler schedule = new ThreadPoolTaskScheduler();
        //线程池创建时候初始化的线程数
        schedule.setPoolSize(20);
        //线程名称
        schedule.setThreadNamePrefix("ScheduleJob-");
        //设定线程池关闭的时候是否等待线程执行完成再销毁
        schedule.setWaitForTasksToCompleteOnShutdown(true);
        //该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。
        schedule.setAwaitTerminationSeconds(60);
        return schedule;
    }
}
