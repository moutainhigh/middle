package com.njwd.entity.reportdata.vo;


import com.njwd.common.Constant;
import com.njwd.entity.reportdata.InsBeerFee;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;


/**
 * @author jds
 * @Description 啤酒进场费
 * @create 2019/12/3 9:27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InsBeerFeeVo extends InsBeerFee {

    /**
     * 批量修改时传参-ID集合
     */
    private List<String> idList;


    private String statusStr;


    public String getStatusStr() {
        return this.getStatus() != null && Constant.Is.YES.equals(this.getStatus()) ? "已启用" : "已禁用";
    }

}
