package com.njwd.schedule.service;

import com.njwd.entity.schedule.TaskKey;
import com.njwd.entity.schedule.TaskRely;
import com.njwd.schedule.mapper.TaskMapper;
import com.njwd.utils.StringUtil;
import org.springframework.stereotype.Service;

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
public class TaskViewService {
    @Resource
    TaskMapper taskMapper;
    public Map<String,Object> getData(String enteId,String taskKey,String type){
        Map<String,Object> data=new HashMap<>();
        Map<String,Object> map=new HashMap<>();
        map.put("enteId",enteId);
        TaskKey param=new TaskKey();
        param.setTaskKey(taskKey);
        param.setEnteId(enteId);
        if(StringUtil.isEmpty(taskKey)){
            data.put("states",taskMapper.getTaskKeys(enteId));
            data.put("edge",taskMapper.getRelyData(map));
        }else{
            if(type.equals("ALL")){
                //查询与该taskKey相关的taskKey
                List<String> states=new ArrayList<>();
                List<String> checkedList=new ArrayList<>();
                if(states.size()==1){
                    data.put("states",states);
                    data.put("edge",new ArrayList<TaskRely>());
                    return data;
                }
                getRelyTaskKey(param,states,checkedList);
                map.put("taskKeys",states);
                data.put("states",states);
                data.put("edge",taskMapper.getRelyData(map));
            }else if(type.equals("FRONT")){
                List<String> state=new ArrayList<>();
                List<TaskRely> edge=new ArrayList<>();
                getFrontRely(param,state,edge);
                data.put("states",state);
                data.put("edge",edge);

            }else if(type.equals("BACK")){
                List<String> state=new ArrayList<>();
                List<TaskRely> edge=new ArrayList<>();
                getBackRely(param,state,edge);
                data.put("states",state);
                data.put("edge",edge);
            }

        }
        return data;
    }

    public List<String> getRelyTaskKey(TaskKey param,List<String> states,List<String> checkedList){
        states.add(param.getTaskKey());
        checkedList.add(param.getTaskKey());
        List<String> taskKeys=taskMapper.getTaskKeysByRela(param);
        states.removeAll(taskKeys);
        states.addAll(taskKeys);
        for(String relyTaskKey:taskKeys){
            param.setTaskKey(relyTaskKey);
            if(!checkedList.contains(relyTaskKey)){
                checkedList.add(relyTaskKey);
                getRelyTaskKey(param,states,checkedList);
            }
        }
        return states;
    };

    public List<String> getFrontRely(TaskKey param,List<String> state,List<TaskRely> edge){
        String thisKey=param.getTaskKey();
        state.add(param.getTaskKey());
        List<String> taskKeys=taskMapper.getTaskKeysBySouce(param);
        state.addAll(taskKeys);
        for(String taskKey:taskKeys){
            TaskRely rely=new TaskRely();
            rely.setSourceTaskKey(thisKey);
            rely.setTargetTaskKey(taskKey);
            edge.add(rely);
            param.setTaskKey(taskKey);
            getFrontRely(param,state,edge);
        }
        return state;
    }

    public List<String> getBackRely(TaskKey param,List<String> state,List<TaskRely> edge){
        String thisKey=param.getTaskKey();
        state.add(param.getTaskKey());
        List<String> taskKeys=taskMapper.getTaskKeysByTarget(param);
        state.addAll(taskKeys);
        for(String taskKey:taskKeys){
            TaskRely rely=new TaskRely();
            rely.setSourceTaskKey(taskKey);
            rely.setTargetTaskKey(thisKey);
            edge.add(rely);
            param.setTaskKey(taskKey);
            getBackRely(param,state,edge);
        }
        return state;
    }
}
