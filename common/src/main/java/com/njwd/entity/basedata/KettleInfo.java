package com.njwd.entity.basedata;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/10/30 14:24
 */
@Getter
@Setter
public class KettleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 作业名称
     */
    private String fileName;

    /**
     * 作业参数
     */
    private Map<String, String> params;

    /**
     * 数据库连接信息
     */
    private List<DatabaseInfo> databaseInfoList;

    /**
     * JNDI配置文件地址
     */
    private String propertiesFilePath;

}
