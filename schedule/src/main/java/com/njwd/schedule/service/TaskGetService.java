package com.njwd.schedule.service;

import com.njwd.common.ScheduleConstant;
import com.njwd.entity.schedule.EnteApp;
import com.njwd.entity.schedule.EnteServer;
import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.TaskRely;
import com.njwd.entity.schedule.vo.TaskVo;
import com.njwd.schedule.mapper.EnteAppMapper;
import com.njwd.schedule.mapper.EnteServerMapper;
import com.njwd.schedule.mapper.TaskMapper;
import com.njwd.schedule.mapper.TaskRelyMapper;
import com.njwd.utils.FastUtils;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:提供对外查询需要执行的任务的类
 * @Author: yuanman
 * @Date: 2019/11/5 17:54
 */
@Service
public class TaskGetService {

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private EnteAppMapper enteAppMapper;
    @Resource
    private EnteServerMapper enteServerMapper;
    @Resource
    private TaskRelyMapper taskRelyMapper;
    @Resource
    private RedisTemplate redisTemplate;
    private final static Logger LOGGER = LoggerFactory.getLogger(TaskGetService.class);

    /**
     * @param role
     * @Description:任务获取service
     * @Author: yuanman
     * @Date: 2019/11/8 17:47
     * @return:com.njwd.entity.schedule.vo.TaskVo
     */
    public TaskVo getTaskByRole(String role){
         //得到最优先需要执行的任务
        TaskVo  taskVo = getNeedToExcuteTask(role);
        if (null != taskVo) {
            //配置数据库信息
            getDbConfig(taskVo);
            //配置需返回的参数
            getParam(taskVo);
        }
        return taskVo;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/4/15 10:42
     * @Param
     * @return
     * @Description 获取需要重新拉取数据的任务
     */
    public TaskVo getNeedUpdateDataTask(){
        LOGGER.info("schedule获取进入------------");
        String key =ScheduleConstant.TaskLock.TASK_LOCK_POS_PRE;
        TaskVo  taskVo = null;
        if(redisTemplate.opsForValue().setIfAbsent(key, "lock")){
            //设值成功后,设置锁超时时间 (我这里是9分钟)
            redisTemplate.expire(key, 9, TimeUnit.MINUTES);
            LOGGER.info("schedule参数进入---------");
            //获得需要执行任务的企业ID和APPid
              taskVo = new TaskVo();
            //查询列表
            List<EnteApp> appList = enteAppMapper.findNeedTask();
            //配置数据库信息
            if(!FastUtils.checkNullOrEmpty(appList)){
                taskVo.setEnteId(appList.get(0).getEnteId());
                taskVo.setAppId(appList.get(0).getAppId());
            }
            getDbConfig(taskVo);
        }
        return taskVo;
    }

    /**
     * @param taskVo
     * @Description:获取数据库配置信息
     * @Author: yuanman
     * @Date: 2019/11/22 9:34
     * @return:com.njwd.entity.schedule.vo.TaskVo
     */
    private TaskVo getDbConfig(TaskVo taskVo) {
        //查询app列表
        List<EnteApp> appList = enteAppMapper.selectByEnteIdAndAppId(taskVo);
        //剔除type为Server的
        List<EnteServer> serverList = enteServerMapper.selectByEnteIdAndType(taskVo.getEnteId());
        taskVo.setEnteApp(appList);
        taskVo.setEnteServer(serverList);
        return taskVo;
    }

    /**
     * @param taskVo
     * @Description:获取需要业务系统需要填充的参数
     * @Author: yuanman
     * @Date: 2019/11/22 9:34
     * @return:com.njwd.entity.schedule.vo.TaskVo
     */
    private TaskVo getParam(TaskVo taskVo) {
        //初始化查询条件
        TaskRely taskRely = new TaskRely();
        taskRely.setEnteId(taskVo.getEnteId());
        //查询以该任务为source_task_key的依赖关系列表
        taskRely.setSourceTaskKey(taskVo.getTaskKey());
        List<TaskRely> targetList = taskRelyMapper.selectBySourceWithoutStoped(taskRely);
        List<String> expressionList = new ArrayList<>();
        //遍历获取顺向参数列表
        targetList.forEach(rely -> {
            List<String> paramsList =StringUtil.getParamNameFromExpression(rely.getFrontExpression());
            expressionList.removeAll(paramsList);
            expressionList.addAll(paramsList);
        });
        //查询以该任务为target_task_key的依赖关系列表
        taskRely.setTargetTaskKey(taskVo.getTaskKey());
        List<TaskRely> sourceList=taskRelyMapper.selectByTargetWithoutStoped(taskRely);
        //遍历获取参数列表
        sourceList.forEach(rely -> {
            List<String> paramsList =StringUtil.getParamNameFromExpression(rely.getBackExpression());
            expressionList.removeAll(paramsList);
            expressionList.addAll(paramsList);
        });
        taskVo.setParam(expressionList);
        return taskVo;
    }

    /**
     * @param role
     * @Description:获取需要优先执行的任务
     * @Author: yuanman
     * @Date: 2019/11/13 16:55
     * @return:com.njwd.entity.schedule.Task
     */
    private TaskVo getNeedToExcuteTask(String role) {
        //使用锁机制
        return RedisUtils.tasklock(ScheduleConstant.TaskLock.TASK_LOCK_PRE + role, () -> getAndUpdate(role));
    }

    /**
     * @Description:获取以及更新任务
     * @Author: yuanman
     * @Date: 2020/1/3 17:22
     * @param role
     * @return:com.njwd.entity.schedule.vo.TaskVo
     */
    private TaskVo getAndUpdate(String role){
        Task task = new Task();
        task.setJobRole(role);
        //根据参数查询出最优先的任务
        TaskVo taskVo = taskMapper.selectNeedToExcuteByRole(task);
        if(null!=taskVo){
            //更新task信息
            task.setEnteId(taskVo.getEnteId());
            task.setTaskKey(taskVo.getTaskKey());
            //更新状态为执行中
            task.setTaskStatus(ScheduleConstant.TaskStatus.EXCUTING);
            //更新上次执行时间
            Date now=taskMapper.getCurrentTime();
            task.setLastExcuteTime(now);
            //将执行前被触发的次数清0
            task.setTriggerCount(0);
            RedisUtils.tasklock(ScheduleConstant.TaskLock.TASK_LOCK_UPDATE_PRE+task.getEnteId()+task.getTaskKey(),()-> taskMapper.updateByPrimaryKeySelective(task));
        }
        return taskVo;
    }



}
