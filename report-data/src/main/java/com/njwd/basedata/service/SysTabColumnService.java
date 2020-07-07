package com.njwd.basedata.service;

import com.njwd.entity.basedata.excel.ExcelColumn;
import com.njwd.entity.reportdata.dto.SysTabColumnDto;
import com.njwd.entity.reportdata.vo.SysTabColumnVo;

import java.util.List;
import java.util.Set;

/**
 * @Author liuxiang
 * @Description 表格配置
 * @Date:9:25 2019/6/14
 **/
public interface SysTabColumnService {


    /**
     * @Description 查询表格配置列表
     * @Author liuxiang
     * @Date:17:03 2019/7/2
     * @Param [sysTabColumnVo]
     * @return java.util.List<com.njwd.platform.entity.vo.SysTabColumnVo>
     **/
    List<SysTabColumnVo> findSysTabColumnList(SysTabColumnDto sysTabColumnDto);

    /**
     * @Description 根据ID查询表格配置
     * @Author liuxiang
     * @Date:17:03 2019/7/2
     * @Param [sysTabColumnVo]
     * @return com.njwd.platform.entity.vo.SysTabColumnVo
     **/
    SysTabColumnVo findSysTabColumnById(SysTabColumnDto sysTabColumnDto);

    /**
     * 报表配置项 列表
     * @param: []
     * @return: java.util.List<com.njwd.entity.reportdata.vo.SysTabColumnVo>
     * @author: zhuzs
     * @date: 2019-12-16
     */
    Set<SysTabColumnVo> findSysTabColumnSet();

    /**
     * 根据菜单配置获取表格配置项
     * @param menuCode
     * @return
     */
    List<ExcelColumn> getExcelColumns(String menuCode);
}
