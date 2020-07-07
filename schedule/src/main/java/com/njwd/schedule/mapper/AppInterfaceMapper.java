package com.njwd.schedule.mapper;

import com.njwd.entity.schedule.AppInterface;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:wd_ente_app_interface
 * @Author: yuanman
 * @Date: 2019/11/25 11:01
 */
@Repository
public interface AppInterfaceMapper {
    /**
     * @Description:根据主键查询
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    AppInterface selectByPrimaryKey(String interfaceId);
    /**
     * @Description:根据企业id查询列表
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    List<AppInterface> selectByEnteId(String enteId);
}