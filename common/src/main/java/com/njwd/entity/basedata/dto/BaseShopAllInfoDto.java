package com.njwd.entity.basedata.dto;

import com.njwd.entity.basedata.vo.BaseShopAllInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: 门店全量信息dto
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseShopAllInfoDto extends BaseShopAllInfoVo {
    /**
    * 门店id
    */
    private List<String>  shopIdList;

    /**
     * 门店类型
     */
    private List<String>  shopTypeIdList;
}
