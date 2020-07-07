package com.njwd.entity.basedata.vo;

import com.njwd.entity.basedata.BaseDesk;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 台位vo
 * @Author LuoY
 * @Date 2019/12/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseDeskVo extends BaseDesk {
    /**
     * 台位总数
     */
    private int deskCount;
}
