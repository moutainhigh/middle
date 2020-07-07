package com.njwd.entity.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:映射数据
 * @Author: yuanman
 * @Date: 2019/11/25 10:32
 */
@Data
public class MapDataVo {
    /**
     * 主键1
     */
    private String key1;
    /**
     * 主键2
     */
    private String key2;
    /**
     * 主键3
     */
    private String key3;
    /**
     * 主键4
     */
    private String key4;
    /**
     * 主键5
     */
    private String key5;
    /**
     * 名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 映射子集key和name拼接后的字符串
     */
    private String maps;
    /**
     * 映射子集的列表
     */
    private List<MapDataVo> mapData;
    /**
     * 映射子集的数量
     */
    private int mapCount;

}
