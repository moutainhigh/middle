package com.njwd.entity.basedata.dto;

import com.njwd.entity.basedata.BaseSupplier;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
* @Description: 供应商dto
* @Author: LuoY
* @Date: 2020/3/27 14:22
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseSupplierDto extends BaseSupplier {
    /**
    * 门店idList
    */
    private List<String> shopIdList;

    /**
     * 门店类型idlist
     */
    private List<String> shopTypeIdList;

    /**
    * 供应商编码list
    */
    private List<String> numberList;
}
