package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.SysMenuTabColumn;
import com.njwd.entity.basedata.dto.query.TableConfigQueryDto;
import com.njwd.entity.basedata.vo.SysMenuTabColumnVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表格配置mapper
 */
public interface SysMenuTabColumnMapper extends BaseMapper<SysMenuTabColumn> {
    /**
     * 根据菜单code和用户id查询
     * @param queryDto 查询实体
     * @return
     */
    List<SysMenuTabColumnVo> findList(@Param("queryDto") TableConfigQueryDto queryDto);

}