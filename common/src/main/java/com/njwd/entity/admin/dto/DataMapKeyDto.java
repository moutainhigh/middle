package com.njwd.entity.admin.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.DataMapKey;
import com.njwd.entity.admin.vo.MapDataVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:数据映射的查询条件
 * @Author: yuanman
 * @Date: 2019/11/26 9:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataMapKeyDto extends DataMapKey {
    /**
     *映射类型：已匹配MAPED，未匹配UNMAPED,全部ALL
     */
    private String type;
    /**
     * 名称，模糊查询的条件
     */
    private String name;
    /**
     * 分页参数
     */
    private Page<MapDataVo> page= new Page<>();

}
