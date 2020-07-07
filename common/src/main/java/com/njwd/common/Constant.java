package com.njwd.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用查询参数
 *
 * @author luoY
 * @since 2019/10/30
 */
public interface Constant {

	/**
	 * 异常类常量值
	 */
	interface Exception {
		String ERRORSQL = "Error SQL";

		/**
		 * 巡店接口返回
		 */
		String ERROR = "error";
	}

	/**
	 * 系统名相关
	 */
	interface Context {

		/**
		 * 报表系统的nacos注册服务名
		 */
		String REPORT_FEIGN = "report-data";
	}

	interface ExcelConfig {
		/**
		 * excel缓存
		 */
		String EXCEL_KEY_PREFIX = "excel:cache:%s";
	}


	/**
	 * 常用列名
	 */
	interface ColumnName {
		String IS_DEL = "is_del";
	}


	/**
	 * 返回结果 success：成功，fail：业务返回的失败，error：非业务异常失败
	 */
	interface ReqResult {
		String SUCCESS = "success";
		String FAIL = "fail";
		String ERROR = "error";
	}

	/**
	 * 组织类型
	 */
	interface OrgType {
		/**
		 * 企业
		 */
		Byte ENTE = 0;
		/**
		 * 公司
		 */
		Byte COMPANY = 1;
		/**
		 * 部门
		 */
		Byte DEPT = 2;
		/**
		 * 门店
		 */
		Byte SHOP = 3;
		/**
		 * 区域
		 */
		Byte REGION = 4;
		/**
		 * 区域
		 */
		Byte BRAND = 5;
	}

	/**
	 * 组织类型
	 */
	interface DateType {
		/**
		 * 日
		 */
		int DAY = 0;
		/**
		 * 周
		 */
		int WEEK = 1;
		/**
		 * 月
		 */
		int MONTH = 2;
		/**
		 * 季
		 */
		int QUART = 3;
		/**
		 * 年
		 */
		int SEASON = 4;
		/**
		 * 自定义
		 */
		int CUSTOMIZE = 5;
		/**
		 * 半年
		 */
		int HALF_YEAR = 6;

	}

	/**
	 * 门店类型编码
	 */
	interface ShopTypeCode {
		/**
		 * 自营
		 */
		String SELFSUPPORTCODE = "01";
		/**
		 * 全托
		 */
		String FULLSUPPORTCODE = "02";
		/**
		 * 合作
		 */
		String COOPERATIONCODE = "03";
		/**
		 * 半托
		 */
		String HALFSUPPORTCODE = "04";
	}

	/**
	 * 常用数值
	 */
	interface Number {
		Integer MINUS_ZERO = -1;
		Integer ZERO = 0;
		Integer ONE = 1;
		Integer TWO = 2;
		Integer THREE = 3;
		Integer FOUR = 4;
		Integer FIVE = 5;
		Integer SIX = 6;
		Integer SEVEN = 7;
		Integer EIGHT = 8;
		Integer NINE = 9;
		Integer TEN = 10;
		Integer TWENTYFOUR = 24;
		Integer TWENTYFIVE = 25;
		Integer TWENTYSIX = 26;
		Integer THIRTYTWO = 32;
		Integer THIRTYTHREE = 33;
		Integer THIRTYFOUR = 34;
		Integer FOURTY = 40;
		Integer LENGTH = 3;
		Integer NINETYNINE = 99;
		Integer ONEHUNDRED = 100;
        Integer ONETHOUSAND =1000;
		Long ZEROL = 0L;
		Long ONEL = 1L;
		Long COMPANY = 2L;
		Long BUSINESS = 3L;
		Long ONETHOUSANDL = 1000L;
		Byte ONEB = 1;
		Byte TWOB = 2;
		Byte THREEB = 3;
		Byte ANTI_INITLIZED = 0;
		Byte INITLIZED = 1;
		Double ZBXS = 0.01;
		BigDecimal ZEROB = new BigDecimal(0);
		BigDecimal ONE_B = new BigDecimal(1);
		BigDecimal ONE_FO = new BigDecimal(1.0000);
		BigDecimal ZEROBXS = new BigDecimal(0.00);
		BigDecimal HUNDRED = new BigDecimal(100.00);
		BigDecimal FIVETEEN = new BigDecimal(15);
		BigDecimal THIRTYONE = new BigDecimal(31);
		Double ZEROD = 0D;
		BigDecimal BEER_NUM = new BigDecimal(12);
	}

	/**
	 * 当前环境 dev：开发, test：测试, prod：生产
	 */
	interface ProfileActive {
		String DEV = "dev";
		String TEST = "test";
		String PROD = "prod";
		String LIANTIAO = "liantiao";
	}

