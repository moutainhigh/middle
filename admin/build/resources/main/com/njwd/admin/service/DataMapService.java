package com.njwd.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.DataMap;
import com.njwd.entity.admin.dto.DataMapKeyDto;
import com.njwd.entity.admin.vo.MapDataVo;

import java.util.List;

/**
 * @Description:
 * @Author: yuanman
 * @Date: 2019/11/25 11:46
 */
public interface DataMapService {
     /**
      * @param dataMap
      * @param dataMapKeyDto
      * @Description:分页返回源数据
      * @Author: yuanman
      * @Date: 2019/11/26 15:06
      * @return:com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.njwd.entity.admin.vo.MapDataVo>
      */
     Page<MapDataVo> getSourceData(DataMap dataMap, DataMapKeyDto dataMapKeyDto);
     /**
      * @param dataMap
      * @param dataMapKeyDto
      * @Description:查询目标表数据
      * @Author: yuanman
      * @Date: 2019/11/27 11:03
      * @return:java.util.List<com.njwd.entity.admin.vo.MapDataVo>
      */
     Page<MapDataVo> getTargetData(DataMap dataMap, DataMapKeyDto dataMapKeyDto);
     /**
      * @Description:保存映射数据
      * @Author: yuanman
      * @Date: 2019/11/28 11:21
      * @param dataMap
      * @param data
      * @param viewType
      * @return:int
      */
     int saveMapData(DataMap dataMap, List<MapDataVo> data,String viewType);
     /**
      * @Description:根据主表查询关联源表数据
      * @Author: yuanman
      * @Date: 2019/11/28 17:13
      * @param dataMap
      * @param primaryVo
      * @return:java.util.List<com.njwd.entity.admin.vo.MapDataVo>
      */
     List<MapDataVo> getSingleMapData(DataMap dataMap,MapDataVo primaryVo,String type);
     /**
      * @Description:删除映射数据
      * @Author: yuanman
      * @Date: 2019/11/29 11:31
      * @param dataMap
      * @param vo
      * @param viewType
      * @return:int
      */
     int deleteMapData(DataMap dataMap,MapDataVo vo,String viewType);
}
