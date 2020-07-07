package com.njwd.utils;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;


/**
 * @Description: cron表达式工具
 * @Author: yuanman
 * @Date: 2019/11/6 16:54
 */
public class CronUtil {

    /**
     * @Description:获取下次执行时间（getFireTimeAfter，也可以下下次...）
     * @Author: yuanman
     * @Date: 2019/12/16 16:17
     * @param cron
     * @return:java.util.Date
     */
    public static Date getNextTriggerTime(String cron){

        if(!CronExpression.isValidExpression(cron)){
            return null;
        }
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("Caclulate Date").withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        Date startTime = trigger.getStartTime();
        Date time = trigger.getFireTimeAfter(startTime);
        return time;
    }

    /**
     * @Description:判断cron表达式是否合法
     * @Author: yuanman
     * @Date: 2019/12/16 16:18
     * @param cron
     * @return:boolean
     */
    public static boolean checkCron(String cron){
        if(StringUtil.isEmpty(cron)||!CronExpression.isValidExpression(cron)){
            return false;
        }else{
            return true;
        }
    }


    /**
     * @Description:根据传入的时间获取下次执行时间。
     * @Author: yuanman
     * @Date: 2019/12/24 17:01
     * @param cron
     * @param time
     * @return:java.util.Date
     */
    public static Date getNextTriggerTimeByTime(String cron,Date time){
        if(!CronExpression.isValidExpression(cron)){
            return null;
        }
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);
        Date nextRunTime = cronSequenceGenerator.next(time);
        return nextRunTime;
    }

}
