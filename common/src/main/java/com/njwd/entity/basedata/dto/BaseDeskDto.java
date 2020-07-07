package com.njwd.entity.basedata.dto;

import com.njwd.entity.basedata.vo.BaseDeskVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: 台位dto
 * @Author LuoY
 * @Date 2019/12/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseDeskDto extends BaseDeskVo {
    /**
     * 区域id
     */
    private String regionId;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 门店idList
     */
    private List<String> shopIdList;

    /**
     * 门店类型idlist
     */
    private List<String> shopTypeIdList;
}
