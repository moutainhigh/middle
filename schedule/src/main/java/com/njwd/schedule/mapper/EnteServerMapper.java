package com.njwd.schedule.mapper;

import com.njwd.entity.schedule.EnteServer;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:wd_ente_server的mapper
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@Repository
public interface EnteServerMapper {

    /**
     * @Description:根据主键获取企业服务器信息
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    EnteServer selectByPrimaryKey(String configId);

    /**
     * @Description:根据企业id和服务类型获取服务信息
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    List<EnteServer> selectByEnteIdAndType(String enteId);


}