	/**
	 * Service缓存value
	 */
	interface RedisCache {
		/**
		 * Redis key 分隔符（@Cacheable自动生成的分隔符）
		 */
		String redisSeparator = "::";
	}

	/**
	 * 字典值
	 */
	interface TabColumnCodeCache {

		String ONE_STR = "101";
		String TOW_STR = "102";
		String THREE_STR = "103";
		String FOUR_STR = "104";
		String FIVE_STR = "105";
		String SIX_STR = "106";
		String SEVEN_STR = "107";
		String EIGHT_STR = "108";
		String NINE_STR = "109";
		String TEN_STR = "110";
		String ELEVEN_STR = "111";
		String TWELVE_STR = "112";
		String THIRTEEN_STR = "113";
		String FOURTEEN_STR = "114";
		String FIFTEEN_STR = "115";
		String SIXTEEN_STR = "116";
		String SEVENTEEN_STR = "117";
		String EIGHTEEN_STR = "118";
		String NINETEEN_STR = "119";
		String TWENTY_STR = "120";
		String TWENTY_ONE_STR = "121";
		String TWENTY_TOW_STR = "122";
		String TWENTY_THREE_STR = "123";

	}

	/**
	 * 系统配置
	 */
	interface SysConfig {
		/**
		 * 同步锁自动超时时间(单位:秒)
		 */
		long REDIS_LOCK_TIMEOUT = 20;
		/**
		 * 凭证操作同步锁自动释放时间(单位:秒)
		 */
		long VOUCHER_LOCK_TIMEOUT = 60;
		/**
		 * 结账操作锁自动释放时间(单位:秒)
		 **/
		long SETTLE_ACCOUNT_LOCK_TIMEOUT = 60 * 20;
		/**
		 * 结账后是否允许修改现金流量
		 **/
		byte SETTLE_ALLOW_EDIT_CASH_FLOW = 1;
		/**
		 * 记录超时接口阈值
		 **/
		long LONG_TIME_THRESHOLD = 1000;
		/**
		 * 预警耗时日志打印
		 **/
		String LONG_OUT_TIME_LOG = "接口预警：接口：{} 耗时：{}毫秒";
		/**
		 * 预警耗时日志打印
		 **/
		String LONG_TIME_LOG = "接口耗时：接口：{} 耗时：{}毫秒";
	}


	/**
	 * 常用字符
	 */
	interface Character {
		String GROUP_CODE = "0000";
		String QUESTION = "?";
		String EQUALS = "=";
		String AND = "&";
		String COLON = ":";
		String ASTERISK = "*";
		String POINT = ".";
		String COMMA = ",";
		String BRACKET_LEFT_B = "{";
		String BRACKET_RIGHT_B = "}";
		String ZERO = "00";
		String ONE = "001";
		String String_ZERO = "0";
		String String_ZEROB = "0.00";
		String NULL_VALUE = "";
		String UNDER_LINE = "_";
		String MIDDLE_LINE = "-";
		String MIDDLE_WAVE = "～";
		String VIRGULE = "/";
		Byte FAIL = 0;
		Byte SUCCESS = 1;
		Byte IS_REFERENCE = 2;
		String Percent = "%";
		String UTF8 = "utf-8";
		String HASH_SIGN = "#";
		String COMMA_SINGLE_QUOTE_SEPERATOR = "','";
		String DOUBLE_COMMA = ",,";
		String ON = "ON";
		String OFF = "OFF";
		String TIP_FLAG = "tipFlag";
		String ALIAS_R = "r";
	}


	/**
	 * 是否
	 */
	interface Is {
		Byte YES = 1;
		Byte NO = 0;
		Integer NO_INT = 0;
		Integer YES_INT = 1;
	}

	/**
	 * kettle执行job类型
	 */
	interface KettleType {
		/**
		 * ktr文件
		 */
		String ktr = "ktr";
		/**
		 * kjb文件
		 */
		String kjb = "kjb";
	}

	/**
	 * 巡店常量
	 */
	interface CruiseShop {

		/**
		 * 开始日期
		 */
		String START = "start";

		/**
		 * 结束日期
		 */
		String END = "end";


		/**
		 * 巡店ID
		 */
		String ID = "id";

		/**
		 * 巡店密码
		 */
		String PASSWORD = "password";
		/**
		 * 巡店编码
		 */
		String CODE = "code";

		/**
		 * 巡店查询条件
		 */
		String CONDITION = "?condition=";

