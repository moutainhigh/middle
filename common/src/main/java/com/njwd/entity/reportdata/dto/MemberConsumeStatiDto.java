package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author jds
 * @Description 会员消费数据统计报表
 * @create 2019/11/27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MemberConsumeStatiDto extends BaseQueryDto {
    /**
     * 菜单 code
     */
    private String menuCode;
    /**
     * 查询类型
     */
    private String type;
    /**
     * 上期时间
     */
    private Date preBeginDate;
    private Date preEndDate;
}
