package com.njwd.kettlejob.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.OneToManySql;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.dto.EntePrimaryPaddingDto;
import com.njwd.entity.admin.dto.EnterpriseDataTypeDto;
import com.njwd.entity.admin.vo.PrimaryPaddingVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @program: middle-data
 * @description: 数据填充Mapper
 * @author: Chenfulian
 * @create: 2019-11-20 10:58
 **/
@SqlParser(filter=true)
public interface PrimaryPaddingMapper {

    /**
     * 根据表名list，查询所有表属性
     * @author Chenfulian
     * @date 2019/12/2 16:16
     * @param tableName 表名list
     * @return
     */
    List<TableAttribute> getTableAllAttribute(List<TableObj> tableName);

    /**
     * 查询填充规则表，即由非主系统来的字段
     * @author Chenfulian
     * @date 2019/12/2 15:55
     * @param enterpriseId 企业id
     * @param dataType 数据类型
     * @return
     */
    List<PrimaryPaddingVo> getPrimayPadding(@Param("enterpriseId") String enterpriseId, @Param("dataType") String dataType);

    /**
     * 执行主数据填充
     * @author Chenfulian
     * @date 2019/12/2 16:18
     * @param paddingSqlMap 数据填充sql
     * @return
     */
    void executeOneToOnePadding(Map<String, String> paddingSqlMap);

    /**
     * 执行一对多表格的delete语句
     * @author Chenfulian
     * @date 2019/12/2 15:56
     * @param one2ManySqlMap 一对多的delete sql
     * @return
     */
    void executeOneToManyDelete(Map<String, OneToManySql> one2ManySqlMap);

    /**
     * 执行一对多表格的insert语句
     * @author Chenfulian
     * @date 2019/12/2 15:57
     * @param one2ManySqlMap 一对多的insert sql
     * @return
     */
    void executeOneToManyInsert(Map<String, OneToManySql> one2ManySqlMap);
}
