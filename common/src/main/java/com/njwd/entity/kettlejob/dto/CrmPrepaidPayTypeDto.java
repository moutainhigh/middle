package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.CrmPrepaidPayType;
import com.njwd.entity.kettlejob.vo.CrmPrepaidPayTypeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ljc
 * @Description 会员储值支付明细
 * @create 2019/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmPrepaidPayTypeDto extends CrmPrepaidPayType {
    /**
     * 充值id
     */
    private List<String> prepaidIds;
}
