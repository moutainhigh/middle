package com.njwd.schedule.mapper;

import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.TaskKey;
import com.njwd.entity.schedule.TaskRely;
import com.njwd.entity.schedule.dto.HandleTaskSwitchDto;
import com.njwd.entity.schedule.vo.TaskVo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:wd_task的mapper
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@Repository
public interface TaskMapper {
    /**
     * @Description:根据主键删除
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int deleteByPrimaryKey(String taskId);
    /**
     * @Description:新增
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int insertSelective(Task record);

    /**
     * @Description:根据主键更新，为null的不更新
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int updateByPrimaryKeySelective(Task record);
    /**
     * @Description:查询需要最优先执行的任务
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    TaskVo selectNeedToExcuteByRole(Task record);
    /**
     * @Description:根据参数修改任务的下次执行时间
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int updateToTriggerTask(Map<String,Object> param);
    /**
     * @Description:根据企业id和任务key获取任务
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    Task selectByEnteIdAndTaskKey(TaskKey key);


    /**
     * @Description:根据超时系数查询已经超时的任务
     * @Author: yuanman
     * @Date: 2019/12/11 11:01
     */
    List<Task> selectOverTimeTask();

    /**
     * @Description:恢复超时任务
     * @Author: yuanman
     * @Date: 2019/12/11 11:01
     */
    int updateForReflexTask(Task task);



    /**
    * @Description:获取数据库当前时间
    * @Author: yuanman
    * @Date: 2019/12/24 17:26
    * @return: Date
    */
    Date getCurrentTime();


    /**
     * @Description:根据taskKeys开启自动开启的任务，修改下次执行时间（当前时间+12小时）和开关状态,所有任务的修改状态一致
     * @Author: yuanman
     * @param openTaskDto
     * @return int
     */
    int updateAutoSwitchStatusByKeys(HandleTaskSwitchDto openTaskDto);

    /**
     * @Description:根据taskKeys开启任务，修改下次执行时间和开关状态，修改状态可以不一致
     * @Author: yuanman
     * @param openTaskDto
     * @return int
     */
    int updateBatchSwitchStatusByKeys(HandleTaskSwitchDto openTaskDto);

    /**
     * @Description:根据相关类型enteId,appId,businessType,dataType开启任务，修改下次执行时间和开关状态，修改状态可以不一致
     * @Author: yuanman
     * @param openTaskDto
     * @return int
     */
    int updateBatchSwitchStatusTaskByTypes(HandleTaskSwitchDto openTaskDto);

    /**
     * @Description:查询该任务依赖任务中没有开启的任务
     * @Author: yuanman
     * @param taskKey
     * @return List<String>
     */
    List<String> getUnOpenTaskByRely(TaskKey taskKey);

    /**
     * @Description:查询该企业已经开启的开启类型为自动开启的任务
     * @Author: yuanman
     * @param enteId
     * @return List<String>
     */
    List<String> getOpenedTaskByEnteId(String enteId);

    /**
     * @Description:查询该企业没有开启的开启类型为自动开启的任务
     * @Author: yuanman
     * @param enteId
     * @return List<String>
     */
    List<String> getUnOpenTaskByEnteId(String enteId);


   /**
   * @Description:根据keys查询该企业的任务
   * @Author: yuanman
   * @Date: 2019/12/29 13:51
    * @param handleTaskSwitchDto
   * @return:List<Task>
   */
    List<Task> getListByKeys(HandleTaskSwitchDto handleTaskSwitchDto);

   /**
   * @Description: 删除该企业所有任务，逻辑删除
   * @Author: yuanman
   * @Date: 2019/12/30 10:56
    * @param enteId
   * @return:int
   */
    int updateDeleteStatusToOne(String enteId);

    /**
    * @Description:创建企业的主数据统一任务
    * @Author: yuanman
    * @Date: 2019/12/30 11:27
     * @param handleTaskSwitchDto
    * @return:int
    */
    int createTaskByKeys(HandleTaskSwitchDto handleTaskSwitchDto);
    /**
    * @Description:创建企业相关业务任务
    * @Author: yuanman
    * @Date: 2019/12/30 11:42
     * @param enteId
    * @return:int
    */
    int createTaskForBusiness(String enteId);

    /**
     * @Description:查询appId为空，businessType不为DATA_UNIFY的已删除任务
     * @Author: yuanman
     * @Date: 2020/01/14 11:42
     * @param task
     * @return:int
     */
    List<String> selectByEnteIdAndDeleteStatus(Task task);

    /**
     * @Description:查询该任务依赖的任务deleteStatus为1的任务数量
     * @Author: yuanman
     * @Date: 2020/01/14 11:42
     * @param task
     * @return:int
     */
   int getDeletedCountTaskByRely(Task task);


   //查看任务关系图相关
    List<String> getTaskKeys(String enteId);
    List<TaskRely> getRelyData(Map<String,Object> param);
    List<String> getTaskKeysByRela(TaskKey param);
    List<String> getTaskKeysBySouce(TaskKey param);
    List<String> getTaskKeysByTarget(TaskKey param);

}