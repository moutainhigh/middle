package com.njwd.admin.service;

/**
 * @Author Chenfulian
 * @Description 初始化 Service类
 * @Date 2019/12/19 11:10
 * @Version 1.0
 */
public interface InitializationService {

     /**
      * 获取创建业仓表格（空表）的sql
      * @author Chenfulian
      * @date 2019/12/19 10:42
      * @param 
      * @return 
      */
     Object getBwTableCreationSql();

    Object getBwDataIntertSql();

    Object initPrimaryDataTask();

    Object copyIntertSqlByTemplate(String enterpriseId);

    Object initTableObjAndAttr();

    Object getEnterpriseId();
}
