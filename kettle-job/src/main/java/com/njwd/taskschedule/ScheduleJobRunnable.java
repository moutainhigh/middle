package com.njwd.taskschedule;

import com.alibaba.fastjson.JSON;
import com.njwd.common.Constant;
import com.njwd.common.KettleJobConstant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.basedata.DatabaseInfo;
import com.njwd.entity.basedata.KettleInfo;
import com.njwd.entity.schedule.EnteApp;
import com.njwd.entity.schedule.EnteServer;
import com.njwd.entity.schedule.dto.TaskDto;
import com.njwd.entity.schedule.dto.TaskResultDto;
import com.njwd.entity.schedule.vo.TaskCallBackVo;
import com.njwd.entity.schedule.vo.TaskVo;
import com.njwd.exception.DataException;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.kettlejob.cloudclient.TaskExcuteFeignClient;
import com.njwd.service.KettleService;
import com.njwd.utils.FastUtils;
import com.njwd.utils.SpringUtils;
import com.njwd.utils.StringUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: WorkerSchedule实现
 * @Author LuoY
 * @Date 2019/11/27
 */
public class ScheduleJobRunnable implements Runnable {
    private Logger logger = LoggerFactory.getLogger(ScheduleJobRunnable.class);

    private TaskExcuteFeignClient taskExcuteFeignClient;
    private KettleService kettleService;
    private String role;

    ScheduleJobRunnable(TaskExcuteFeignClient taskExcuteFeignClient, KettleService kettleService, String role) {
        this.role = role;
        this.taskExcuteFeignClient = taskExcuteFeignClient;
        this.kettleService = kettleService;
    }

