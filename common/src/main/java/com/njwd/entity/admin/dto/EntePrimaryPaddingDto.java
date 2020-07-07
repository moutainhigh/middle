package com.njwd.entity.admin.dto;

import com.njwd.entity.admin.vo.PrimaryPaddingVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @program: middle-data
 * @description: 企业填充规则Dto
 * @author: Chenfulian
 * @create: 2019-11-21 16:13
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EntePrimaryPaddingDto extends EnterpriseDataTypeDto{
    /**
     * 主系统填充规则 新增的字段list
     */
    private List<PrimaryPaddingVo> addList;

    /**
     * 主系统填充规则 删除的字段list
     */
    private List<PrimaryPaddingVo> deleteList;

    /**
     * 主系统填充规则list
     */
    private List<PrimaryPaddingVo> primaryPaddingVoList;

}