		/**
		 * 巡店获取COOKIEurl
		 */
		String URL = "url";


		/**
		 * 企业
		 */
		String ENTEID = "enteId";

		/**
		 * app
		 */
		String APPID = "appId";


	}

	/**
	 * 报表常量
	 */
	interface Report {

		/**
		 * 企业
		 */
		String ENTEID = "enteId";

		/**
		 * 品牌
		 */
		String BRANDID = "brandId";

		/**
		 * 区域
		 */
		String REGIONID = "regionId";

		/**
		 * 项目类型
		 */
		String TYPEID = "typeId";

		/**
		 * 门店ID
		 */
		String SHOPID = "shopId";
		/**
		 * 项目名称
		 */
		String TYPE = "type";


	}

	/**
	 * 台型常量
	 */
	interface CruiseDesk {
		/**
		 * 合计
		 */
		String TOTAL_NUMBER = "合计";
		/**
		 * 合计 ID
		 */
		String TOTAL_NUMBER_ID = "total";
	}

	/**
	 * excel错误信息
	 */
	interface ExcelErrorMsg {
		String ERROR_LENGTH = "不能超过长度%d";
		String ERROR_EMPTY = "不能为空";
		String ERROR_INSERT = "数据入库异常:%s";
		String ERROR_CONVERT = "数据转换失败:%s";
	}

	/**
	 * 门店开业状态
	 */
	interface BaseShopStatus {
		/**
		 * 关停
		 */
		String SHUTDOWN = "关停";
		/**
		 * 营业
		 */
		String OPENING = "营业";
	}

	/**
	 * 门店开业状态 对应值
	 */
	interface BaseShopStatusValue {
		/**
		 * 正常
		 */
		String OPENING = "0";
		/**
		 * 关店
		 */
		String SHUTDOWN = "1";
	}

	interface LineNumber {
		/**
		 * 表格数据 首值所处行
		 */
		int FIRST_VALUE_NUMBER = 2;
	}

	/**
	 * 微生活crm
	 */
	interface AceWillCrm {

		/**
		 * 开始日期
		 */
		String BEGIN_DATE = "begin_date";

		/**
		 * 结束日期
		 */
		String END_DATE = "end_date";

		/**
		 * 页数
		 */
		String PAGE = "page";
		/**
		 * 是否全部流水,是:true 否:false[默认，只查接口来源的流水]
		 */
		String IS_ALL = "is_all";
		/**
		 * 是否包括分页信息 是:true 否:false[默认]
		 */
		String IS_HAVE_PAGE = "is_have_page";
		/**
		 * 【默认】消费：consume，储值：charge(用户查询微生活支付方式)
		 */
		String OPTYPE = "optype";

		/**
		 * 请求参数
		 */
		String REQ = "req";
		/**
		 * 微生活appid
		 */
		String APPID = "appid";
		String APPKEY = "appkey";
		/**
		 * 版本
		 */
		String VERSION = "v";
		/**
		 * 时间戳
		 */
		String TS = "ts";
		/**
		 * 签名
		 */
		String SIG = "sig";
		/**
		 * 返回格式
		 */
		String FMT = "fmt";

		/**
		 * 返回编码
		 */
		String ERRCODE = "errcode";
		/**
		 * 用于微生活接口参数v=2.0
		 */
		String VERSION_VALUE = "2.0";
		/**
		 * 用于微生活接口参数fmt=JSON
		 */
		String ACEWILL_FMT = "JSON";
		/**
		 * 获取url
		 */
		String URL = "url";

		/**
		 * 门店接口
		 */
		String ACEWILL_SHOP_LIST_URL = "shop/list";

		/**
		 * 会员消费记录
		 */
		String ACEWILL_CONSUME_LIST_URL = "/consume/list";
		/**
		 * 会员充值记录
		 */
		String ACEWILL_PREPAID_LIST_URL = "/charge/list";
		/**
		 * 会员等级
		 */
		String ACEWILL_GRADE_LIST_URL = "/grade/rule";

		/**
		 * 支付方式
		 */
		String ACEWILL_PAYTYPE_LIST_URL = "/deal/getallpaytype";
	}

	/**
	 * 接口信息
	 */
	interface InterfaceMethod {
		/**
		 * 自动登录
		 */
		String AUTO_LOGIN = "autoLogin";

		/**
		 * 修改密码
		 */
		String MODIFY_PASSWORD = "modifyPwd";

		/**
		 * 开通账号
		 */
		String OPEN_ACCOUNT = "openAccount";

		/**
		 * 回收账号
		 */
		String CLOSE_ACCOUNT = "closeAccount";

