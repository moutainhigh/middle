package com.njwd.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.FinReportConfig;
import com.njwd.entity.reportdata.dto.FinReportConfigDto;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @param
 * @author fancl
 * @description 财务报表基准表配置Mapper
 * @date 2020/1/8
 * @return
 */
public interface FinReportConfigMapper extends BaseMapper<FinReportConfig> {
    /**
     * @param
     * @return
     * @description 根据企业id查询对象
     * @author fancl
     * @date 2020/1/10
     */
    @Deprecated
    FinReportConfigVo findConfigByType(@Param("configQueryDto") FinReportConfig configQueryDto);

    /**
     * 根据组名查,返回数组
     *
     * @param configQueryDto
     * @return FinReportConfigVo
     */
    List<FinReportConfigVo> findConfigByCondition(@Param("configQueryDto") FinReportConfig configQueryDto);

    /**
     * 查询配置信息列表
     *
     * @param page
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Page<FinReportConfigVo> findFinReportConfigList(Page<FinReportConfigVo> page, @Param("param") FinReportConfigDto param);

    /**
     * 根据id查询配置信息
     *
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    FinReportConfigVo findFinReportConfigById(@Param("param") FinReportConfigDto param);

    /**
     * 新增配置信息
     *
     * @param list
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Integer addInfo(@Param("list") List<FinReportConfigDto> list);

    /**
     * 更新配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Integer updateInfo(@Param("param") FinReportConfigDto param);

    /**
     * 批量删除配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Integer deleteInfo(@Param("param") FinReportConfigDto param);

    /**
     * 校验重复数据
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Integer checkIfExist(@Param("param") FinReportConfigDto param);

}
