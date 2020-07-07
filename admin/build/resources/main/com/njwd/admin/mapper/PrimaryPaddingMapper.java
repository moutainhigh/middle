package com.njwd.admin.mapper;

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
public interface PrimaryPaddingMapper{

    /**
     * 查询某企业主系统的填充规则
     * @author Chenfulian
     * @date 2019/12/2 15:52
     * @param enterpriseId 企业id
     * @param dataType 数据类型
     * @param baseTableName base表名
     * @param relaTableName rela表名
     * @return
     */
    List<PrimaryPaddingVo> getModifiableField(@Param("enterpriseId") String enterpriseId, @Param("dataType") String dataType,
                                              @Param("baseTableName") String baseTableName, @Param("relaTableName") String relaTableName);

    /**
     * 查询某一主数据 所有依赖关系数据存放的表格
     * @param dataType
     * @return
     */
    List<String> getAllRelyBaseTableName(String dataType);

    /**
     * 查询某企业某数据类型 所有的融合规则
     * @param enterpriseDataTypeDto
     * @return
     */
    List<String> getJointRuleByEnteDataType(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 查询该企业该数据类型 所有数据融合的应用
     * @author Chenfulian
     * @date 2019/12/2 15:52
     * @param enterpriseId 企业id
     * @param dataType 数据类型
     * @return
     */
    List<App> getJointAppByDataType(@Param("enterpriseId") String enterpriseId, @Param("dataType") String dataType);

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
     * 删除填充规则
     * @author Chenfulian
     * @date 2019/12/2 15:55
     * @param enterpriseId 企业id
     * @param dataType 数据类型
     * @return
     */
    void delPaddingRule(@Param("enterpriseId") String enterpriseId, @Param("dataType") String dataType);

    /**
     * 添加填充规则
     * @author Chenfulian
     * @date 2019/12/2 15:56
     * @param entePrimaryPaddingDto 企业id,数据类型,填充负责list
     * @return
     */
    void insertPaddingRule(EntePrimaryPaddingDto entePrimaryPaddingDto);

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
