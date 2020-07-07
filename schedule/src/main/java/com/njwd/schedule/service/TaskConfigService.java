package com.njwd.schedule.service;

import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.TaskKey;
import com.njwd.entity.schedule.dto.HandleTaskSwitchDto;
import com.njwd.schedule.mapper.TaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:提供对外查询需要执行的任务的类
 * @Author: yuanman
 * @Date: 2019/11/5 17:54
 */
@Service
public class TaskConfigService {

    @Resource
    private TaskMapper taskMapper;

    /**
     * @Description:批量开启或者关闭任务
     * @Author: yuanman
     * @Date: 2019/12/27 16:24
     * @param handleTaskSwitchDto
     * @return:java.util.Map<java.lang.String,java.lang.Object>
     */
    @Transactional
    public Map<String,Object> HandleSwicthStatus(HandleTaskSwitchDto handleTaskSwitchDto,String type ){
        //定义map，返回用
        Map<String,Object> map=new HashMap<>();
        map.put("openOtherTasks",null);
        map.put("closeOtherTasks",null);
        //更新任务开关状态，开启时下次执行时间为当前时间
        int updateCount=0;
        if(type.equals(ScheduleConstant.handleType.BY_KEYS)){
            updateCount=taskMapper.updateBatchSwitchStatusByKeys(handleTaskSwitchDto);
        }else if(type.equals(ScheduleConstant.handleType.BY_TYPES)){
            updateCount=taskMapper.updateBatchSwitchStatusTaskByTypes(handleTaskSwitchDto);
        }
        if(updateCount>0){
            //自动开启其他任务,为避免会立即执行，下次执行时间设为12小时后，期间靠其他任务修改下次执行时间。
            List<String> taskAutoOpen=autoOpenTask(handleTaskSwitchDto.getEnteId());
            map.put("openOtherTasks",taskAutoOpen);
            //自动关闭其他任务
            List<String> taskAutoClose=autoCloseTask(handleTaskSwitchDto.getEnteId());
            map.put("closeOtherTasks",taskAutoClose);
        }
        return map;
    }

    /**
     * @Description:所有依赖的非手动开启任务都开启的任务自动开启
     * @Author: yuanman
     * @Date: 2019/12/27 16:20
     * @param enteId
     * @return:java.util.List<java.lang.String>
     */

    private List<String> autoOpenTask(String enteId){
        //定义自动开启的任务list
        List<String> taskAutoOpen=new ArrayList<>();
        //定义查询参数
        TaskKey task=new TaskKey();
        task.setEnteId(enteId);
        //定义该任务未开启的依赖任务集合
        List<String> unOpenRelyTasks;
        HandleTaskSwitchDto openTaskDto=new HandleTaskSwitchDto();
        while(true){
            //定义本次循环需要开启的任务
            List<String> tasksNeedToOpen=new ArrayList<String>();
            //获取未开启的非手动开启的任务
            List<String> taskKeys=taskMapper.getUnOpenTaskByEnteId(enteId);
            for(String taskKey:taskKeys){
                //判断该任务依赖的任务是否都开启了，是就开启此任务
                task.setTaskKey(taskKey);
                //查询该任务未开启的依赖任务集合
                unOpenRelyTasks=taskMapper.getUnOpenTaskByRely(task);
                //如果不存在依赖任务未开启，则该任务需要自动开启，加入到开启集合中
                if(unOpenRelyTasks.size()==0){
                    tasksNeedToOpen.add(taskKey);
                }
            }
            //如果没有任务开启则终止循环
            if(tasksNeedToOpen.size()==0) {
                break;
            }
            //批量开启任务
            openTaskDto.setEnteId(enteId);
            openTaskDto.setTaskKeys(tasksNeedToOpen);
            openTaskDto.setSwitchStatus(ScheduleConstant.SwitchStatus.ON);
            //自动开启的任务下次执行时间增加1小时，靠其他任务触发
            taskMapper.updateAutoSwitchStatusByKeys(openTaskDto);
            taskAutoOpen.addAll(tasksNeedToOpen);
        }
        return taskAutoOpen;
    }

