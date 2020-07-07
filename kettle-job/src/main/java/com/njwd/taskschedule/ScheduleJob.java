package com.njwd.taskschedule;

import com.alibaba.fastjson.JSON;
import com.njwd.common.Constant;
import com.njwd.common.KettleJobConstant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.basedata.DatabaseInfo;
import com.njwd.entity.basedata.KettleInfo;
import com.njwd.entity.schedule.EnteApp;
import com.njwd.entity.schedule.EnteServer;
import com.njwd.entity.schedule.vo.TaskVo;
import com.njwd.kettlejob.cloudclient.TaskExcuteFeignClient;
import com.njwd.service.KettleService;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Description: worker调度
 * @Author LuoY
 * @Date 2019/11/28
 */
@Component
@RefreshScope
public class ScheduleJob {
    @Value("${schedule.param.poolsize}")
    private volatile int poolSize;

    @Value("${schedule.param.role}")
    private String role;

    @Resource
    private TaskExcuteFeignClient taskExcuteFeignClient;

    @Resource
    private KettleService kettleService;

    private ExecutorService taskExe;
    private BlockingQueue<Runnable> workQueue;
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJob.class);

    /**
     * @return void
     * 设置线程池ThreadPoolExecutor：
     * 线程池核心线程数
     * 线程池最大线程数
     * 空闲线程存活时间（过期回收）
     * keepAliveTime的单位
     * BlockingDeque: 线程队列
     * 拒绝处理策略：
     * ThreadPoolExecutor.AbortPolicy()：被拒绝后抛出RejectedExecutionException异常
     * ThreadPoolExecutor.CallerRunsPolicy()：被拒绝后给调用线程池的线程处理
     * ThreadPoolExecutor.DiscardOldestPolicy()：被拒绝后放弃队列中最旧的未处理的任务
     * ThreadPoolExecutor.DiscardPolicy()：被拒绝后放弃被拒绝的任务(当前新添加的任务)
     * @Author LuoY
     * @Description 调度任务执行部分
     * @Date 2019/11/27 10:44
     */
    @Scheduled(cron = "0/1 * * * * *")
    private void executeTask() {
        if (FastUtils.checkNull(taskExe)) {
            //创建一个固定大小的数组FIFO阻塞队列
            workQueue = new ArrayBlockingQueue<>(poolSize);
            //创建一个线程池
            taskExe = new ThreadPoolExecutor(poolSize, poolSize, Constant.Number.ZEROL, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.AbortPolicy());
        }
        if (((ThreadPoolExecutor) taskExe).getActiveCount() < poolSize) {
            //当前线程活动数小于线程池长度，表示线程池有空闲数
            ScheduleJobRunnable scheduleJobRunnable = new ScheduleJobRunnable(taskExcuteFeignClient, kettleService,role);
            taskExe.execute(scheduleJobRunnable);
        }
    }

    @Scheduled(cron = "0 03 09 * * ?")
    private void dataUpdateTask() {
        LOGGER.info("delete pos start");
        TaskVo task = taskExcuteFeignClient.getNeedUpdateDataTask().getData();
        LOGGER.info("delete pos task="+task);
        if(FastUtils.checkNull(task)){
            return;
        }
        //运行时参数
        Map<String, String> runtimeParams = (Map<String, String>) JSON.parse(task.getRuntimeParam());
        if(null == runtimeParams){
            runtimeParams = new HashMap<>();
        }
        List<DatabaseInfo> databaseInfos  = new ArrayList<>();
        //企业id
        runtimeParams.put(KettleJobConstant.KettleJobParam.enteId, task.getEnteId());
        //应用id
        runtimeParams.put(KettleJobConstant.KettleJobParam.appId, task.getAppId());
        //只是取了app相关数据库信息
        if(null != task.getEnteApp()){
            for (EnteApp enteApp : task.getEnteApp()) {
                if (ScheduleConstant.JoinModel.DB.equals(enteApp.getJointMode())) {
                    //数据库
                    if(!FastUtils.checkNull(enteApp.getSrcConfig())){
                        DatabaseInfo databaseInfo = JSON.parseObject(enteApp.getSrcConfig(), DatabaseInfo.class);
                        //数据库参数校验
                        if (!FastUtils.checkNull(databaseInfo)) {
                            //连接名
                            if(StringUtil.isEmpty(databaseInfo.getDbConnectType())){
                                continue;
                            }
                            databaseInfos.add(databaseInfo);
                        }
                    }
                }
            }
        }
        //业仓数仓数据库配置
        if(null != task.getEnteServer() && task.getEnteServer().size()>0){
            for (EnteServer enteServer : task.getEnteServer()) {
                String connectConfig = enteServer.getConnectConfig();
                String enteId = enteServer.getEnteId();
                if(enteId.equals(task.getEnteId())){
                    DatabaseInfo databaseInfo = JSON.parseObject(connectConfig, DatabaseInfo.class);
                    databaseInfos.add(databaseInfo);
                }

            }
        }
        //设置kettle执行参数
        KettleInfo kettleInfo = new KettleInfo();
        kettleInfo.setFileName("update/updateData.kjb");
        kettleInfo.setParams(runtimeParams);
        kettleInfo.setDatabaseInfoList(databaseInfos);
        kettleService.runKettle(kettleInfo, Constant.KettleType.kjb);
//        KettleInfo kettleInfo = new KettleInfo();
//        kettleInfo.setFileName("update/updateData.kjb");
//        Map runtimeParams = new ConcurrentHashMap<>();
//        runtimeParams.put("enteId","999");
//        runtimeParams.put("appId","pos_pinzhikeji_01");
//        runtimeParams.put("hostNamebusinessDb","njwd.tpddns.cn");
//        runtimeParams.put("dbNamebusinessDb","middle_db");
//        runtimeParams.put("dbPortbusinessDb","3308");
//        runtimeParams.put("userNamebusinessDb","admin");
//        runtimeParams.put("passwordbusinessDb","admin1245!@$%");
//        runtimeParams.put("hostNamedataDb","njwd.tpddns.cn");
//        runtimeParams.put("dbNamedataDb","jiudingxuan_dev");
//        runtimeParams.put("dbPortdataDb","5431");
//        runtimeParams.put("userNamedataDb","postgres");
//        runtimeParams.put("passworddataDb","postgres");
//        kettleInfo.setParams(runtimeParams);
//        kettleService.runKettle(kettleInfo, Constant.KettleType.kjb);
    }
}
