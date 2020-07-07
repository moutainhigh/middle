package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.ScmInstockEntry;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* @Description: 入库单明细
* @Author: LuoY
* @Date: 2020/3/27 16:32
*/
@Data
public class ScmInstockEntryDto extends ScmInstockEntry {
    /**
    * 门店idList
    */
    private List<String> shopIdList;

    /**
     * 物料idList
     */
    private List<String> numberIdList;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     *  结束时间
     */
    private Date EndDate;
}