    /**
     * @Description:存在依赖的任务未开启的任务自动关闭
     * @Author: yuanman
     * @Date: 2019/12/29 11:48
     * @param enteId
     * @return:java.util.List<java.lang.String>
     */
    private List<String> autoCloseTask(String enteId){
        //定义自动关闭的任务list
        List<String> taskAutoClose=new ArrayList<>();
        //定义查询参数
        TaskKey task=new TaskKey();
        task.setEnteId(enteId);
        //定义该任务未开启的依赖任务集合
        List<String> unOpenRelyTasks;
        HandleTaskSwitchDto closeTaskDto=new HandleTaskSwitchDto();
        while(true){
            //定义本次循环需要关闭的任务
            List<String> tasksNeedToClose=new ArrayList<String>();
            //获取已经开启的非手动开启任务
            List<String> taskKeys=taskMapper.getOpenedTaskByEnteId(enteId);
            for(String taskKey:taskKeys){
                //判断该任务依赖的任务是否都开启了,如果存在未开启的则关闭该任务
                task.setTaskKey(taskKey);
                //查询该任务未开启的依赖任务集合
                unOpenRelyTasks=taskMapper.getUnOpenTaskByRely(task);
                //如果不存在依赖任务未开启，则该任务需要自动开启，加入到开启集合中
                if(unOpenRelyTasks.size()>0){
                    tasksNeedToClose.add(taskKey);
                }
            }
            //如果没有任务需要关闭则终止循环
            if(tasksNeedToClose.size()==0) {
                break;
            }
            //批量关闭任务
            closeTaskDto.setEnteId(enteId);
            closeTaskDto.setTaskKeys(tasksNeedToClose);
            closeTaskDto.setSwitchStatus(ScheduleConstant.SwitchStatus.OFF);
            taskMapper.updateAutoSwitchStatusByKeys(closeTaskDto);
            taskAutoClose.addAll(tasksNeedToClose);
        }
        return taskAutoClose;
    }

    /**
     * @Description:根据keys和enteId查询相关任务
     * @Author: yuanman
     * @Date: 2019/12/29 14:01
     * @param handleTaskSwitchDto
     * @return:java.util.List<com.njwd.entity.schedule.Task>
     */
    public List<Task> getListByKeys(HandleTaskSwitchDto handleTaskSwitchDto){
        return taskMapper.getListByKeys(handleTaskSwitchDto);
    }

    /**
     * @Description:更新企业任务的删除状态
     * @Author: yuanman
     * @Date: 2019/12/30 13:59
     * @param handleTaskSwitchDto
     * @return:java.lang.String
     */
    @Transactional
    public String handleDelStatus(HandleTaskSwitchDto handleTaskSwitchDto){
        String enteId=handleTaskSwitchDto.getEnteId();
        //删除所有任务（逻辑删除）。
        taskMapper.updateDeleteStatusToOne(enteId);
        //创建主数据统一任务和拉取任务
        handleTaskSwitchDto.setDeleteStatus(ScheduleConstant.deleteStatus.NO_DEL);
        taskMapper.createTaskByKeys(handleTaskSwitchDto);
        //创建企业业务数据
        taskMapper.createTaskForBusiness(enteId);
        //处理appId为空的的数据
        autoCreateTask(enteId);
        return Constant.ReqResult.SUCCESS;
    }

    /**
     * @Description:自动创建appid为空且business_type不为DATA_UNIFY的任务
     * @Author: yuanman
     * @Date: 2020/1/14 13:47
     * @param enteId
     * @return:void
     */
    private void autoCreateTask(String enteId){
        Task param=new Task();
        param.setEnteId(enteId);
        HandleTaskSwitchDto createTaskDto=new HandleTaskSwitchDto();
        createTaskDto.setEnteId(enteId);
        while (true){
            param.setDeleteStatus(ScheduleConstant.deleteStatus.DEL);
            List<String> deletedTask=taskMapper.selectByEnteIdAndDeleteStatus(param);
            List<String> needToCreateTasks=new ArrayList<>();
            for(String taskKey:deletedTask){
                param.setTaskKey(taskKey);
                int count=taskMapper.getDeletedCountTaskByRely(param);
                //如果不存在依赖任务为未删除的则创建该任务
                if(count==0){
                    needToCreateTasks.add(taskKey);
                }
            }
            if(needToCreateTasks.size()>0){
                createTaskDto.setTaskKeys(needToCreateTasks);
                createTaskDto.setDeleteStatus(ScheduleConstant.deleteStatus.NO_DEL);
                //创建这些任务
                taskMapper.createTaskByKeys(createTaskDto);
            }else{
                //没有则终止循环
                break;
            }
        }

    }
}
