package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.dto.ScmInstockEntryDto;
import lombok.Data;

import java.math.BigDecimal;

/**
* @Description: 入库单明细Vo
* @Author: LuoY
* @Date: 2020/3/27 17:07
*/
@Data
public class ScmInstockEntryVo extends ScmInstockEntryDto {
    /**
    * 入库实收数量合计
    */
    private BigDecimal realqtySum;

    /**
     * 供应商id
     */
    private String supplierId;

    /**
    * 物料编码
    */
    private String materialNum;
}
