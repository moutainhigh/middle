package com.njwd.admin.service.impl;

import com.njwd.admin.mapper.InitializationMapper;
import com.njwd.admin.mapper.PrimarySystemSettingMapper;
import com.njwd.admin.service.InitializationService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.vo.DataTypeVo;
import com.njwd.utils.ExportSqlUtil;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Author Chenfulian
 * @Description 初始化实现类
 * @Date 2019/12/19 10:38
 * @Version 1.0
 */
@Service
public class InitializationServiceImpl implements InitializationService {
    @Resource
    private InitializationMapper initializationMapper;
    @Resource
    private PrimarySystemSettingMapper primarySystemMapper;
    @Resource
    private IdWorker idWorker;

    //业仓数据库名
    static String dbName = "middle_db";
    //sql文件存放地址
    static String sqlFilePath = "d:/mysql/";

    static String user = "user";
    static String userRole = "user_role";

    @Override
    public Object getBwTableCreationSql() {

        String sqlFileName = "bw_create.sql";
        //注释符号】
        String noteSign = Constant.Character.HASH_SIGN;

        //文件注释
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(System.lineSeparator()).append(noteSign).append(dbName).append(System.lineSeparator());
        //切换数据库空间
        stringBuffer.append("use ").append(dbName).append(AdminConstant.Character.SEMICOLON).append(System.lineSeparator());

        //获取所有表名
        List<TableObj> allBwTable = initializationMapper.getAllTable(dbName);
        //依次循环，将每张表的建表语句写到对应文件中
        for (TableObj tableObj:allBwTable) {
            Map<String,Object> value  = initializationMapper.getCreateSql(dbName, tableObj.getTableName());
            //表信息和建表语句写入sql文件
            stringBuffer.append(System.lineSeparator()).append(noteSign).append(tableObj.getTableName()).append(tableObj.getTableDesc()).append(System.lineSeparator());
            //如果表存在，drop DROP TABLE IF EXISTS `base_account_book`;
            stringBuffer.append("DROP TABLE IF EXISTS `").append(tableObj.getTableName()).append("`").append(AdminConstant.Character.SEMICOLON).append(System.lineSeparator());
            //加上建表语句
            stringBuffer.append(value.get("create table")).append(AdminConstant.Character.SEMICOLON).append(System.lineSeparator());
        }
        System.out.println(stringBuffer.toString());

        File file = new File(sqlFilePath + sqlFileName);

        writeInFile(file, stringBuffer.toString());   //写入文件
        //写入sql文件

        return null;
    }

