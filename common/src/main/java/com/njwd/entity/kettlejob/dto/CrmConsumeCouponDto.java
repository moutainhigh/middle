package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.CrmConsumeCouponVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ljc
 * @Description 会员消费使用的券记录
 * @create 2019/11/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmConsumeCouponDto extends CrmConsumeCouponVo {
    /**
     * 消费id
     */
    private List<String> consumeIds;
}