		/**
		 * key
		 */
		String KEY = "c5ad5753097bf9f165e839b8d06d4380";

		/**
		 * sign格式
		 */
		String SIGN_FORMAT = "%s_%s_%s";

		/**
		 * 参数系统标识
		 */
		String PLATFORM = "platform";
	}

	/**
	 * url
	 */
	interface Url {
		String mainUrl = "/middle-data";
		String topUrl = "/admin/";
		String exUrl = "http://115.29.243.81:8196/sso/authentication/getLoginUserInfoByToken";
		String redirectAdminUrl = "http://115.29.243.81/middle-data-admin";
		String redirectReportUrl = "http://115.29.243.81/middle-data-frontend";
	}

	/**
	 * session时效
	 */
	interface GetSessionTime {
		int SESSION_TIME_2H = 60 * 60 * 2;
	}

	/**
	 * 退赠
	 */
	interface RetreatAndGrive {
		String RETREAT = "退菜";
		String GRIVE = "赠菜";
	}

	/**
	 * 正则表达式常量
	 */
	interface RegExp {
		/**
		 * 括号，包括大括号和小括号
		 */
		String BRACKETS = "\\{|\\}|\\(|\\)";
		/**
		 * 不可见字符
		 */
		String INVISIBLE = "\\s";
		/**
		 * 运算符 加减乘除
		 */
		String OPERATOR = "\\+|\\-|\\*|\\/";
		/**
		 * 数字，包括整形和浮点型
		 */
		String NUMBER = "'(\\d|\\.)+'";
		/**
		 * 逗号开头或结尾
		 */
		String COMMA_START_OR_END = "^,|,$";
		/**
		 * 井号左括号
		 */
		String HASH_SIGN_BRACKET_LEFT = "#\\{";
		/**
		 * 右括号
		 */
		String BRACKET_RIGHT = "\\}";
		/**
		 * #{内容}，占位符内容
		 */
		String PLACE_HOLDER_CONTENT = "#\\{([^}])*\\}";
		/**
		 * #{参数map.
		 */
		String PLACE_HOLDER_PARAM_MAP = "#\\{paramMap.";
		/**
		 * 井号或者大括号
		 */
		String HASH_SIGN_BRACKETS = "#|\\{|\\}";
		/**
		 * 空请求体
		 */
		String EMPTY_REQUEST_BODY = "\\{\\s+\\}";

	}

	/**
	 * 编码层级
	 */
	interface CodeLevel {
		Integer PARENT = 1;

	}

	/**
	 * 任务返回
	 */
	interface TaskResult {
		/**
		 * 返回状态
		 */
		String STATUS = "status";
		/**
		 * 更新数量
		 */
		String EFFECTCOUNT = "effectCount";

		/**
		 * 业务系统填充后的参数
		 */
		String PARAM = "param";
		/**
		 * 业务系统填充后的参数
		 */
		String MSG = "msg";
		/**
		 * 运行时参数
		 */
		String RUNTIMEPARAM = "runtimeParam";
		/**
		 * 任务参数返回门店标识
		 */
		String SHOPCODE = "shopCode";
		/**
		 * 任务参数返回支付方式标识
		 */
		String PAYTYPECODE = "payTypeCode";
		/**
		 * 任务参数返回交易类型标识
		 */
		String TRANSACTIONTYPECODE = "transactionTypeCode";
	}

	/**
	 * 模版类型
	 */
	interface TemplateType {
		String BUSINESS_DAILY = "business_daily";
	}

	/**
	 * 数据类型 1.金额，2.占比，3.数量，99.无操作
	 */
	interface DataType {
		int MONEY = 1;
		int PERCENT = 2;
		int NUMBER = 3;
		int NONE = 99;
	}


	/**
	 * 巡店评分
	 */
	interface BusinessScore {
		/**
		 * 总分
		 */
		String SCORE_SUM = "总分";
		/**
		 * 总分类型
		 */
		String SCORE_SUM_TYPE = "-1";
	}

	/**
	 * 是否需要计算 上期 去年同期总分
	 */
	interface CountLastFlag {
		/**
		 * 是
		 */
		boolean TRUE = true;
		/**
		 * 否
		 */
		boolean FALSE = false;
	}

	/**
	 * 本期 同比 环比 标识前缀
	 */
	interface ComparePrefixFlag {
		/**
		 * 本期
		 */
		String CURRENT = "本期_";
		/**
		 * 去年同期
		 */
		String LAST_YEAR = "去年同期_";
		/**
		 * 上期
		 */
		String LAST_PERIOD = "上期_";
		/**
		 * 环比分析
		 */
		String LAST_PERIOD_COMPARE = "环比分析_";
		/**
		 * 同比分析
		 */
		String LAST_YEAR_COMPARE = "同比分析_";
	}

