package com.njwd.utils;

import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.basedata.DatabaseInfo;
import com.njwd.entity.basedata.KettleInfo;
import com.njwd.exception.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/10/31 9:21
 */
public class KettleUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(KettleUtils.class);


    /**
     * @Author ZhuHC
     * @Date  2019/10/31 9:22
     * @Param  kettleInfo job参数
     * @return
     * @Description 执行job
     */
    public static String runKettleJob(KettleInfo kettleInfo) {
        Job job = null;
        String kjbFilePath = kettleInfo.getFileName();
        List<DatabaseInfo> databaseInfos = kettleInfo.getDatabaseInfoList();
        Map<String,String> initKettleParam = kettleInfo.getParams();
        if(null == initKettleParam){
            initKettleParam = new HashMap<>();
        }
        String uuid = UUID.randomUUID().toString();
        LOGGER.info("KettleUtil@runKettleJob:"+uuid+" {kjbFilePath:"+kjbFilePath+"}");
        String status;
        try {
            //JNDI 方式连接数据库 加载数据库配置信息
            String propertiesFilePath = kettleInfo.getPropertiesFilePath();
            if(StringUtil.isNotBlank(propertiesFilePath)){
                Const.JNDI_DIRECTORY = propertiesFilePath;
            }
            //目前动态更变job数据库参数有问题，暂时把数据库信息放到map参数里面
            if(null != databaseInfos && databaseInfos.size()>0){
                for (DatabaseInfo databaseInfo : databaseInfos) {
                    if(StringUtil.isNotEmpty(databaseInfo.getConnectionName())){
                        //连接地址
                        initKettleParam.put(ScheduleConstant.SrcConfig.HOSTNAME+databaseInfo.getConnectionName(),databaseInfo.getHostName());
                        //数据库名称
                        initKettleParam.put(ScheduleConstant.SrcConfig.DBNAME+databaseInfo.getConnectionName(),databaseInfo.getDbName());
                        //端口
                        initKettleParam.put(ScheduleConstant.SrcConfig.DBPORT+databaseInfo.getConnectionName(),databaseInfo.getDbPort());
                        //用户
                        initKettleParam.put(ScheduleConstant.SrcConfig.USERNAME+databaseInfo.getConnectionName(),databaseInfo.getUserName());
                        //密码
                        initKettleParam.put(ScheduleConstant.SrcConfig.PASSWORD+databaseInfo.getConnectionName(),databaseInfo.getPassword());
                    }

                }
            }
            if(!KettleEnvironment.isInitialized()){
                KettleEnvironment.init();
            }
            //初始化job路径
            JobMeta jobMeta = new JobMeta(kjbFilePath, null);
            List<DatabaseMeta> dmList = jobMeta.getDatabases();
            setDataBaseMetaInfo(databaseInfos, dmList,uuid);
            job = new Job(null, jobMeta);
            //初始化job参数，脚本中获取参数值：${variableName}
            if(StringUtil.isEmpty(initKettleParam.get(ScheduleConstant.AppInterface.STRATTIME))){
                initKettleParam.put(ScheduleConstant.AppInterface.STRATTIME,
                        //设置开始、结束时间
                        DateUtils.format(DateUtils.subMonths( new Date(),1),DateUtils.PATTERN_DAY));

            }
            if(StringUtil.isEmpty(initKettleParam.get(ScheduleConstant.AppInterface.ENDTIME))){
                initKettleParam.put(ScheduleConstant.AppInterface.STRATTIME,
                        DateUtils.format(new Date(),DateUtils.PATTERN_DAY));
            }
            for (String variableName : initKettleParam.keySet()) {
                job.setVariable(variableName, initKettleParam.get(variableName));
            }
            job.start();
            job.waitUntilFinished();
            if (job.getErrors() > 0) {
                status = Constant.ReqResult.FAIL;
                LOGGER.info("KettleUtil@runKettleJob:"+uuid+" 执行失败");
            }else{
                status = Constant.ReqResult.SUCCESS;
                LOGGER.info("KettleUtil@runKettleJob:"+uuid+" 执行成功");
            }
            return status;
        } catch (Exception e) {
            status = Constant.ReqResult.ERROR;
            LOGGER.error("KettleUtil@runKettleJob:"+uuid, e);
            return status;
        }
    }
    /**
     * @Author ZhuHC
     * @Date  2019/10/31 9:45
     * @param kettleInfo Transfer参数
     * @return
     * @Description 执行Transfer
     */
    public static String runKettleTransfer(KettleInfo kettleInfo) {
        Trans trans = null;
        String ktrFilePath = kettleInfo.getFileName();
        Map<String,String> initKettleParam = kettleInfo.getParams();
        List<DatabaseInfo> databaseInfos = kettleInfo.getDatabaseInfoList();
        String uuid = UUID.randomUUID().toString();
        LOGGER.info("KettleUtil@runKettleTransfer:"+uuid+" {ktrFilePath:"+ktrFilePath+"}");
        String status;
        try {
            //JNDI 方式连接数据库 加载数据库配置信息
            String propertiesFilePath = kettleInfo.getPropertiesFilePath();
            if(StringUtil.isNotBlank(propertiesFilePath)){
                Const.JNDI_DIRECTORY = propertiesFilePath;
            }
            //初始化
            if(!KettleEnvironment.isInitialized()){
                KettleEnvironment.init();
            }
            EnvUtil.environmentInit();
            //设置 数据库连接 信息
            TransMeta transMeta = new TransMeta(ktrFilePath);
            List<DatabaseMeta> dmList = transMeta.getDatabases();
            setDataBaseMetaInfo(databaseInfos, dmList,uuid);
            //转换
            trans = new Trans(transMeta);
            //初始化trans参数，脚本中获取参数值：${variableName}
            if (initKettleParam != null) {
                for (String variableName : initKettleParam.keySet()) {
                    trans.setVariable(variableName, initKettleParam.get(variableName));
                }
            }
            //执行转换
            trans.execute(null);
            //等待转换执行结束
            trans.waitUntilFinished();
            if (trans.getErrors() > 0) {
                status = Constant.ReqResult.FAIL;
                LOGGER.info("KettleUtil@runKettleTransfer:"+uuid+" 执行失败");
            }else{
                status = Constant.ReqResult.SUCCESS;
                LOGGER.info("KettleUtil@runKettleTransfer:"+uuid+" 执行成功");
            }
            return status;
        } catch (Exception e) {
            status = Constant.ReqResult.FAIL;
            LOGGER.error("KettleUtil@runKettleTransfer:"+uuid, e);
            return status;
        }
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/7 11:20
     * @Param [databaseInfos, dmList]
     * @return void
     * @Description 设置 数据库连接 信息
     */
    private static void setDataBaseMetaInfo(List<DatabaseInfo> databaseInfos, List<DatabaseMeta> dmList,String uuid) {
        if (!FastUtils.checkNullOrEmpty(databaseInfos) && !FastUtils.checkNullOrEmpty(dmList)) {
            for(DatabaseInfo info : databaseInfos){
                //数据库 连接类型 JDBC
                if(Constant.DBConnectType.JDBC.equals(info.getDbConnectType())){
                    for(DatabaseMeta meta : dmList){
                        //根据连接名 找到对应连接
                        if(StringUtils.trimToNull(meta.getName()).equals(info.getConnectionName())){
                            if(StringUtil.isBlank(info.getHostName())){
                                LOGGER.error("KettleUtil@runKettleTransfer:"+uuid, ResultCode.KETTLE_URL_NULL_ERROR.message);
                            }
                            if(StringUtil.isBlank(info.getDbName())){
                                LOGGER.error("KettleUtil@runKettleTransfer:"+uuid, ResultCode.KETTLE_DBNAME_NULL_ERROR.message);
                            }
                            if(StringUtil.isBlank(info.getDbPort())){
                                LOGGER.error("KettleUtil@runKettleTransfer:"+uuid, ResultCode.KETTLE_PORT_NULL_ERROR.message);
                            }
                            if(StringUtil.isBlank(info.getUserName())){
                                LOGGER.error("KettleUtil@runKettleTransfer:"+uuid, ResultCode.KETTLE_USERNAME_NULL_ERROR.message);
                            }
                            if(StringUtil.isBlank(info.getPassword())){
                                LOGGER.error("KettleUtil@runKettleTransfer:"+uuid, ResultCode.KETTLE_PASSWORD_NULL_ERROR.message);
                            }
                            //连接地址
                            meta.setHostname(info.getHostName());
                            //数据库名称
                            meta.setDBName(info.getDbName());
                            //端口
                            meta.setDBPort(info.getDbPort());
                            //用户
                            meta.setUsername(info.getUserName());
                            //密码
                            meta.setPassword(info.getPassword());
                        }
                    }
                }
            }
        }
    }

}
