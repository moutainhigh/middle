package com.njwd.kettlejob.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.schedule.dto.TaskParamDto;
import com.njwd.entity.schedule.vo.TaskParamVo;
import com.njwd.kettlejob.mapper.TaskParamMapper;
import com.njwd.kettlejob.service.TaskParamService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 任务参数
 * @Author ljc
 * @Date 2020/1/9
 **/
@Service
public class TaskParamServiceImpl implements TaskParamService {
    @Resource
    TaskParamMapper taskParamMapper;

    /**
     * 批量修改任务参数
     * @param appId
     * @param enteId
     * @param paramsMap
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskParamBatch(String appId, String enteId, Map<String, Object> paramsMap) {
        List<TaskParamDto> taskParamList = new ArrayList<>();
        String taskKey = paramsMap.get(ScheduleConstant.AppInterface.TASK_KEY) == null ? "" : paramsMap.get(ScheduleConstant.AppInterface.TASK_KEY).toString();
        List<String> targetTaskKeyList = null;
        //处理当前执行的任务
        //pull:修改已执行完任务的状态为1;clean:判断是否全部清洗,存在未清洗的数据则修改状态为0，
        String businessType = paramsMap.get(ScheduleConstant.AppInterface.BUSINESSTYPE)!=null?paramsMap.get(ScheduleConstant.AppInterface.BUSINESSTYPE).toString():"";
        if (StringUtil.isNotBlank(taskKey)) {

            TaskParamDto taskParamDto = new TaskParamDto();
            taskParamDto.setEnteId(enteId);
            taskParamDto.setTaskKey(taskKey);
            //如果为清洗任务，判断是否全部清洗完毕，存在未清洗干净的数据的，则变更status状态为0，否则变更为1
            if(businessType.equals(ScheduleConstant.BusinessType.CLEAN)){
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put(ScheduleConstant.AppInterface.TASK_KEY,taskKey);
                //查询原任务参数配置
                TaskParamVo oldTaskParam = findTaskParam(enteId,paramMap);
                //原任务参数状态是否为1，status=1则查询是否存在未清洗掉的数据，存在则变更为0，否则为1
                if(oldTaskParam!=null && oldTaskParam.getStatus()==1){
                    //未清洗数据标识
                    String unCleanCode = paramsMap.get(ScheduleConstant.AppInterface.UNCLEANCODE)!=null?paramsMap.get(ScheduleConstant.AppInterface.UNCLEANCODE).toString():"";
                    if(StringUtil.isNotBlank(unCleanCode)){
                        taskParamDto.setStatus(Constant.Number.ZERO);
                    }else{
                        taskParamDto.setStatus(Constant.Number.ONE);
                    }
                    taskParamList.add(taskParamDto);
                }
            }else{
                taskParamDto.setStatus(Constant.Number.ONE);
                taskParamList.add(taskParamDto);
            }

        }
        //变更触发任务的执行时间
        System.out.println("-----------------------------------------target_task_key="+paramsMap.get(ScheduleConstant.AppInterface.TARGET_TASK_KEY));
        if (paramsMap.get(ScheduleConstant.AppInterface.TARGET_TASK_KEY) != null) {
            targetTaskKeyList = (List) paramsMap.get(ScheduleConstant.AppInterface.TARGET_TASK_KEY);
            //查询触发的任务
            List<TaskParamVo> taskParamVoList = findTaskParamList(enteId,targetTaskKeyList);
            String startTime = paramsMap.get(ScheduleConstant.AppInterface.STRATTIME).toString();

            if (taskParamVoList != null && taskParamVoList.size() > Constant.Number.ZERO) {
                for (TaskParamVo taskParamVo : taskParamVoList) {
                    //上次任务执行状态为1，则变更下次任务执行时间
                    if(taskParamVo.getStatus()!=null && taskParamVo.getStatus()==1){
                        TaskParamDto taskParamDto = new TaskParamDto();
                        taskParamDto.setEnteId(enteId);
                        taskParamDto.setTaskKey(taskParamVo.getTaskKey());
                        taskParamDto.setParam(startTime);
                        taskParamList.add(taskParamDto);
                    }
                }
            }
        }
        //修改任务参数表
        if (taskParamList.size() > Constant.Number.ZERO) {
            taskParamMapper.updateTaskParamBatch(taskParamList);
        }
    }

    /**
     * 根据taskKey查询任务参数
     * @param enteId
     * @param paramsMap
     * @return
     */
    @Override
    public TaskParamVo findTaskParam(String enteId, Map<String, Object> paramsMap) {
        String taskKey = paramsMap.get(ScheduleConstant.AppInterface.TASK_KEY).toString();
        TaskParamDto taskParamDto = new TaskParamDto();
        taskParamDto.setTaskKey(taskKey);
        taskParamDto.setEnteId(enteId);
        TaskParamVo taskParamVo = taskParamMapper.findTaskParamByKey(taskParamDto);
        return taskParamVo;
    }
    /**
     * 根据taskKey查询任务参数
     * @param enteId
     * @param taskKeyList
     * @return
     */
    @Override
    public List<TaskParamVo> findTaskParamList(String enteId, List<String> taskKeyList) {
        List<TaskParamVo> taskParamVoList = null;
        if(taskKeyList!=null && taskKeyList.size()>Constant.Number.ZERO){
//            List<TaskParamDto> taskParamDtoList = new ArrayList<>();
//            for (String taskKey : taskKeys){
//                TaskParamDto taskParamDto = new TaskParamDto();
//                taskParamDto.setTaskKey(taskKey);
//                taskParamDto.setEnteId(enteId);
//                taskParamDtoList.add(taskParamDto);
//            }

            Map<String,Object> map = new HashMap<>();
            map.put("enteId",enteId);
            map.put("taskKeyList",taskKeyList);
            taskParamVoList = taskParamMapper.findTaskParamListByKeys(map);
        }
        return taskParamVoList;
    }
}