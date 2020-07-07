package com.njwd.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.mapper.DataMapMapper;
import com.njwd.admin.service.DataMapService;
import com.njwd.entity.admin.DataMap;
import com.njwd.entity.admin.DataMapKey;
import com.njwd.entity.admin.dto.DataMapKeyDto;
import com.njwd.entity.admin.dto.MapDataDto;
import com.njwd.entity.admin.dto.SingleMapDataDto;
import com.njwd.entity.admin.vo.MapDataVo;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:数据映射controller类
 * @Author: yuanman
 * @Date: 2019/11/26 9:53
 */
@RestController
@RequestMapping("datamap")
public class DataMapController extends BaseController {

    @Resource
    private DataMapService dataMapService;
    @Resource
    private DataMapMapper dataMapMapper;

    /**
     * @Description:查询源表数据
     * @Author: yuanman
     * @Date: 2019/11/28 14:56
     * @param dataMapKeyDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("getSourceMapData")
    @ApiOperation(value = "获取源表映射数据", notes="获取源表映射数据")
    public Result<Page<MapDataVo>> getSourceMapData(@RequestBody DataMapKeyDto dataMapKeyDto){
        FastUtils.checkParams(dataMapKeyDto.getDatamapKey(),dataMapKeyDto.getEnteId(),dataMapKeyDto.getType());
        DataMap dataMap=dataMapMapper.selectByPrimaryKey(dataMapKeyDto);
        FastUtils.checkParams(dataMap);
        Page<MapDataVo> data=dataMapService.getSourceData(dataMap,dataMapKeyDto);
        return ok(data);
    }

    /**
     * @Description:查询目标表数据
     * @Author: yuanman
     * @Date: 2019/11/28 14:57
     * @param dataMapKeyDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("getTargetMapData")
    @ApiOperation(value = "获取目标表数据", notes="获取目标表数据")
    public Result<Page<MapDataVo>> getTargetMapData(@RequestBody DataMapKeyDto dataMapKeyDto){
        FastUtils.checkParams(dataMapKeyDto.getDatamapKey(),dataMapKeyDto.getEnteId(),dataMapKeyDto.getType());
        DataMap dataMap=dataMapMapper.selectByPrimaryKey(dataMapKeyDto);
        FastUtils.checkParams(dataMap);
        return ok(dataMapService.getTargetData(dataMap,dataMapKeyDto));
    }

    /**
     * @Description:获取映射菜单列表
     * @Author: yuanman
     * @Date: 2019/11/28 14:57
     * @param dataMapKeyDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("getMapList")
    @ApiOperation(value = "获取映射菜单列表", notes="获取映射菜单列表")
    public Result<List<DataMap>> getMapList(@RequestBody DataMapKeyDto dataMapKeyDto){
        FastUtils.checkParams(dataMapKeyDto.getEnteId());
        return ok(dataMapMapper.selectList(dataMapKeyDto));
    }


    /**
     * @Description:根据目标key获取关联的源数据列表
     * @Author: yuanman
     * @Date: 2019/11/29 11:43
     * @param mapDataDto
     * @return:com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.MapDataVo>>
     */
    @PostMapping("getSingleMapData")
    @ApiOperation(value = "根据目标key获取关联的源数据列表", notes="根据目标key获取关联的源数据列表")
    public Result<List<MapDataVo>> getSingleMapData(@RequestBody SingleMapDataDto mapDataDto){
        FastUtils.checkParams(mapDataDto.getMapDataVo(),mapDataDto.getViewType());
        DataMapKey key=new DataMapKey();
        String enteId=mapDataDto.getEnteId();
        String mapKey=mapDataDto.getDatamapKey();
        FastUtils.checkParams(enteId,mapKey);
        key.setEnteId(enteId);
        key.setDatamapKey(mapKey);
        DataMap dataMap=dataMapMapper.selectByPrimaryKey(key);
        FastUtils.checkParams(dataMap);
        return ok(dataMapService.getSingleMapData(dataMap,mapDataDto.getMapDataVo(),mapDataDto.getViewType()));
    }

    /**
     * @Description:更新映射数据
     * @Author: yuanman
     * @Date: 2019/12/18 17:25
     * @param mapDataDto
     * @return:com.njwd.support.Result<java.lang.Integer>
     */
    @PostMapping("updateMapData")
    @ApiOperation(value = "更新映射数据", notes="更新映射数据")
    public Result<Integer> updateMapData(@RequestBody MapDataDto mapDataDto){
        FastUtils.checkParams(mapDataDto.getViewType());
        DataMapKey key=new DataMapKey();
        String enteId=mapDataDto.getEnteId();
        String mapKey=mapDataDto.getDatamapKey();
        FastUtils.checkParams(enteId,mapKey);
        key.setEnteId(enteId);
        key.setDatamapKey(mapKey);
        DataMap dataMap=dataMapMapper.selectByPrimaryKey(key);
        FastUtils.checkParams(dataMap);
        return ok(dataMapService.updateMapData(dataMap,mapDataDto.getViewType(),mapDataDto.getAddList(),mapDataDto.getDeleteList()));
    }
}