    @Override
    public Object getBwDataIntertSql() {
        try {
            ExportSqlUtil.getBwDataIntertSql();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 企业初始化sql
     * @author Chenfulian
     * @date 2020/1/13 12:34
     * @param enterpriseId
     * @return java.lang.Object
     */
    @Override
    public Object copyIntertSqlByTemplate(String enterpriseId) {
        String sqlFilePath = "d:/mysql/";
        String sqlFileName = "bw_init.sql";

        StringBuffer sb = new StringBuffer();
        //1.任务task
        //执行清除动作
        sb.append("DELETE FROM wd_task WHERE ente_id='").append(enterpriseId).append("'").append(System.lineSeparator());
        sb.append("DELETE FROM wd_task_rely WHERE ente_id='").append(enterpriseId).append("'").append(System.lineSeparator());
        sb.append("DELETE FROM wd_task_config WHERE ente_id='").append(enterpriseId).append("'").append(System.lineSeparator());
        sb.append("DELETE FROM wd_task_trigger WHERE ente_id='").append(enterpriseId).append("'").append(System.lineSeparator());

        //拷贝动作.初始化wd_task表
        sb.append("INSERT INTO wd_task (task_name, task_key, ente_id, app_id, task_type, target_excute,task_status, error_reason, cron, last_excute_time,\n" +
                "    next_excute_time, switch_status,runtime_param, job_role, max_excute_time,business_type,data_type,open_type)\n" +
                "SELECT task_name, task_key, '").append(enterpriseId).append("', app_id, task_type, target_excute,'WAITING', '', cron, NULL,\n" +
                "    NOW(), 'OFF',runtime_param, job_role, max_excute_time,business_type,data_type,open_type FROM wd_task_base;\n").append(System.lineSeparator());;
        //拷贝动作.初始化wd_task_rely表
        sb.append("INSERT INTO wd_task_rely( rely_id, ente_id, source_task_key, target_task_key, front_expression,back_expression, expression_param, \n" +
                "    max_refuse_count, current_refuse_count, sum_refuse_count,warn_refuse_count)\n" +
                "SELECT  UUID_SHORT(), '").append(enterpriseId).append("', source_task_key, target_task_key, front_expression,back_expression, expression_param, \n" +
                "    max_refuse_count, 0, 0,warn_refuse_count FROM wd_task_rely_base;\n").append(System.lineSeparator());;
        //初始化wd_task_config:包含企业是否付费（默认付费），包含企业任务自动恢复时间系数（默认2），自动恢复开关（默认关闭），预警时间系数（默认1），预警开关（默认开启）
        sb.append("INSERT INTO wd_task_config(ente_id) VALUES('").append(enterpriseId).append("');\n").append(System.lineSeparator());;

        //2.基准设置benchmark
        sb.append("delete from wd_benchmark where ente_id = '").append(enterpriseId).append("'").append(System.lineSeparator());
        sb.append("delete from wd_benchmark_config where ente_id = '").append(enterpriseId).append("'").append(System.lineSeparator());
        sb.append("delete from wd_benchmark_config_rela where ente_id = '").append(enterpriseId).append("'").append(System.lineSeparator());

        //拷贝动作 wd_benchmark
        sb.append("INSERT INTO `wd_benchmark`(`benchmark_id`, `ente_id`, `benchmark_code`, `benchmark_name`)\n" +
                "SELECT UUID_SHORT(), '").append(enterpriseId).append("',benchmark_code, benchmark_name FROM wd_benchmark WHERE ente_id='0';\n").append(System.lineSeparator());;
        //拷贝动作 wd_benchmark_config
        sb.append("INSERT INTO `wd_benchmark_config`(`ente_id`, `config_code`, `config_name`, `metrics_code`, `metrics_name`, `category_code`, `category_name`, `config_sql`)\n" +
                "select '").append(enterpriseId).append("',`config_code`, `config_name`, `metrics_code`, `metrics_name`, `category_code`, `category_name`, `config_sql` from wd_benchmark_config where ente_id='0';\n").append(System.lineSeparator());;
        //拷贝动作 wd_benchmark_config_rela
        sb.append("INSERT INTO `wd_benchmark_config_rela`(`id`, `ente_id`, `benchmark_code`, `benchmark_name`, `metrics_code`, `metrics_name`) \n" +
                "SELECT UUID_SHORT(), '").append(enterpriseId).append("', `benchmark_code`, `benchmark_name`, `metrics_code`, `metrics_name` from wd_benchmark_config_rela where ente_id='0';\n").append(System.lineSeparator());

        File file = new File(sqlFilePath + sqlFileName);

        writeInFile(file, sb.toString());   //写入文件

        return true;
    }

    @Override
    public Object initTableObjAndAttr() {
        //清除所有主数据的表信息，机所有base_开头的表信息
        initializationMapper.deleteTableObj();
        initializationMapper.deleteTableAttr();

        //获取表的所有字段信息，进行前三个排序
        List<TableObj> allDataTypeTable = initializationMapper.getAllDataTypeTableObjAndAttr(dbName);
        TableAttribute tableAttribute = null;
        for (TableObj tableTemp:allDataTypeTable) {
            //base_user_role的数据类型是user
            if (userRole.equals(tableTemp.getDataType())) {
                tableTemp.setDataType(user);
            }
            //处理order alias字段
            for (int i = 0; i < tableTemp.getTableAttributeList().size(); i++) {
                tableAttribute = tableTemp.getTableAttributeList().get(i);
                tableAttribute.setColumnAlias(StringUtil.underlineToCamel(tableAttribute.getColumnName()));
                tableAttribute.setDisplayOrder(i + 1);
            }
        }
        //插入表对象和表属性数据
        initializationMapper.insertTableObj(allDataTypeTable);
        initializationMapper.insertTableAttr(allDataTypeTable);
        return true;

    }


    /**
     * InitPrimaryDataTask初始化主数据任务
     * @author Chenfulian
     * @date 2019/12/20 9:27
     * @param
     * @return java.lang.Object
     */
    @Override
    public Object initPrimaryDataTask() {
        //获取所有主数据类型
        List<DataTypeVo> dataTypeVoList = primarySystemMapper.getAllDataType(null);
        //初始化每个主数据的主系统、数据统一、字段填充任务
        initializationMapper.initPrimaryDataTask(dataTypeVoList,AdminConstant.Task.PRIMARY_SYS_SUFFIX,AdminConstant.Task.PRIMARY_JOINT_SUFFIX,AdminConstant.Task.PRIMARY_PADDING_SUFFIX);
        //初始化任务依赖关系。数据统一 依赖于 主系统，字段填充 依赖于 数据统一。
        initializationMapper.initPrimaryDataTaskRely(dataTypeVoList,AdminConstant.Task.PRIMARY_SYS_SUFFIX,AdminConstant.Task.PRIMARY_JOINT_SUFFIX,AdminConstant.Task.PRIMARY_PADDING_SUFFIX);
        return null;
    }

    private void writeInFile(File file, String content) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("创建文件名失败！！");
                e.printStackTrace();
            }
        }

        FileWriter writer = null;
        BufferedWriter out = null;
        try {
            file.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            writer = new FileWriter(file);
            out = new BufferedWriter(writer);
            out.write(content); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
                writer.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public String getEnterpriseId() {
        String pkValue = idWorker.nextId();
        return pkValue;
    }
}
