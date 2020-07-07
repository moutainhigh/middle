package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.dto.EnterpriseInstallAppDto;
import com.njwd.entity.admin.vo.AppVo;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseServerMapper 应用
 * @Date 2019/11/11 10:59 上午
 * @Version 1.0
 */
public interface AppMapper extends BaseMapper<App> {

   /**
    * 查询应用列表
    * @author XiaFq
    * @date 2019/12/2 3:33 下午
    * @param
    * @return
    */
    List<AppVo> selectAppListForDict(EnterpriseInstallAppDto enterpriseInstallAppDto);

    /**
     * 通过id查询
     * @author XiaFq
     * @date 2019/12/2 3:33 下午
     * @param appId
     * @return 
     */
    App selectAppById(String appId);
}
