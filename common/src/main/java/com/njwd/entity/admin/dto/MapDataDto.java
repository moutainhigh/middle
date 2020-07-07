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
public class MapDataDto extends DataMapKey{

    /**
     *视角类型，SOURCE/TARGET
     */
    private String viewType;

    //private MapDataVo mapDataVo;

    /**
     * 新增列表
     */
    private List<MapDataVo> addList;

    /**
     * 删除列表
     */
    private List<MapDataVo> deleteList;

}
