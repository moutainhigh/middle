package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.CrmCard;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/14 9:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmCardVo extends CrmCard {

    /**
     * 会员卡数量
     */
    private BigDecimal cardNum;

}
