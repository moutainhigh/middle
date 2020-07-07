package com.njwd.schedule.service;


import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.schedule.*;
import com.njwd.entity.schedule.vo.TaskCallBackVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.schedule.mapper.TaskMapper;
import com.njwd.schedule.mapper.TaskRelyMapper;
import com.njwd.schedule.mapper.TaskResultMapper;
import com.njwd.schedule.mapper.TaskTriggerMapper;
import com.njwd.utils.CronUtil;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description:任务回调service
 * @Author: yuanman
 * @Date: 2019/11/5 17:54
 */
@Service
public class TaskCallBackService {

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private TaskResultMapper taskResultMapper;
    @Resource
    private TaskRelyMapper taskRelyMapper;
    @Resource
    private IdWorker idWorker;
    @Resource
    private TaskTriggerMapper taskTriggerMapper;


    private final static Logger LOGGER = LoggerFactory.getLogger(TaskCallBackService.class);

    /**
     * @param
     * @Description:任务执行回调
     * @Author: yuanman
     * @Date: 2019/11/13 16:53
     * @return:java.lang.String
     */
    public TaskCallBackVo taskCallBack(TaskResult result) {
        TaskCallBackVo callBackVo;
        //校验errorcode
        if (    StringUtil.isNotEmpty(result.getErrCode())
                &&!result.getErrCode().startsWith(ScheduleConstant.TaskErrorCode.DATA_ERR_PRE)
                && !result.getErrCode().startsWith(ScheduleConstant.TaskErrorCode.TASK_ERR_PRE)
                && !result.getErrCode().startsWith(ScheduleConstant.TaskErrorCode.BUS_ERR_PRE)
        ) {
           throw new ServiceException(ResultCode.SCHEDULE_PARAM_ERRORCODE_FORMAT_ERR);
        }
        //校验result
        if (!result.getResult().equals(ScheduleConstant.ResultType.FAIL)
                && !result.getResult().equals(ScheduleConstant.ResultType.SUCCESS)
                && !result.getResult().equals(ScheduleConstant.ResultType.PART_FAIL)
        ) {
            throw new ServiceException(ResultCode.SCHEDULE_PARAM_RESULT_FORMAT_ERR);
        }

        //保存日志数据
        callBackVo = new TaskCallBackVo();
        try {
            saveResult(result);
            callBackVo.setLogSave(ScheduleConstant.ExecuteStatus.SUCCESS);
        } catch (Exception e) {
            LOGGER.error("保存日志失败",e);
            callBackVo.setLogSave(ScheduleConstant.ExecuteStatus.FAIL);
        }
        Task task=null;
        //更新该任务的任务状态、上次执行时间，下次执行时间
        try {
            task= RedisUtils.tasklock(ScheduleConstant.TaskLock.TASK_LOCK_UPDATE_PRE+result.getEnteId()+result.getTaskKey(),()-> updateTask(result));
            if(task == null){
                callBackVo.setTaskUpdate(ScheduleConstant.ExecuteStatus.FAIL);
            }else{
                callBackVo.setTaskUpdate(ScheduleConstant.ExecuteStatus.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("修改任务状态失败",e);
            callBackVo.setTaskUpdate(ScheduleConstant.ExecuteStatus.FAIL);
        }
        if (task == null) {
            throw new ServiceException(ResultCode.SCHEDULE_NO_ENTE_BUSSINESS_ERR);
        }

        //触发其他任务
        try {
            List<String> triggerTasks = triggerOtherTask(result);
            //将查询出来的任务的下次执行时间提前。
            for(String taskKey:triggerTasks){
                Map<String, Object> param = new HashMap<String, Object>();
                Date nextExcuteTime=taskMapper.getCurrentTime();
                param.put("nextExcuteTime", nextExcuteTime);
                param.put("taskKey", taskKey);
                param.put("enteId", result.getEnteId());
                RedisUtils.tasklock(ScheduleConstant.TaskLock.TASK_LOCK_UPDATE_PRE+result.getEnteId()+taskKey,()->taskMapper.updateToTriggerTask(param));
            }
            //往触发日志里记录触发
            TaskTrigger trigger=new TaskTrigger();
            trigger.setTriggerId(idWorker.nextId());
            trigger.setSourceTaskKey(result.getTaskKey());
            trigger.setTargetTaskKey(listToString(triggerTasks, ','));
            trigger.setEnteId(result.getEnteId());
            trigger.setDescription("触发日志");
            trigger.setTriggerStatus(ScheduleConstant.SwitchStatus.OFF);
            taskTriggerMapper.insertSelective(trigger);
            callBackVo.setTriggerTask(triggerTasks);
        } catch (Exception e) {
            LOGGER.error("触发其他任务失败",e);
            e.printStackTrace();
        }

        return callBackVo;
    }

    /**
     * @param result
     * @Description:保存日志数据
     * @Author: yuanman
     * @Date: 2019/11/13 16:48
     * @return:void
     */
    private void saveResult(TaskResult result) {
        result.setResultId(idWorker.nextId());
        //判断日志长度是否大于数据库最大长度，大于就截取
        String log=result.getBackLog();
        if(result.getBackLog().length()>=2000){
            result.setBackLog(log.substring(0,1999));
        }
        taskResultMapper.insertSelective(result);
    }

    /**
     * @param result
     * @Description:更新任务信息
     * @Author: yuanman
     * @Date: 2019/11/13 16:49
     * @return:void
     */
    @Transactional
    public Task updateTask(TaskResult result) {
        TaskKey key = new TaskKey();
        key.setEnteId(result.getEnteId());
        key.setTaskKey(result.getTaskKey());
        Task task = taskMapper.selectByEnteIdAndTaskKey(key);
        //修改任务的信息
        String errorCode = result.getErrCode();
        if (null == task) {return null;}
        //1、修改错误原因
        if(null==errorCode){
            task.setErrorReason("");
        }else{
            task.setErrorReason(errorCode);
        }
        //2、修改执行状态
        if (StringUtil.isNotEmpty(errorCode) && errorCode.startsWith(ScheduleConstant.TaskErrorCode.TASK_ERR_PRE)) {
            task.setTaskStatus(ScheduleConstant.TaskStatus.ERROR);
        } else {
            task.setTaskStatus(ScheduleConstant.TaskStatus.WAITING);
        }
        //3、修改运行时参数
        if(StringUtil.isNotEmpty(result.getRuntimeParam())){
            task.setRuntimeParam(result.getRuntimeParam());
        }
        //4、修改当前任务的下次执行时间（如果cron表达式格式错误，task_status,error_reason 也会发生变化）
        setNextExcuteTime(task);
        //提交任务的修改信息
        taskMapper.updateByPrimaryKeySelective(task);
        return task;
    }

    /**
     * @param task
     * @Description:设置任务下次执行时间
     * @Author: yuanman
     * @Date: 2019/11/13 16:49
     * @return:com.njwd.entity.schedule.Task
     */
    private Task setNextExcuteTime(Task task) {
        String taskStatus = task.getTaskStatus();
        //任务状态为错误的不更新
        if (!taskStatus.equals(ScheduleConstant.TaskStatus.ERROR)) {
            String cron = task.getCron();
            //如果表达式为空或者表达式错误，则把该任务的状态置为ERROR
            if (StringUtil.isEmpty(cron) || !CronExpression.isValidExpression(cron)) {
                task.setTaskStatus(ScheduleConstant.TaskStatus.ERROR);
                task.setErrorReason(ScheduleConstant.TaskErrorCode.CRON_FORMAT_ERROR);
                LOGGER.error(task.getEnteId() + "_" + task.getTaskKey() + " cron is whrong");
            } else {
                //如果中间被其他任务触发的次数大于0则不更新下次执行时间
                if(task.getTriggerCount()==0){
                    //以数据库时间为准
                    Date now=taskMapper.getCurrentTime();
                    task.setNextExcuteTime(CronUtil.getNextTriggerTimeByTime(cron,now));
                }
            }
        }
        return task;
    }


    /**
     * @Description:触发其他任务
     * @Author: yuanman
     * @Date: 2019/12/28 16:49
     * @param result
     * @return:java.util.List<java.lang.String>
     */
    public List<String> triggerOtherTask(TaskResult result) {
        String errCode = result.getErrCode();
        int count = result.getEffectCount();
        //定义返回的需要触发的taskId的集合
        List<String> taskKeys = new ArrayList<>();
        //&& count > 0  理应加上该条件但目前worker端获取不到执行数量
        if ((StringUtil.isEmpty(errCode) || errCode.startsWith(ScheduleConstant.TaskErrorCode.DATA_ERR_PRE)) ) {
            //查询回触任务，从task_trigger表中查询回触任务，比如：a由于b的原因触发了b，b完成后还要去触发a
            List<String> backTriggerTaskKeys=getBackTriggerTaskKeys(result);
            //查询顺向执行的任务
            List<String> frontTaskKeys=getFrontTaskKeys(result);
            //查询反向执行的任务
            List<String> backTaskKeys=getBackTaskKeys(result);
            //合并任务
            taskKeys.addAll(frontTaskKeys);
            taskKeys.removeAll(backTaskKeys);
            taskKeys.addAll(backTaskKeys);
            taskKeys.removeAll(backTriggerTaskKeys);
            taskKeys.addAll(backTriggerTaskKeys);
        }
        return taskKeys;
    }
    /**
     * @Description:查询以及关闭任务回触
     * @Author: yuanman
     * @Date: 2019/12/31 11:34
     * @param result
     * @return:java.util.List<java.lang.String>
     */
    private List<String> getBackTriggerTaskKeys(TaskResult result){

        TaskTrigger triggerParam = new TaskTrigger();
        triggerParam.setSourceTaskKey(result.getTaskKey());
        triggerParam.setEnteId(result.getEnteId());
        triggerParam.setTriggerStatus(ScheduleConstant.SwitchStatus.ON);
        List<String> taskKeys = taskTriggerMapper.selectTargetBySourceAndStatus(triggerParam);
        //关闭这些触发
        if (taskKeys.size() > 0) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("targetTaskKeys", taskKeys);
            param.put("sourceTaskKey", result.getTaskKey());
            param.put("enteId", result.getEnteId());
            taskTriggerMapper.updateStatusToOffByIds(param);
        }
        //如果触发的任务B，还有其他任务A在触发它并且A在执行中，那么B移除触发列表
        List<String> triggerTaskRemove = new ArrayList<>();
        taskKeys.forEach(targetKey -> {
            triggerParam.setTargetTaskKey(targetKey);
            List<String> excutingSource = taskTriggerMapper.selectSourceByTargetAndExcuting(triggerParam);
            if (excutingSource.size() > 0) {
                triggerTaskRemove.add(targetKey);
            }
        });
        taskKeys.removeAll(triggerTaskRemove);
        return taskKeys;
    }
    /**
     * @Description:获取顺向触发的任务
     * @Author: yuanman
     * @Date: 2019/12/28 16:57
     * @param result
     * @return:java.util.List<java.lang.String>
     */
    private List<String> getFrontTaskKeys(TaskResult result){
        List<String> taskKeys=new ArrayList<>();
        //查询以当前任务为source的依赖关系
        TaskRely relyParam = new TaskRely();
        relyParam.setEnteId(result.getEnteId());
        relyParam.setSourceTaskKey(result.getTaskKey());
        List<TaskRely> relyList = taskRelyMapper.selectBySourceWithoutStoped(relyParam);
        //遍历依赖关系
        for(TaskRely rely : relyList){
            String expression=replaceParam(rely.getFrontExpression(),result.getParam());
            //当表达式为空，或者还存在参数没替换时，结束本次循环，继续下次循环
            if (StringUtil.isEmpty(expression) || expression.contains("{") || expression.contains("}")) {
                 continue;
            }
            //替换参数并拼接小括号
            expression = "(" + expression+ ")";
            //校验表达式
            boolean check;
            if (expression.equals("(false)")) {
                check = false;
            } else if(expression.equals("(true)")){
                check=true;
            }else{
                try{
                    check = taskRelyMapper.excuteExpression(expression);
                }catch(Exception e){
                    LOGGER.error(expression+"表达式执行错误",e);
                    continue;
                }
            }
            if(check){
                taskKeys.add(rely.getTargetTaskKey());
            }
        }
        return taskKeys;
    }

    /**
     * @Description:获取反向触发的任务
     * @Author: yuanman
     * @Date: 2019/12/28 16:57
     * @param result
     * @return:java.util.List<java.lang.String>
     */
    private List<String> getBackTaskKeys(TaskResult result){
        List<String> taskKeys=new ArrayList<>();
        TaskRely relyParam = new TaskRely();
        relyParam.setEnteId(result.getEnteId());
        relyParam.setTargetTaskKey(result.getTaskKey());
        List<TaskRely> relyList = taskRelyMapper.selectByTargetWithoutStoped(relyParam);
        for(TaskRely rely : relyList){
            int currentRefuseCount = rely.getCurrentRefuseCount();
            int maxRefuseCount = rely.getMaxRefuseCount();
            int sumRefuseCount = rely.getSumRefuseCount();
            //替换参数并拼接小括号
            String expression = "(" + replaceParam(rely.getBackExpression(),result.getParam()) + ")";
            //当表达式为空，或者还存在参数没替换时，结束本次循环，继续下次循环
            if (StringUtil.isEmpty(expression) || expression.contains("{") || expression.contains("}")) {
                continue;
            }
            //计算拒绝机制
            try {
                boolean check;
                if (expression.equals("(false)")) {
                    check = false;
                }else if(expression.equals("(true)")){
                    check=true;
                }else{
                    check = taskRelyMapper.excuteExpression(expression);
                }
                //判断拒绝机制
                if (check) {
                    //当前被拒绝次数为0或被拒绝次数大于设定值时该任务执行
                    if (currentRefuseCount == 0 || currentRefuseCount > maxRefuseCount) {
                        //触发该任务
                        taskKeys.add(rely.getSourceTaskKey());
                        //设置当前拒绝次数为1
                        rely.setCurrentRefuseCount(1);
                        //当targetTask执行完之后需要回触本任务
                        TaskTrigger taskTrigger = new TaskTrigger();
                        taskTrigger.setTriggerId(idWorker.nextId());
                        taskTrigger.setTargetTaskKey(result.getTaskKey());
                        taskTrigger.setTriggerStatus(ScheduleConstant.SwitchStatus.ON);
                        taskTrigger.setEnteId(result.getEnteId());
                        taskTrigger.setSourceTaskKey(rely.getSourceTaskKey());
                        taskTriggerMapper.insertSelective(taskTrigger);
                    } else {
                        //被拒绝次数加1
                        rely.setCurrentRefuseCount(currentRefuseCount + 1);
                    }
                    //连续被拒绝次数
                    rely.setSumRefuseCount(sumRefuseCount + 1);
                } else {
                    //将当前拒绝次数和连续拒绝次数清0
                    rely.setCurrentRefuseCount(0);
                    rely.setSumRefuseCount(0);
                }
                taskRelyMapper.updateByPrimaryKeySelective(rely);
            } catch (Exception e) {
                LOGGER.error(expression+"表达式执行错误",e);
                continue;
            }
        }
        return taskKeys;
    }

    /**
     * @param expression
     * @param receiveParam
     * @Description:替换表达式中参数
     * @Author: yuanman
     * @Date: 2019/11/21 18:30
     * @return:java.lang.String
     */
    private String replaceParam(String expression,Map<String, String> receiveParam) {

        if (StringUtil.isEmpty(expression)){ return expression;}
        List<String> params=StringUtil.getParamNameFromExpression(expression);
        for (String param : params) {
            if (null == receiveParam.get(param)){
                return expression;
            }
            if(!StringUtil.isSqlValid(param)){
                throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT,"参数存在sql注入风险");
            }
            //"{" + param + "}";
            String paramName = Constant.Character.BRACKET_LEFT_B + param + Constant.Character.BRACKET_RIGHT_B;
            String value = "'" + receiveParam.get(param) + "'";
            //将参数name替换成参数value
            expression = expression.replace(paramName, value);
        }
        return expression;
    }

    /**
     * 将list转为String
     * @param list
     * @param separator
     * @return
     */
    public String listToString(List list, char separator) {
        return StringUtils.join(list.toArray(), separator);
    }


}
