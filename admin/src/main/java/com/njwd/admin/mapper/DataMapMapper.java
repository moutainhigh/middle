package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.DataMap;
import com.njwd.entity.admin.DataMapKey;
import com.njwd.entity.admin.DataMapSql;
import com.njwd.entity.admin.dto.DataMapKeyDto;
import com.njwd.entity.admin.vo.MapDataVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:wd_datamap
 * @Author: yuanman
 * @Date: 2019/11/25 11:01
 */
@Repository
@SqlParser(filter=true)
public interface DataMapMapper {
    /**
     * @Description:根据主键映射信息
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    DataMap selectByPrimaryKey(DataMapKey key);

    /**
     * @Description:根据企业id和名称模糊查询获取数据映射菜单列表
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    List<DataMap>  selectList(DataMapKeyDto dataMapKeyDto);
    /**
     * @Description:分页查询源表数据
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    Page<MapDataVo> selectSourceData(Page page, DataMapSql sql);

    /**
     * @Description:查询目标表数据
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    Page<MapDataVo> selectTargetData(Page page,DataMapSql sql);


    /**
     * @Description:根据目标表key查询关联的源表数据列表
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    List<MapDataVo> selectSourceDataByTarget(DataMapSql sql);

    /**
     * @Description:保存映射数据
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    int insertMapData(DataMapSql sql);

    /**
     * @Description:删除映射数据
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    int deleteMapData(DataMapSql sql);

    /**
     * @Description:根据主键删除该数据映射菜单
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    int deleteByPrimaryKey(DataMapKey key);
    /**
     * @Description:新增数据映射菜单
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    int insertSelective(DataMap record);
    /**
     * @Description:更新数据映射菜单
     * @Author: yuanman
     * @Date: 2019/11/25 11:01
     */
    int updateByPrimaryKeySelective(DataMap record);



}