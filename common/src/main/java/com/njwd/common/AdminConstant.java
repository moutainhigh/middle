package com.njwd.common;

import java.util.HashMap;
import java.util.List;

/**
* @Author XiaFq
* @Description
* @Date  2019/11/8 4:51 下午
* @Param 后台管理常量类
*/
public interface AdminConstant {

    /**
     * 字符常量
     */
    interface Character {
        String TAG_IDS = "tagIds";
        String JOINT_PARAM = "jointParam";
        String ENTERPRISEAPP_ID = "enterpriseAppId";
        String REG = "(?:')|(?:--)|(?:;)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
                + "(\\b(select|update|and|or|delete|insert|transaction|truncate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
        String BASE = "Base";
        String RELA = "Rela";
        String LEFT_JOIN = " left join ";
        String ON = " on ";
        String BASE_TABLE_START = "base_";
        String RELA_TABLE_END = "_rela";
        String SPACE = " ";
        String SEMICOLON = ";";
        String AND = " and ";
        String OR = " or ";
        String LOGIC_AND = "&&";
        String LOGIC_OR = "||";
        String MASTER_DATA = "masterData";
        String MID_PLAT_PERSPECTIVE = "MidPlat";
        String APP_PERSPECTIVE = "App";
        String ID = "id";
        String SELECT_UUID = "(SELECT UUID_SHORT()) ";
        String END_ID = "_id";
        String LEFT_PARENTHESIS = "(";
        String RIGHT_PARENTHESIS = ")";
		String MANY= "many";
        String SINGLE = "single";
		String PRIMARY_SYS = "主系统";
        String SINGLE_QUOTE = "'";
        String T = "t";
        String NOT_EQUALS = "!=";
        String CASE_WHEN = "case when ";
        String EQUALS_SPACE = " = ";
        String IS_NULL = " is null ";
        String IS_NOT_NULL = " is not null ";
        String THEN = " then ";
        String ELSE = " else ";
        String END = " end ";
        String LIKE=" like ";
        String ONE_EQUAL_ONE=" 1=1 ";
        String START_STR = "##";
        String DATA_TYPE = "data_type";
        String DATA_TYPE_CAMEL = "dataType";
        String BUSINESS_TYPE = "business_type";
        String ZERO = "0";
        String ONE = "1";
        String BASE_NAME = "中台";
        String ENTERPRISE_ID = "enterpriseId";
        String TASK_STATUS_ON = "ON";
        String TASK_STATUS_OFF = "OFF";
        String BUSINESS_TYPE_PULL_PRI = "PULL_PRI";
        String BUSINESS_TYPE_PULL_BUS = "PULL_BUS";
        String END_NAME = "_name";
        String START_THIRD = "third_";
        String COMMA = ",";
        String COMMAS = ",',',";
        String CONCAT_START = "concat(";
        String CONCAT_END = ") displayColumn";
        String ON_APP_ID = "app_id = ";
        String ON_ENTE_ID = "ente_id = ";
        String ORDER_DESC = " desc ";
        String DISPLAY_COLUMN = "displayColumn";
        String ID_TWO = "Id";
        String MINPLAT_ID = " minPlatId";
        String THIRD_ID = " thirdId";
        String EQUALS = "=";
        String APP_ID = "app_id";
        String ENTE_ID = "ente_id";
        String success = "SUCCESS";
        String COLUMN_ALIAS = "columnAlias";
        String COLUMN_DESC = "columnDesc";
        String TIPS = "tips";
        String B = "b";
        String C = "c";
        String SOURCE = "source_";
        String TRUE_CONDITION = "1 = 1 ";
        String BAK = "bak";
        String OPERATOR_TYPE = "match";
        String PARAM_NULL = "{}";
        String IN = " in ";
        String FIELD_CODE = "field_code";
        String FIELD_CODE_DESC = "字段编码";
        String CREATE_TIME="create_time";
        String UPDATE_TIME="update_time";
        String SYSTEM_CONTROL="system_control";
        String TOTAL="total";
        String THIRD_ENTE_ID ="third_ente_id";
        String ID_COLUMN_END = "_id";
        String PERCENT = "%";
    }

    interface Number {
        int ADD_SUCCESS = 0;
        int ADD_FAILED = -1;
        int ADD_EXISTS = 100022;
        int ONE = 1;
        int ZERO = 0;
        int UPDATE_SUCCESS = 0;
        int UPDATE_FAILED = -1;
        int RECORD_NOT_EXIST = 10001;
        int DELETE_SUCCESS = 0;
        int DELETE_FAILED = -1;
        int PRIMARY_SYSTEM_APPLY = 20006;
    }

    /**
     * 数据库相关
     */
    interface Db {
        String BASE_PREFIX = "base_";
        String RELA_SUFFIX = "_rela";
        String THIRD_PREFIX = "third_";
        String ID_SUFFIX = "_id";
        String THIRD_ENTE_ID = "third_ente_id";
        String ENTE_ID = "ente_id";
        String APP_ID = "app_id";
        String TABLE = "table";

        String SET = " set ";
        String SELECT = " select ";
        String INSERT_INTO = " insert into ";
        String FROM = " from ";
        String WHERE = " where ";
        String IS = " is ";
        String NULL = "  null ";
        String NOT_NULL = " not null ";

        String MID_PLAT_APP_ID = "midPlat";
        String DATE_FORMAT = "date_format";
        String DATE_FORMAT_STR = "%Y-%m-%d %H:%i";
    }

    /**
     * 任务相关
     */
    interface Task {
        String ON = "ON";
        String OFF = "OFF";
        String WAITING = "WAITING";
        String EXCUTING = "EXCUTING";
        String ERROR = "ERROR";
        String PRIMARY_SYS_SUFFIX = "_primary_source";
        String PRIMARY_JOINT_SUFFIX = "_primary_joint";
        String PRIMARY_PADDING_SUFFIX = "_primary_padding";
        String DELETE_END = "_delete";
    }

    interface Logic {
        HashMap LOGIC_MAP = new HashMap(){
            {
                put("&&", "AND");
                put("||", "OR");
            }
        };
    }

    interface JOB_PARAM_KEY {
        String ENTE_ID = "enteId";
        String APP_ID = "appId";
        String DATATYPE = "data_type";
        String ENTE_ID_DB = "ente_id";
    }

    interface DataMap{
        /**
         *查询条件已匹配的
         */
        String MAPED = "MAPED";
        /**
         *查询条件未匹配
         */
        String UNMAPED = "UNMAPED";
        /**
         *查询条件全部
         */
        String ALL = "ALL";
        /**
         *一次保存数量
         */
        int SAVESIZE = 100;
        /**
         *视角类型-源表
         */
        String SOURCE = "SOURCE";
        /**
         *视角类型-目标表
         */
        String TARGET = "TARGET";
        /**
         * 映射表 别名前缀
         */
        String MAP_TABLE_PRE="m.";
        /**
         * 源表 别名前缀
         */
        String SOURCE_TABLE_PRE="s.";
        /**
         * 目标表 别名前缀
         */
        String TARGET_TABLE_PRE="t.";
        /**
         * 映射表中，源表字段前缀
         */
        String SOURCE_PRE="source_";
        /**
         * 映射表中，源表字段前缀
         */
        String TARGET_PRE="target_";

        String KEY=" key";
        String NAME=" name";
        String CODE=" code";
        //长串分割符
        String BIG_SPILT="_____";
        //短串分割符
        String SMALL_SPILT="____";
    }

}
