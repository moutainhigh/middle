package com.njwd.entity.reportdata.vo;

import com.njwd.common.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/4/26 11:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffTypeInfoVo extends BaseScopeOfQueryType implements Serializable {
    /**
     * 员工人数
     */
    private Integer hireNum = Constant.Number.ZERO;
    /**
     * 离职人数
     */
    private Integer leaveNum = Constant.Number.ZERO;
    /**
     * 试用期人数
     */
    private Integer positiveNum = Constant.Number.ZERO;
    /**
     * 当期入职人数
     */
    private Integer currentNum = Constant.Number.ZERO;
}
