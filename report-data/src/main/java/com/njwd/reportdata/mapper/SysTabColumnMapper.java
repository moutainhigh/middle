package com.njwd.reportdata.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.SysTabColumn;
import com.njwd.entity.reportdata.dto.SysTabColumnDto;
import com.njwd.entity.reportdata.vo.SysTabColumnVo;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * @author liuxiang
 * @Description 表格配置
 * @Date:10:52 2019/6/13
 */
public interface SysTabColumnMapper extends BaseMapper<SysTabColumn> {

    /**
     * @Description 查询表格配置列表
     * @Author liuxiang
     * @Date:15:36 2019/7/2
     * @Param [sysTabColumnVo]
     * @return java.util.List<com.njwd.platform.entity.vo.SysTabColumnVo>
     **/
    List<SysTabColumnVo> findSysTabColumnList(SysTabColumnDto sysTabColumnDto);


    /**
     * @Description 根据ID查询表格配置
     * @Author liuxiang
     * @Date:15:36 2019/7/2
     * @Param [sysTabColumnVo]
     * @return com.njwd.platform.entity.vo.SysTabColumnVo
     **/
    SysTabColumnVo findSysTabColumnById(SysTabColumnDto sysTabColumnDto);

    /**
     * 报表配置项 列表
     * 
     * @param: []
     * @return: java.util.List<com.njwd.entity.reportdata.vo.SysTabColumnVo>
     * @author: zhuzs
     * @date: 2019-12-16 
     */
    Set<SysTabColumnVo> findSysTabColumnSet();
    /*
    * @Description 修改配置项
     * @Author shenhf
     * @Date:15:36 2020/3/28
     * @Param [sysTabColumnDto]
     * @return
    * */
    void updateTabColumn(@Param("userId") String userId,@Param("menuCode") String menuCode,List<String> tabColumnCodeList);
    /*
     * @Description 动态插入配置项
     * @Author shenhf
     * @Date:15:36 2020/3/28
     * @Param [creatorId,menuCode,creatorName]
     * @return
     * */
    void insertTabColumn(@Param("creatorId") String creatorId,@Param("menuCode") String menuCode,@Param("creatorName") String creatorName);
}