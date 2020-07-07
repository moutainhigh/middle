package com.njwd.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * @Author Chenfulian
 * @Description sql导出 工具类 第二版 根据条件导出数据
 * @Date 2019/12/19 18:44
 * @Version 1.0
 */
public class ExportSqlUtil {

    private static Connection conn = null;
    private static Statement sm = null;
    private static String schema = "zhongtai_test";//模式名
//    private static String schema = "middle_db";//模式名
    private static String select = "SELECT * FROM";//查询sql
    private static String insert = "INSERT INTO";//插入sql
    private static String values = "VALUES";//values关键字
    private static List<String> insertList = new ArrayList<String>();//全局存放insertsql文件的数据
    private static String filePath = "d:/mysql//bw_insert.sql";//绝对路径导出数据的文件

    private static LinkedHashMap<String,String> tableConditionMap = new LinkedHashMap<>();//表格与查找条件

    /**
     * 导出数据库表
     * @paramargs
     * @throwsSQLException
     */
    public static void main(String[] args) throws SQLException {
        List<String> listSQL = new ArrayList<String>();
        connectSQL("com.mysql.jdbc.Driver",
                "jdbc:mysql://mzdp8888.mysql.rds.aliyuncs.com:3306/middle_db?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true",
                "zt_develop", "ZhongTai8888");//连接数据库
        listSQL = createSQL();//创建查询语句
        executeSQL(conn, sm, listSQL);//执行sql并拼装
        createFile();//创建文件
    }

    public static void getBwDataIntertSql() throws SQLException {
        List<String> listSQL = new ArrayList<String>();
//        //连接开发数据库
//        connectSQL("com.mysql.jdbc.Driver",
//                "jdbc:mysql://mzdp8888.mysql.rds.aliyuncs.com:3306/middle_db?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true",
//                "zt_develop", "ZhongTai8888");
        //连接测试数据库
        connectSQL("com.mysql.jdbc.Driver",
                "jdbc:mysql://mzdp8888.mysql.rds.aliyuncs.com:3306/zhongtai_test?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&autoReconnectForPools=true&failOverReadOnly=false&useSSL=false&allowMultiQueries=true&serverTimezone=Asia/Shanghai&tinyInt1isBit=false",
                "zhongtai_test", "sys1234!@#$");
        listSQL = createSQL();//创建查询语句
        executeSQL(conn, sm, listSQL);//执行sql并拼装
        createFile();//创建文件
    }

    static {
        tableConditionMap.put("wd_app","");
        tableConditionMap.put("wd_benchmark","where ente_id = '0'");
        tableConditionMap.put("wd_benchmark_config","where ente_id = '0'");
        tableConditionMap.put("wd_benchmark_config_rela","where ente_id = '0'");
        tableConditionMap.put("wd_benchmark_param","");
        tableConditionMap.put("wd_datamap","where ente_id = '0'");
        tableConditionMap.put("wd_dict","");
        tableConditionMap.put("wd_dict_value","");
        tableConditionMap.put("wd_primary_fixed_column","");
        tableConditionMap.put("wd_primary_rely","");
        tableConditionMap.put("wd_table_attribute","");
        tableConditionMap.put("wd_table_obj","");
        tableConditionMap.put("wd_tag","");
        tableConditionMap.put("wd_task_base","");
        tableConditionMap.put("wd_task_config","where ente_id = '0'");
        tableConditionMap.put("wd_task_rely_base","");
    }