	/**
	 * 本期 同比 环比 标识前缀
	 */
	interface DBConnectType {
		/**
		 * jdbc
		 */
		String JDBC = "JDBC";
		/**
		 * ODBC
		 */
		String ODBC = "ODBC";
		/**
		 * OCI
		 */
		String OCI = "OCI";
		/**
		 * JNDI
		 */
		String JNDI = "JNDI";
	}

	/**
	 * 乐才hr
	 */
	interface Joyhr {
		String STAMP = "stamp";
		String SIGN = "sign";
		String COMPANYID = "CompanyID";
		String DepartCode = "DepartCode";
		String STATUS = "Status";
		String COUNT = "Count";
		String DATE = "Date";
		String UPDATE_DATE = "UpdateDate";
		String UPDATE_DATE2 = "Updatedate";
		String DEPART_ID = "DepartID";
		String IS_ON_DUTY = "IsOnduty";
		String PAGE_INDEX = "PageIndex";
		String PAGE_SIZE = "PageSize";
		String START_DATE = "StartDate";
		String END_DATE = "EndDate";
		String SALARY_DATE = "SalaryDate";
		String TASK_KEY = "taskKey";
		String NOW_KEY = "nowKey";
	}

	/**
	 * 内容是否有修改 true有修改  false 无修改
	 */
	interface IsHasUpdated {
		Boolean Yes = true;
		Boolean No = false;
	}

	/**
	 * 验签
	 */
	interface CheckSign {
		/**
		 * 验签map
		 */
		Map<String, Object> SIGN_MAP = new HashMap<String, Object>() {
			{
				/**
				 *秘钥
				 */
				put("MD_PRIVATE_KEY", "IV8LQ1WRWXB7U83AXJ37WG8W3B271G");
				/**
				 * key
				 */
				put("MD_KEY", "middle_data");
			}
		};
	}

	/**
	 * 区分集成中台or数据中台
	 */
	interface isAdminOrReport {
		/**
		 * 集成中台
		 */
		String isAdmin = "0";
		/**
		 * 数据中台
		 */
		String isReport = "1";
	}

	/**
	 * 报表同比环比阀值CODE
	 */
	interface ThresholdCode {
		/**
		 * 台型分析
		 */
		String deskTypeAnalysis = "deskTypeAnalysis";
	}

	/**
	 * 报表同比环比阀值比较状态
	 */
	interface ThresholdCompareStatus {
		/**
		 * 大于阀值
		 */
		boolean gt = true;
		/**
		 * 小于等于阀值
		 */
		boolean le = false;
	}

	/**
	 * 餐别
	 */
	interface MealId {
		/**
		 * 午市
		 */
		String afternoonMarket = "2";

		/**
		 * 晚市
		 */
		String eveningMarket = "4";

		/**
		 * 夜宵
		 */
		String midnightSnack = "5";
	}

	/**
	 * 模板类型 模板1：1,模板2：2,模板3：3
	 */
	interface ModelType {
		String MODEL_TYPE_ONE = "1";
		String MODEL_TYPE_TWO = "2";
		String MODEL_TYPE_THR = "3";
	}

	/**
	 * 横坐标类型  24小时：hh24 ,日:DD,月：MM
	 */
	interface AbscissaType {
		String HOUR_TYPE = "hh24";
		String DATE_TYPE = "DD";
		String MONTH_TYPE = "MM";
	}

	/**
	 * dict常量
	 */
	interface DictValue {
		String GROSS_PROFIT_INCOME_DISH = "gross_profit_income_dish";
		String GROSS_PROFIT_INCOME_WINE = "gross_profit_income_wine";
		String GROSS_PROFIT_INCOME_CONDIMENT = "gross_profit_income_condiment";
		String POS_ORDER_CASH_AVG = "pos_order_cash_avg";
		String POS_ORDER_DETAIL_FOOD = "pos_order_detail_food";
	}

	/**
	 * 毛利分析表字段名
	 */
	interface GrossProfitColumnName {
		String INCOME_DISH = "income_dish";
		String INCOME_WINE = "income_wine";
		String SALE_INCOME_CONDIMENT = "sale_income_condiment";
	}

	/**
	 * 报表任务加锁
	 */
	interface TransferFinancialReport {
		String BALANCE_REPORT = "balanceReport";
		String CASH_FLOW_REPORT = "cashFlowReport";
		long TIME_OUT = 1800L;
	}
}


