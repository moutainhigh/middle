package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.PsItemScoreVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author jds
 * @Description 巡店项目得分
 * @create 2019/11/13 9:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PsItemScoreDto extends PsItemScoreVo implements Serializable {

    /**
     * 开始日期
     */
    private String startDay;

    /**
     * 结束日期
     */
    private String endDay;

}
