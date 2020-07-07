package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.vo.DataTypeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Chenfulian
 * @Description 初始化
 * @Date 2019/12/19 10:59 上午
 * @Version 1.0
 */
@SqlParser(filter=true)
public interface InitializationMapper extends BaseMapper {

    /**
     * 根据数据库，返回所有表信息
     * @author Chenfulian
     * @date 2019/12/19 11:03
     * @param dbName 数据库名称
     * @return
     */
    List<TableObj> getAllTable(String dbName);

    /**
     * 根据表名，获取
     * @author Chenfulian
     * @date 2019/12/19 11:12
     * @param
     * @param dbName
     * @return
     */
    Map<String, Object> getCreateSql(@Param("dbName") String dbName, @Param("tableName") String tableName);

    /**
     * 根据所有数据类型，初始化任务wd_task_base
     * @author Chenfulian
     * @date 2019/12/20 9:39
     * @param 
     * @return 
     */
    void initPrimaryDataTask(@Param("dataTypeVoList")List<DataTypeVo> dataTypeVoList, @Param("primarySysSuffix")String primarySysSuffix,
                             @Param("primaryJointSuffix") String primaryJointSuffix, @Param("primaryPaddingSuffix")String primaryPaddingSuffix);

    /**
     * 初始化所有数据的任务依赖 wd_task_rely_base
     * @author Chenfulian
     * @date 2020/1/6 11:25
     * @param
     * @return
     */
    void initPrimaryDataTaskRely(@Param("dataTypeVoList")List<DataTypeVo> dataTypeVoList, @Param("primarySysSuffix")String primarySysSuffix,
                                 @Param("primaryJointSuffix") String primaryJointSuffix, @Param("primaryPaddingSuffix")String primaryPaddingSuffix);

    List<TableObj> getAllDataTypeTable(String dbName);

    void deleteTableObj();

    void deleteTableAttr();

    List<TableObj> getAllDataTypeTableObjAndAttr(String dbName);

    void insertTableObj(List<TableObj> allDataTypeTable);

    void insertTableAttr(List<TableObj> allDataTypeTable);
}
