package com.njwd.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.platform.dto.ApplyCashDto;
import com.njwd.entity.platform.dto.PageApplyCashDto;
import com.njwd.entity.platform.vo.ApplyCashVo;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 提现有关业务
 * @Date 2020/4/7 10:43
 * @Author 胡翔鸿
 */

public interface ApplyCashService {

    /**
     * @Description: 新增提现的业务
     * @Param:  ApplyCashDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/4/7 14:07
     */
    void doAddAppCash(ApplyCashDto applyCashDto);

    /**
     * @Description: 分页查询提现
     * @Param:  ApplyCashDto
     * @return: Page<ApplyCashVo>
     * @Author: huxianghong
     * @Date: 2020/4/7 14:15
     */
    Page<ApplyCashVo> findAppCashList(@Param("param") PageApplyCashDto param);
}
