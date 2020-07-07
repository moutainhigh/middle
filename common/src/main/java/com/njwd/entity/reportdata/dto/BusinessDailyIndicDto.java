package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.BusinessDailyIndicVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author jds
 * @Description 经营日报指标
 * @create 2019/12/3 9:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessDailyIndicDto extends BusinessDailyIndicVo {

    private static final long serialVersionUID = -1715229845890359125L;
    private List<BusinessDailyIndicDto> businessDailyIndicList;

    /**
     * 项目id List
     */
    List<String> projectIdList;

    /**
    * 门店idList
    */
    private List<String> shopIdList;

    private Integer beginTime;

    private Integer endTime;

}
