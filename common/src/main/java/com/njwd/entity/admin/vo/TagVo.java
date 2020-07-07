package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author XiaFq
 * @Description TagVo TODO
 * @Date 2019/11/12 9:50 上午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TagVo extends Tag {
    /**
     * 是否已选择
     */
    private boolean isSelected;
}
