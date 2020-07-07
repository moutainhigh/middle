package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.MemberConsume;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Description 会员消费数据统计报表
 * @create 2019/11/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberConsumeStatiVo extends MemberConsume {
    /**
     * 数据类型：用于区分品牌/区域/门店
     */
    private String type;
    /**
     * 数据类型id
     */
    private String typeId;

}