    @Override
    public void run() {
        Long startTime = System.currentTimeMillis();
        Long endTime = Constant.Number.ZEROL;
        TaskDto taskDto = new TaskDto();
        taskDto.setRole(role);
        //调用任务接口获取任务
        TaskVo task = taskExcuteFeignClient.getTaskToExcute(taskDto).getData();
        //回调接口参数
        TaskResultDto taskResultDto = new TaskResultDto();
        //todo （功能未完成，完成后方法需要拆分）
        System.out.println("------------检查任务是否假死-------------" );
        if(FastUtils.checkNull(task)){
            return;
        }
        try {
            if (!FastUtils.checkNull(task)) {
                logger.info(Thread.currentThread().getName() + "-------------------------");
                logger.info("------------开始时间-------------" + startTime.toString());
                BeanUtils.copyProperties(task,taskResultDto);
                taskResultDto.setResult(ScheduleConstant.ResultType.FAIL);
                taskResultDto.setEffectCount(0);
                //数据库/服务信息
                List<Map<String, String>> dataApis = new ArrayList<>();
                List<Map<String, String>> dataAnts = new ArrayList<>();
                List<DatabaseInfo> databaseInfos  = new ArrayList<>();
                //运行时参数
                Map<String, String> runtimeParams = (Map<String, String>) JSON.parse(task.getRuntimeParam());
                if(null == runtimeParams){
                    runtimeParams = new HashMap<>();
                }
                //企业id
                runtimeParams.put(KettleJobConstant.KettleJobParam.enteId, task.getEnteId());
                //应用id
                runtimeParams.put(KettleJobConstant.KettleJobParam.appId, task.getAppId());
                //业务类型
                runtimeParams.put(KettleJobConstant.KettleJobParam.businessType, task.getBusinessType());
                //数据类型
                runtimeParams.put(KettleJobConstant.KettleJobParam.dataType, task.getDataType());
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
                        } else if (ScheduleConstant.JoinModel.API.equals(enteApp.getJointMode())) {
                            //接口服务
                            if(!FastUtils.checkNull(enteApp.getSrcConfig())){
                                Map<String, String> dataApi = (Map<String, String>) JSON.parse(enteApp.getSrcConfig());
                                //接口服务参数校验
                                if (!FastUtils.checkNull(dataApi)) {
                                    if (StringUtil.isEmpty(dataApi.get(ScheduleConstant.AppInterface.URL))) {
                                        //接口地址
                                        throw new ServiceException(ResultCode.TASK_SERVICE_URL_NULL_ERROR);
                                    }
                                    dataApis.add(dataApi);
                                }
                            }
                        } else if (ScheduleConstant.JoinModel.ANT.equals(enteApp.getJointMode())) {
                            //爬虫
                            if(!FastUtils.checkNull(enteApp.getSrcConfig())){
                                Map<String, String> dataAnt = (Map<String, String>) JSON.parse(enteApp.getSrcConfig());
                                if (!FastUtils.checkNull(dataAnt)) {
                                    dataAnts.add(dataAnt);
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
                kettleInfo.setFileName(task.getTargetExcute());
                kettleInfo.setParams(runtimeParams);
                kettleInfo.setDatabaseInfoList(databaseInfos);

                String result = "";
                //执行任务
                switch (task.getTaskType()) {
                    case KettleJobConstant.ScheduleJobType.KTR:
                        result = kettleService.runKettle(kettleInfo, Constant.KettleType.ktr);
                        break;
                    case KettleJobConstant.ScheduleJobType.KJB:
                        result = kettleService.runKettle(kettleInfo, Constant.KettleType.kjb);
                        break;
                    case KettleJobConstant.ScheduleJobType.JAVABEAN:
                        /**
                         *任务类型javaBean，调度这边通过反射实现，只会调用对应方法的service
                         *所有调用的service固定参数  所有通过调度调用的javaBean接口
                         *接口参数统一格式(String appId,String enteId,Map<String,String> params)
                         *除了appid和enteid，其它的参数都通过params这个map进行获取
                         */
                        String targetExcute = task.getTargetExcute();
                        if(!FastUtils.checkNull(targetExcute)){
                            Map apiParams = new HashMap();
                            if(null != runtimeParams){
                                apiParams.putAll(runtimeParams);
                            }
                            if(null != dataApis && dataApis.size()>0){
                                for (Map<String, String> map : dataApis) {
                                    apiParams.putAll(map);
                                }
                            }
                            String[] targetExcuteList = StringUtil.split(targetExcute,"\\.");
                            Method method = ReflectionUtils.findMethod(
                                    SpringUtils.getBean(targetExcuteList[0]).getClass(), targetExcuteList[1], String.class,String.class,Map.class);
                            String resultString = (String) ReflectionUtils.invokeMethod(method,  SpringUtils.getBean(targetExcuteList[0]),task.getAppId(),task.getEnteId(),apiParams);
                            Map resultMap = (Map) JSON.parse(resultString);
                            result = resultMap.get("status").toString();
                        }
                        break;
                    default:
                }
                logger.info("result--------" + result);
                if (KettleJobConstant.KettleException.ERROR.equals(result)) {
                    throw new ServiceException(ResultCode.SERVER_ERROR);
                } else if (KettleJobConstant.KettleException.FAIL.equals(result)) {
                    throw new DataException(ResultCode.DATA_ERROR);
                } else if (KettleJobConstant.KettleException.SUCCESS.equals(result)) {
                    //kettle不返回数据操作条数，默认为0
                    taskResultDto.setEffectCount(Constant.Number.ZERO);
                    taskResultDto.setResult(ScheduleConstant.ResultType.SUCCESS);
                }
            }
        } catch (MyBatisSystemException ex) {
            taskResultDto.setErrCode(KettleJobConstant.ScheduleError.DATA_ERR_INFO);
            taskResultDto.setBackLog(ex.getMessage());
            logger.error("数据异常="+ex.getMessage());
        } catch (ServiceException se) {
            taskResultDto.setErrCode(KettleJobConstant.ScheduleError.TASK_ERR_PARAM_ERROR);
            taskResultDto.setBackLog(se.getMessage());
            logger.error(" task任务参数信息异常="+se.getMessage());
        }catch (DataException de) {
            taskResultDto.setErrCode(KettleJobConstant.ScheduleError.DATA_ERR_INFO);
            taskResultDto.setBackLog(de.getMessage());
            logger.error(" 数据异常="+de.getMessage());
        } catch (Exception e) {
            taskResultDto.setErrCode(KettleJobConstant.ScheduleError.BUS_ERR_SERVER);
            taskResultDto.setBackLog(e.getMessage());
            logger.error("应用服务异常="+e.getMessage());
        } finally {
            endTime = System.currentTimeMillis();
            logger.info("------------结束时间-------------" + endTime.toString());
            //获取线程执行时长
            Long executeTime = endTime -startTime;
            logger.info(Thread.currentThread().getName() + "线程执行时长--------" + executeTime);
//            List<String> stringList = FileReadUtil.readTxtFileIntoStringArrList("D:\\cash_user_id.txt");
//            logger.info("读文件时间长="+(endTime - System.currentTimeMillis()));
//            System.out.println("-------使用BufferedReader读取-----------");
//            for(String str : stringList)
//            {
//                System.out.println(str);
//            }
            //后续改进，异常处理调用下一个任务
            Map map = new HashMap();
            if(null != task){
                List list = task.getParam();
                if(null != list&& list.size()>0){
                    for (Object s : list) {
                        map.put(s,"111");
                    }
                }
            }
            taskResultDto.setParam(map);
            taskResultDto.setExcuteTime(executeTime.intValue());
            TaskCallBackVo taskCallBackVo = taskExcuteFeignClient.taskCallback(taskResultDto).getData();
            if(null != taskCallBackVo){
                if (ScheduleConstant.ExecuteStatus.FAIL.equals(taskCallBackVo.getTaskUpdate())) {
                    //todo task端执行失败，需要做点什么
                }
            }
        }
    }


}
