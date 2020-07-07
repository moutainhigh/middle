package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.CrmPrepaidVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ljc
 * @Description 会员充值记录
 * @create 2019/11/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmPrepaidDto extends CrmPrepaidVo {
    /**
     * 充值id
     */
    private List<String> prepaidIds;
    /**
     * 开始时间
     */
    private String beginPrepaidTime;
    /**
     * 结束时间
     */
    private String endPrepaidTime;
}
