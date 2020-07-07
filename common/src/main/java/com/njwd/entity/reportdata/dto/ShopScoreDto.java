package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author jds
 * @Description 门店评分报表
 * @create 2019/11/26 9:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopScoreDto extends BaseQueryDto implements Serializable {
    /**
     * 菜单 code
     */
    private String menuCode;

}
