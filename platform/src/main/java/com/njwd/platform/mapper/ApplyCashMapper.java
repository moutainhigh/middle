package com.njwd.platform.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.platform.dto.ApplyCashDto;
import com.njwd.entity.platform.dto.PageApplyCashDto;
import com.njwd.entity.platform.vo.ApplyCashVo;
import org.apache.ibatis.annotations.Param;

public interface ApplyCashMapper {
    int deleteByPrimaryKey(Long cashId);

    int insert(ApplyCashDto record);

    /**
     * 新增提现记录
     * @param applyCashDto
     * @return
     */
    int insertApplyCash(ApplyCashDto applyCashDto);

    int updateByPrimaryKeySelective(ApplyCashDto record);

    int updateByPrimaryKey(ApplyCashDto record);

    /**
     * @Description: 分页查询提现
     * @Param:  ApplyCashDto
     * @return: Page<ApplyCashVo>
     * @Author: huxianghong
     * @Date: 2020/4/7 14:15
     */
    Page<ApplyCashVo> selectApplyCashByPage(Page<ApplyCashDto> page, @Param("param") PageApplyCashDto param);

    /**
     * 修改CRM提现状态
     * @param applyCashDto
     * @return
     */
    int updateCrmStatus(ApplyCashDto applyCashDto);

    /**
     * 修改MSS提现状态
     * @param applyCashDto
     * @return
     */
    int updateMssStatus(ApplyCashDto applyCashDto);
}