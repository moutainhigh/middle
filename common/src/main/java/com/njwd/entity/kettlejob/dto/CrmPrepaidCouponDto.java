package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.CrmPrepaidCouponVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ljc
 * @Description 会员赠送券记录
 * @create 2019/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmPrepaidCouponDto extends CrmPrepaidCouponVo {
    /**
     * 充值id
     */
    private List<String> prepaidIds;
}
