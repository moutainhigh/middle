package com.njwd.basedata.service.impl;


import com.njwd.basedata.service.SysTabColumnService;
import com.njwd.common.Constant;
import com.njwd.entity.basedata.SysMenuTabColumn;
import com.njwd.entity.basedata.dto.query.TableConfigQueryDto;
import com.njwd.entity.basedata.excel.ExcelColumn;
import com.njwd.entity.basedata.vo.SysMenuTabColumnVo;
import com.njwd.entity.reportdata.dto.SysTabColumnDto;
import com.njwd.entity.reportdata.vo.SysTabColumnVo;
import com.njwd.reportdata.mapper.SysMenuTabColumnMapper;
import com.njwd.reportdata.mapper.SysTabColumnMapper;
import com.njwd.utils.FastUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author liuxiang
 * @Description 表格配置
 * @Date:17:02 2019/7/2
 **/
@Service
public class SysTabColumnServiceImpl implements SysTabColumnService {

    @Resource
    private SysTabColumnMapper sysTabColumnMapper;

    @Resource
    private SysMenuTabColumnMapper sysMenuTabColumnMapper;


    /**
     * @Description 查询表格配置列表
     * @Author liuxiang
     * @Date:17:02 2019/7/2
     * @Param [sysTabColumnVo]
     * @return java.util.List<com.njwd.platform.entity.vo.SysTabColumnVo>
     **/
    @Override
    //@Cacheable(value = "sysTabColumnList", key = "#sysTabColumnDto.menuCode+'-'+#sysTabColumnDto.isEnterpriseAdmin")
    public List<SysTabColumnVo> findSysTabColumnList(SysTabColumnDto sysTabColumnDto) {
        List<SysTabColumnVo> sysTabColumnVoList = sysTabColumnMapper.findSysTabColumnList(sysTabColumnDto);
        //判断这个人下面有没有菜单，如果没有就自动复制一份
        if(null == sysTabColumnVoList || sysTabColumnVoList.size()==0){
            //插入列表数据
            sysTabColumnMapper.insertTabColumn(sysTabColumnDto.getCreatorId(),sysTabColumnDto.getMenuCode(),sysTabColumnDto.getCreatorName());
            sysTabColumnVoList = sysTabColumnMapper.findSysTabColumnList(sysTabColumnDto);
        }
        List<SysTabColumnVo> tree = new ArrayList<>();
        if(!FastUtils.checkNullOrEmpty(sysTabColumnVoList)){
            for(SysTabColumnVo tabColumnVo : sysTabColumnVoList) {
                // 判断是否为根节点
                if(Constant.CodeLevel.PARENT.equals(tabColumnVo.getCodeLevel())) {
                    tree.add(tabColumnVo);
                }
                for(SysTabColumnVo item : sysTabColumnVoList) {
                    if(StringUtils.equals(item.getUpCode(), tabColumnVo.getCode())) {
                        if(tabColumnVo.getChildren() == null) {
                            // 当前节点的子节点为null，则新增一个用于存放子节点的list
                            tabColumnVo.setChildren(new ArrayList<>());
                        }
                        tabColumnVo.getChildren().add(item);
                    }
                }
            }
        }
        return tree;
    }

    /**
     * @Description 根据ID查询表格配置
     * @Author liuxiang
     * @Date:17:03 2019/7/2
     * @Param [sysTabColumnVo]
     * @return com.njwd.platform.entity.vo.SysTabColumnVo
     **/
    @Override
    @Cacheable(value = "sysTabColumnById", key = "#sysTabColumnDto.id+''",unless="#result == null")
    public SysTabColumnVo findSysTabColumnById(SysTabColumnDto sysTabColumnDto) {
        return sysTabColumnMapper.findSysTabColumnById(sysTabColumnDto);
    }

    /**
     * 报表配置项 列表
     *
     * @param: []
     * @return: java.util.Set<com.njwd.entity.reportdata.vo.SysTabColumnVo>
     * @author: zhuzs
     * @date: 2019-12-16
     */
    @Override
    public Set<SysTabColumnVo> findSysTabColumnSet() {
        return sysTabColumnMapper.findSysTabColumnSet();
    }

    /**
     * @param: [menuCode]
     * @return: java.util.List<com.njwd.entity.basedata.excel.ExcelColumn>
     * @author: zhuzs
     * @date: 2020-01-07
     */
    @Override
    public List<ExcelColumn> getExcelColumns(String menuCode) {
        TableConfigQueryDto queryDto = new TableConfigQueryDto();
        queryDto.setMenuCode(menuCode);
        List<SysMenuTabColumnVo> sysMenuTabColumnVos = sysMenuTabColumnMapper.findList(queryDto);
        List<ExcelColumn> excelColumns = new ArrayList<>();
        for (SysMenuTabColumn source : sysMenuTabColumnVos) {
            ExcelColumn excelColumn = new ExcelColumn();
            excelColumn.setField(source.getColumnName());
            excelColumn.setTitle(source.getColumnRemark());
            excelColumns.add(excelColumn);
        }
        return excelColumns;
    }
}
