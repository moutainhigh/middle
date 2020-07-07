package com.njwd.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.Log;

/**
 * 日志
 * @author XiaFq
 * @date 2020/1/7 1:58 下午
 */
public interface LogMapper extends BaseMapper<Log> {

    /**
     * 批量新增日志
     * @author XiaFq
     * @date 2020/1/7 2:06 下午
     * @param log
     * @return
     */
    @SqlParser(filter=true)
    void addLogBatch(Log log);

    /**
     * 新增
     * @author XiaFq
     * @date 2020/1/7 2:07 下午
     * @param log
     * @return 
     */
    @SqlParser(filter=true)
    void addLog(Log log);
}