    /**
     * 创建insertsql.txt并导出数据
     */
    private static void createFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("创建文件名失败！！");
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            if (insertList.size() > 0) {
                for (int i = 0; i < insertList.size(); i++) {
                    bw.append(insertList.get(i));
                    bw.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拼装查询语句
     *
     * @return返回 select集合
     */
    private static List<String> createSQL() {
        List<String> listSQL = new ArrayList<String>();
        Set<String> tableSet = tableConditionMap.keySet();
        for (String table:tableSet) {
            StringBuffer sb = new StringBuffer();
            sb.append(select).append(" ").append(schema).append(".").append(table).append(" ").append(tableConditionMap.get(table));
            listSQL.add(sb.toString());
        }
        return listSQL;
    }

    /**
     * 连接数据库创建statement对象
     * *@paramdriver
     * *@paramurl
     * *@paramUserName
     * *@paramPassword
     */
    public static void connectSQL(String driver, String url, String UserName, String Password) {
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, UserName, Password);
            sm = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行sql并返回插入sql
     *
     * @paramconn
     * @paramsm
     * @paramlistSQL *
     * @throwsSQLException
     */
    public static void executeSQL(Connection conn, Statement sm, List listSQL) throws SQLException {
        List<String> insertSQL = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = getColumnNameAndColumeValue(sm, listSQL, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            sm.close();
            conn.close();
        }
    }

    /**
     * 获取列名和列值
     *
     * @return
     * @paramsm
     * @paramlistSQL
     * @paramrs
     * @throwsSQLException
     */
    private static ResultSet getColumnNameAndColumeValue(Statement sm, List listSQL, ResultSet rs) throws SQLException {
        if (listSQL.size() > 0) {
            for (int j = 0; j < listSQL.size(); j++) {
                String sql = String.valueOf(listSQL.get(j));
                insertList.add("#"+sql);
                try {
                    rs = sm.executeQuery(sql);
                } catch (Exception e){
                    e.printStackTrace();
                }


                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                while (rs.next()) {
                    StringBuffer ColumnName = new StringBuffer();
                    StringBuffer ColumnValue = new StringBuffer();
                    for (int i = 1; i <= columnCount; i++) {
                        String value = (rs.getString(i) == null) ? null:rs.getString(i).trim();
                        //处理单引号转义
                        if (null != value) {
                            value = value.replaceAll("\\'","\\\\'");
                        }
                        if ("".equals(value)) {
                            value = "";
                        }
                        if (i == 1 || i == columnCount) {
                            if(i==columnCount){
                                ColumnName.append(",");
                            }
                            ColumnName.append("`").append(rsmd.getColumnName(i)).append("`");
                            if( i== 1){
                                if (null == value) {
                                    ColumnValue.append("null").append(",");
                                }
                                else if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i) || Types.LONGVARCHAR == rsmd.getColumnType(i)) {
                                    ColumnValue.append("'").append(value).append("',");
                                } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i) || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i) || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i) || Types.DECIMAL == rsmd.getColumnType(i)|| Types.TINYINT == rsmd.getColumnType(i)) {
                                    ColumnValue.append(value).append(",");
                                } else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i) || Types.TIMESTAMP == rsmd.getColumnType(i)) {
                                    ColumnValue.append("timestamp'").append(value).append("',");
                                } else {
                                    ColumnValue.append(value).append(",");

                                }
                            }else{
                                if (null == value) {
                                    ColumnValue.append("null");
                                }
                                else if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i) || Types.LONGVARCHAR == rsmd.getColumnType(i)) {
                                    ColumnValue.append("'").append(value).append("'");
                                } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i) || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i) || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i) || Types.DECIMAL == rsmd.getColumnType(i)|| Types.TINYINT == rsmd.getColumnType(i)) {
                                    ColumnValue.append(value);
                                } else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i) || Types.TIMESTAMP == rsmd.getColumnType(i)) {
                                    ColumnValue.append("timestamp'").append(value).append("'");
                                } else {
                                    ColumnValue.append(value);

                                }
                            }

                        } else {
                            ColumnName.append(",`" + rsmd.getColumnName(i)+"`");
                            if (null == value) {
                                ColumnValue.append("null").append(",");
                            }
                            else if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i) || Types.LONGVARCHAR == rsmd.getColumnType(i)) {
                                ColumnValue.append("'").append(value).append("'").append(",");
                            } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i) || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i) || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i) || Types.DECIMAL == rsmd.getColumnType(i)|| Types.TINYINT == rsmd.getColumnType(i)) {
                                ColumnValue.append(value).append(",");
                            } else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i) || Types.TIMESTAMP == rsmd.getColumnType(i)) {
                                ColumnValue.append("timestamp'").append(value).append("',");
                            } else {
                                ColumnValue.append(value).append(",");
                            }
                        }
                    }
                    System.out.println(ColumnName.toString());
                    System.out.println(ColumnValue.toString());
                    String tableName = getByIndex(tableConditionMap,j);
                    insertSQL(ColumnName, ColumnValue,tableName);
                }
                insertList.add(System.lineSeparator());
            }
        }
        return rs;
    }

    public static  String getByIndex(LinkedHashMap<String, String> hMap, int index){
        int i =0;
        Set<String> keys = hMap.keySet();
        for (String key :keys) {
            if (i == index) {
                return key;
            }
            else {
                i++;
            }
        }
        return null;
    }

    /**
     * 拼装insertsql放到全局list里面
     * @paramColumnName
     * @paramColumnValue
     */
    private static void insertSQL(StringBuffer ColumnName, StringBuffer ColumnValue,String tableName) {
        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append(insert).append(" ").append(schema).append(".")
                .append(tableName).append("(").append(ColumnName.toString()).append(")").append(values).append("(").append(ColumnValue.toString()).append(");");
        insertList.add(insertSQL.toString());
        System.out.println(insertSQL.toString());

    }

}
