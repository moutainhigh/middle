package com.njwd.entity.schedule.vo;

import com.njwd.entity.schedule.EnteApp;
import com.njwd.entity.schedule.EnteServer;
import com.njwd.entity.schedule.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description:返回给业务系统的任务信息
 * @Author: yuanman
 * @Date: 2019/11/6 13:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskVo extends Task {
    /**
     *企业app列表
     */
    private List<EnteApp> enteApp;
    /**
     *企业服务器列表
     */
    private List<EnteServer> enteServer;
    /**
     *需要填充的参数
     */
    private List<String> param;
}
