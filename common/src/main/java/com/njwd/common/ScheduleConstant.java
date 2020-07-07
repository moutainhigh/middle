package com.njwd.common;

/**
 * @Description: 任务相关常量
 * @Author: yuanman
 * @Date: 2019/11/6 15:11
 */
public interface ScheduleConstant {
    /**
     * @Description: 任务、循环的开关状态
     * @Author: yuanman
     * @Date: 2019/11/6 15:16
     */
    interface SwitchStatus{

        String ON="ON";
        String OFF="OFF";
    }
    /**
     * @Description: 任务的删除标志，1已删除，0未删除
     * @Author: yuanman
     * @Date: 2019/11/6 15:16
     */
    interface deleteStatus{
        String DEL="1";
        String NO_DEL="0";
    }

    /**
     * @Description: 批量开启关闭任务的类型
     * @Author: yuanman
     * @Date: 2019/11/6 15:16
     */
    interface handleType{
        /**
         * 根据主键操作
         */
        String BY_KEYS="BY_KEYS";
        /**
         * 根据类型操作
         */
        String BY_TYPES="BY_TYPES";
    }

    /**
     * @Description: 任务的状态
     * @Author: yuanman
     * @Date: 2019/11/6 15:16
     */
    interface TaskStatus{
        //等待中
        String WAITING="WAITING";
        //执行中
        String EXCUTING="EXCUTING";
        //错误
        String ERROR="ERROR";
    }


    interface TaskLock{
        /**
         * 获取任务时的锁前缀
         */
        String TASK_LOCK_PRE="TASK_LOCK_PRE_";
        /**
         * 任务自动恢复时的锁前缀
         */
        String LOCK_OF_AUTO_REFLEX="LOCK_OF_AUTO_REFLEX";
        /**
         * 任务更新时锁前缀
         */
        String TASK_LOCK_UPDATE_PRE="TASK_LOCK_UPDATE_";
        /**
         * spring定时任务处理数据
         */
        String TASK_LOCK_POS_PRE="TASK_LOCK_POS_PRE";
    }

    //业务系统返回的errCode前缀。
    interface TaskErrorCode{

        //任务系统
        String TASK_ERR_PRE="TASK_ERR_";
        //业务系统
        String BUS_ERR_PRE="BUS_ERR_";
        //数据出错
        String DATA_ERR_PRE="DATA_ERR_";
        //cron表达式错误
        String CRON_FORMAT_ERROR="CRON_FORMAT_ERR";
        //任务不存在
        String TASK_NOT_EXIST="TASK_NOT_EXIST";
    }




    interface ResultType{
        //全部成功
        String SUCCESS="SUCCESS";
        //全部失败
        String FAIL="FAIL";
        //部分失败
        String PART_FAIL="PART_FAIL";
    }



    interface TaskType{
        //kettle的ktr文件
        String KTR="KTR";
        //java代码
        String BEAN="BEAN";
    }

    /**
     * 执行状态
     */
    interface ExecuteStatus{
        /**
         * 成功
         */
        String SUCCESS = "success";
        /**
         * 失败
         */
        String FAIL = "fail";
    }

    /**
     * 服务对接方式
     */
    interface JoinModel{
        /**
         * 数据库连接
         */
        String DB = "DB";

        /**
         * 接口服务
         */
        String API = "API";

        /**
         * python
         */
        String ANT = "ANT";
    }

    /**
     * 数据库JSONkey
     */
    interface SrcConfig{
        /**
         * 网络连接方式
         */
        String LINKNETWORK = "linkNetwork";

        /**
         * 连接名
         */
        String  CONNECTIONNAME = "connectionName";
        /**
         * 数据库连接方式
         */
        String DBTYPE = "dbConnectType";

        /**
         * 网络连接方式
         */
        String HOSTNAME = "hostName";

        /**
         * 端口号
         */
        String DBPORT = "dbPort";

        /**
         * 用户名
         */
        String USERNAME = "userName";

        /**
         * 网络连接方式
         */
        String PASSWORD = "password";

        /**
         * 网络连接方式
         */
        String DRIVE = "drive";

        /**
         * 数据库名称
         */
        String DBNAME = "dbName";
    }

    /**
     * 应用服务接口JSONkey
     */
    interface AppInterface{

        /**
         * 接口地址
         */
        String URL = "url";

        /**
         * 获取数据url
         */
        String DATAURL = "dataUrl";

        /**
         * 用户名
         */
        String USERNAME = "userName";

        /**
         * 公司编码
         */
        String CODE = "code";

        /**
         * 密码
         */
        String PASSWORD = "password";

        /**
         * 开始时间
         */
        String STRATTIME = "startTime";

        /**
         * 心跳时间
         */
        String HEARTTIME = "heartTime";

        /**
         * 结束时间
         */
        String ENDTIME = "endTime";

        /**
         * 微生活appid
         */
        String APPID = "appid";
        /**
         * appKey
         */
        String APPKEY = "appkey";
        /**
         * 版本
         */
        String VERSION = "v";
        /**
         * 返回格式
         */
        String FMT = "fmt";

        /**
         * 页数(默认传：1)
         */
        String PAGE ="page";
        /**
         * 是否全部流水,是:true 否:false[默认，只查接口来源的流水]
         */
        String IS_ALL="is_all";
        /**
         * 是否包括分页信息 是:true 否:false[默认]
         */
        String IS_HAVE_PAGE = "is_have_page";

        /**
         * 【默认】消费：consume，储值：charge(用户查询微生活支付方式)
         */
        String OPTYPE ="optype";

        /**
         * 企业id
         */
        String COMPANYID ="CompanyID";

        /**
         *返回状态
         */
        String STATUS="Status";
        /**
         * 任务名称
         */
        String TASK_KEY="task_key";
        /**
         * 任务名称
         */
        String TARGET_TASK_KEY="target_task_key";
        /**
         * 未清洗任务的标识
         */
        String UNCLEANCODE="unCleanCode";
        /**
         * 任务类型
         */
        String BUSINESSTYPE="businessType";

    }

    interface BusinessType{
        /**
         * 任务类型-拉取
         */
        String PULL="PULL";
        /**
         * 任务类型-清洗
         */
        String CLEAN="CLEAN";
        /**
         * 任务类型-TRANSFER
         */
        String TRANSFER="TRANSFER";
    }

}
