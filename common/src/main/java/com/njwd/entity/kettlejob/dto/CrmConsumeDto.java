package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.CrmConsumeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author ljc
 * @Description 会员消费记录
 * @create 2019/11/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmConsumeDto extends CrmConsumeVo implements Serializable {
    /**
     * 消费流水ids
     */
    private List<String> consumeIds;
    /**
     * 消费时间
     */
    private String beginConsumeTime;
    /**
     * 消费时间
     */
    private String endConsumeTime;
}
