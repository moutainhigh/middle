package com.njwd.entity.admin.dto;

import com.njwd.entity.admin.DataMapKey;
import com.njwd.entity.admin.vo.MapDataVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description:数据映射参数
 * @Author: yuanman
 * @Date: 2019/11/26 13:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SingleMapDataDto extends DataMapKey{

    /**
     *视角类型，SOURCE/TARGET
     */
    private String viewType;

    /**
     * 映射主体
     */
    private MapDataVo mapDataVo;

}
