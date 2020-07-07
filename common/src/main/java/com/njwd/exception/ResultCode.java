package com.njwd.exception;

/**
 * 结果类型
 *
 * @author CJ
 */
public enum ResultCode {

	/* ============================ 通用错误码 ========================== **/
	/**
	 * 禁止访问 303
	 **/
	PARAMS_NOT(303, "缺少参数"),

	/**
	 * 存在错误参数 304
	 */
	PARAMS_NOT_RIGHT(304, "存在错误参数"),

	/**
	 * 存在错误测试数据 305
	 */
	DATA_ERROR(305, "存在错误测试数据"),

	/**
	 * 存在错误测试数据 305
	 */
	SERVER_ERROR(306, "服务异常"),

	/**
	 * 错误请求 400
	 **/
	BAD_REQUEST(400, "错误请求"),

	/**
	 * 未经授权 401
	 **/
	UNAUTHORIZED(401, "未经授权"),

	/**
	 * 禁止访问 403
	 **/
	FORBIDDEN(403, "禁止访问"),
	/**
	 * 连接超时
	 **/
	TIME_OUT(408, "连接超时"),
	/**
	 * Feign连接错误
	 **/
	FEIGN_CONNECT_ERROR(409, "feignClient 连接错误：%s"),
	/**
	 * 操作异常 500
	 **/
	INTERNAL_SERVER_ERROR(500, "操作异常"),
	/**
	 * SQL异常
	 **/
	SQL_ERROR_EXCEPTION(501, "SQL执行异常!"),
	/**
	 * 操作失败
	 **/
	OPERATION_FAILURE(507, "操作失败"),
	/**
	 * 日期格式处理异常
	 **/
	DATE_FORMAT_EXCEPTION(508, "日期格式处理异常"),

	/* ============================ 业务/错误码 start========================== **/
	/* ===========================后台系统======================== **/
	RECORD_NOT_EXIST(10001, "相关记录不存在!"),
	SYS_USER_INVALID(10002, "用户登录已过期!"),
	PERMISSION_NOT(10003, "无访问权限!"),
	ACCOUNT_NOT(10004, "账号异常!"),
	SHIRO_ERROR(10005, "鉴权/授权过程出错!"),
	MENU_EXIST(10006, "菜单定义已存在！"),
	ROLE_EXIST(10007, "角色定义已存在！"),
	CODE_EXIST(10008, "编码已存在！"),
	NAME_EXIST(10009, "名称已存在!"),
	FIN_REPORT_CONFIG_EXIST(10023, "组别和类型已存在！"),
	FIN_REPORT_CONFIG_DUPLICATE(10024, "组别和类型有重复！"),
	ROLE_DELETE_HALF(10010, "部分岗位未删除成功！"),
	ROLE_REFER_USER(10011, "岗位已关联用户！"),
	SYS_USER_DISABLE(10012, "该账号已禁用！"),
	USER_ROLE_EXIST(10013, "存在关联的用户权限！"),
	BUSINESS_EXIST(10014, "存在关联的业务单元！"),
	IS_REFERENCE(10015, "当前记录被引用！"),
	USER_NOT_EXIST(10016, "您当前没有权限操作，请联系管理员授权"),
	ACCOUNT_ENTITY_NOT_EXIST(10015, "暂无核算主体！"),
	SIMPLE_NAME_EXIST(10016, "简称已存在！"),
	SOME_SYSTEM_IS_ENABLE(10017, "存在已启用子系统！"),
	ID_IS_NULL(10018, "当前记录ID为null!"),
	CREDIT_CODE_EXIST(10019, "统一社会信用代码已存在!"),
	TAXPAYER_NUMBER_EXIST(10020, "纳税人识别号已存在!"),
	REGISTE_NUMBER_EXIST(10021, "工商注册号已存在!"),
    RECORD_EXIST(100022, "该应用已被添加，请不要重复操作!"),
	TOKEN_INVALID(9997, "登录失效，请重新登录！"),
	/* ===========================数据统一======================== **/
	TASK_SWITCH_NEED_CLOSE(20001,"任务已开启，请先关闭"),
	TASK_IS_RUNNING(20002,"任务执行中，请稍等"),
	PRIMARY_SYSTEM_EXISTS(20003,"主系统已设置!"),
	PADDING_BASE_ID_SOURCE_ERROR(20004,"填充来源id字段错误，请选择相同的字段:[%s]"),
	RELY_DATA_IS_NULL(20005,"存在依赖数据为空，请确认是否选择"),
	PRIMARY_SYSTEM_APPLY(20006,"该应用已被设置为来源主系统，无法移除！"),
    PRIMARY_SYSTEM_ALREADY_SETTING(20067, "该应用已是主系统，无需重新设置！"),
	/* ============================ 文件处理 错误码 ========================== **/
	FILE_NOT_EXISTS(21001, "文件不存在"),
	FILE_MUST_IS_EXCEL(21002, "文件类型必须是excel"),
	UPLOAD_EXCEPTION(21003, "上传过程出现异常"),
	EXCEL_RULE_NOT_EXISTS(21004, "excel校验规则不存在"),
	EXCEL_TEMPLATE_NOT_EXISTS(21005, "excel模板不存在"),
	EXCEL_TEMPLATE_NOT_CORRECT(21009, "excel模板不对，请重新下载"),
	EXCEL_DATA_NOT_EXISTS(21006, "excle数据不存在"),
	EXCEL_NOT_CORRECT(21007, "excel不正确"),
	EXCEL_PARSE_CORRECT(21010,"excel解析错误"),
	CURRENT_MONTH_NOT_EXISTS(21008, "当前年月数据不存在"),
	BATCH_NOT_NEED(21009, "无需批量操作！"),
	CELL_IS_NULL(21010, "值不能为空"),
	CELL_FORMAT_ERROR(21011, "值格式不正确"),
	CELL_MIN_LENGTH_ERROR(21012, "值小于最小长度"),
	CELL_MAX_LENGTH_ERROR(21013, "值大于最大长度"),
	CELL_REGULAR_ERROR(21014, "值正则验证不通过"),
	CELL_OPTION_ERROR(21015, "值不在选项集范围内"),
	ROW_DUPLICATE_ERROR(21016, "行重复性校验未通过"),
	CELL_DATE_FORMAT_ERROR(21017,"日期范围格式不正确"),
	SHUT_DOWN_DATE_ERROR(21018, "开业时间不能小于关店时间"),

	/* ===========================schedule======================== **/
	SCHEDULE_SERVER_ERROR(30001,"Server error"),
	SCHEDULE_PARAM_UNUNIQUE(30002,"该企业已经初始化，不能再次执行!"),
	SCHEDULE_PARAM_ERRORCODE_FORMAT_ERR(30003,"errorCode格式不正确!"),
	SCHEDULE_PARAM_RESULT_FORMAT_ERR(30004,"result格式不正确!"),
	SCHEDULE_NO_ENTE_BUSSINESS_ERR(30005,"task不存在!"),

    /* ===========================数据映射======================== **/
    DATA_MAP_KEYCOUNT_WRONG(40001,"key数量不正确"),
    PARAM_EXISIS_SQL_INJECT(40002,"参数存在sql注入风险"),
	DUPLICATE_KEY(40003,"数据重复"),

	/* ============================ 外部接口/错误码 start========================== **/
	CRUISE_SHOP_PORT_ERROR(50001,"巡店接口请求失败"),

	/* ===========================主数据修改======================== **/
	DISABLE_NEED_OPENING_DATE(60001,"关停门店前，需要先完善门店开业信息"),
	SHOP_ALREADY_SHUTDOWN(60002,"门店已是闭店状态，无法再次关闭"),
	SHOP_ALREADY_OPENING(60003,"门店已是开业状态，无法再次开业"),
	SHOP_AREA_IS_NULL(60004,"门店面积不能为空！"),
	SHOP_OPENING_DATE_IS_NULL(60005,"门店开业时间不能为空！"),
	SHOP_STATUS_INCORRECT(60006,"门店已关停，状态不符！"),
	SHOP_STATUS_UNIDENTIFIED(60007,"门店状态无法识别"),

	BEER_FEE_IS_DISABLE(60008,"当前啤酒进场费已禁用"),
	BEER_FEE_IS_ENABLE(60009,"当前啤酒进场费已启用"),
	BUSINESS_DAILY_INDIC_IS_DISABLE(60010,"当前经营日报指标已禁用"),
	BUSINESS_DAILY_INDIC_IS_ENABLE(60011,"当前经营日报指标已启用"),
	DISCOUNTS_SAFE_IS_DISABLE(60012,"当前退赠优免安全阀值已禁用"),
	DISCOUNTS_SAFE_IS_ENABLE(60013,"当前退赠优免安全阀值已启用"),
	PROFIT_BUDGET_IS_DISABLE(60014,"当前实时利润预算已禁用"),
	PROFIT_BUDGET_IS_ENABLE(60015,"当前实时利润预算已启用"),
	/* ===========================KettleJob======================== **/
	PROPERTIES_FILE_NOT_EXIST(61001,"jdbc.properties配置文件不能为空!"),
    KETTLE_INFO_NOT_EXIST(61002,"KETTLE入参不能为空!"),
    KETTLE_FILE_NOT_EXIST(61003,"KETTLE文件路径不能为空!"),
	KETTLE_URL_NULL_ERROR(61004,"数据库地址不能为空！"),
	KETTLE_PORT_NULL_ERROR(61005,"数据库端口号不能为空！"),
	KETTLE_DBNAME_NULL_ERROR(61006,"数据库名称不能为空！"),
	KETTLE_USERNAME_NULL_ERROR(61007,"数据库用户名不为空！"),
	KETTLE_PASSWORD_NULL_ERROR(61008,"数据库密码不能为空！"),
	KETTLE_CONNECTIONNAME_NULL_ERROR(61009,"KETTLE连接名不能为空！"),
	/* ===========================ScheduleWorkerError======================== **/
	TASK_SERVICE_URL_NULL_ERROR(70001,"应用服务接口地址不能为空!"),
	TASK_SERVICE_USER_NULL_ERROR(70002,"应用服务接口用户名不能为空！!"),
	TASK_SERVICE_PASSWORD_NULL_ERROR(70003,"应用服务接口密码不能为空!"),
	TASK_SERVICE_ENTEID_NULL_ERROR(70004,"任务参数企业id不能为空!"),
	TASK_SERVICE_APPID_NULL_ERROR(70005,"任务参数应用id不能为空!"),
	TASK_DATABASE_COUNT_ERROR(70006,"kettle服务，数据库信息参数不能小于两个！"),
	TASK_DATABASE_URL_NULL_ERROR(70007,"数据库地址不能为空！"),
	TASK_DATABASE_PORT_NULL_ERROR(70008,"数据库端口号不能为空！"),
	TASK_DATABASE_DBNAME_NULL_ERROR(70009,"数据库名称不能为空！"),
	TASK_DATABASE_USERNAME_NULL_ERROR(70010,"数据库用户名不为空！"),
	TASK_DATABASE_PASSWORD_NULL_ERROR(70011,"数据库密码不能为空！"),
	TASK_DATETIME_HEARTTIME_FORMAT_ERROR(70011,"心跳时间格式错误！"),
	TASK_DATETIME_STARTTIME_FORMAT_ERROR(70011,"STARTTIME格式异常！"),
	TASK_DATETIME_ENDTIME_CALCULATION_ERROR(70011,"ENDTIME计算异常！"),
	/* ===========================JoyhrError======================== **/
	SIGN_ERROR(80001,"签名验证错误!"),
	METHOD_ERROR(80002,"方法不存在!"),
	/* ===========================CrmHookError======================== **/
	CRM_MEMBER_FOUND_ERROR(81001,"会员信息已存在"),
	CRM_MEMBER_NOT_FOUND_ERROR(81002,"会员信息不存在"),

	/* =============报表 ================= **/
	SUBJECT_CONFIG_ERROR(33001,"科目配置缺失"),
	TABLE_EXISTS(33002, "表名已存在"),
	EXCEL_TEMPLATE_CREATE_ERROR(33003, "EXCEL模板创建失败"),
	SETTING_MODEL_IS_ENABLE(33004, "设置模块已启用"),
	SETTING_MODEL_IS_DISABLE(33005, "设置模块未启用"),
	SETTING_MODEL_DATA_UPDATE_ERROR(33006, "数据更新失败"),
	FIN_REPORT_CONFIG_ERROR(33007, "公式未配置"),
	EXCEL_NULL_ERROR(33008, "EXCEL数据不存在"),
	DATE_DUPLICATE_ERROR(33009, "门店下有效期重复"),

	/* =============供应链 ================= **/
	SCM_ORG_NOT_EXISTS(88001, "门店对应的组织不存在"),
	SCM_SHOP_PRICE_CONFIG_LACK(88002, "缺少该门店对应的价格"),

	/* =============主系统更改Error ================= **/
    MASTER_DATA_RELY_ERROR(90001,"切换系统与中台数据存在数据不匹配"),

    RELY_DATA_NOT_MATCH_BASE(90002,"切换系统依赖数据与中台数据存在不匹配数据"),

    BUS_DATA_NOT_MATCH_BASE(90003,"切换系统业务数据与中台数据存在不匹配数据"),

    DATA_MAPPING_NOT_MATCH_BASE(90004,"切换系统数据映射中数据与中台数据存在不匹配数据"),

	/* =============中台大平台系统 ================= **/
	LOGIN_TIME_OUT(130001,"登录失效或未登录，请重新登录"),
	VERFICATION_CODE_ERROR(130002,"验证码不正确"),
	NO_USER(130003,"用户不存在"),
	PASSWORD_ERROR(130004,"密码不正确"),
	MOBILE_USED(130005,"该手机号码已被注册"),
	BANK_ACCOUNT_USED(130006,"该银行账号已被使用"),
	SCORE_TOO_BIG(130007,"分数最高只能打5.0分"),
    REMARK_TOO_LONG(130008,"评论输入需小于200字"),
	OPEN_GOODS_FAIL(130009,"开通失败，请重新开通或联系相关人员"),
	PROBATION_GOODS_FAIL(130010,"试用失败，请重新试用或联系相关人员"),
	END_GOODS_FAIL(130011,"停用失败，请重新停用或联系相关人员"),
	FIND_ORDER_LIST_FAIL(130012,"查询订单列表失败，请重新查询或联系相关人员"),
	FIND_BILL_FAIL(130013,"查询账单列表失败，请重新查询或联系相关人员"),
	FIND_SIX_MONTH_BILL(130014,"查询近6个月消费失败"),
	FIND_MONTH_GOODS(130015,"查询该月份消费情况失败"),
	FIND_INVOICE_RISE_LIST_FAIL(130016,"查询发票（发票抬头）列表失败"),
	ADD_INVOICE_FAIL(130017,"新增发票抬头失败"),
	UPDATE_INVOICE_FAIL(130018,"修改发票抬头失败"),
	DELETE_INVOICE_FAIL(130019,"删除发票抬头失败"),
	FIND_ADDRESS_FAIL(130020,"查询寄送地址列表失败"),
	ADD_ADDRESS_FAIL(130021,"新增寄送地址失败"),
	UPDATE_ADDRESS_FAIL(130022,"修改寄送地址失败"),
	UPDATE_ADDRESS_DEFAULT_FAIL(130023,"修改默认地址失败"),
	DELETE_ADDRESS_FAIL(130024,"删除寄送地址失败"),
	FIND_BILL_FOR_INVOICE(130025,"查询可开票账单列表失败"),
	FIND_LIST_INVOICE_FAIL(130026,"查询已开票列表失败"),
	FIND_INVOICE_AMOUNT(130027,"查询可开票金额失败"),
	FIND_INVOICE_DETAIL_FAIL(130028,"查询发票明细失败"),
	ADD_INVOICE_TO_FAIL(130029,"开票失败，请重新开票或联系相关人员"),
	UPDATE_INVOICE_TO_FAIL(130030,"修改开票状态失败"),
	FIND_INDEX_DATA_FAIL(130031,"查询多项数据失败"),
	ADD_RECHARGE(130032,"发起支付请求失败"),
	FIND_PAY_ENTRANCE_FAIL(130033,"获取支付入口失败"),
	FIND_PREPAID_PAY(130034,"获取支付状态失败"),
	FIND_PREPAID_RECORD_FAIL(130035,"查询充值记录失败"),
	FIND_PREPAID_BALANCE(130036,"查询余额失败"),
	CRM_CARD_FAIL(130037,"会员系统生成会员失败"),
	CRM_UPDATE_MOBILE_FAIL(130038,"会员系统修改手机号失败"),
	CRM_UPDATE_EMAIL_FAIL(130039,"会员系统修改邮箱失败"),
	FIND_VERFICATION_CODE_ERROR(130040,"获取验证码失败"),
	VERFICATION_CODE_TIME_OUT(130041,"验证码超时或手机号码输入错误"),
	MOBILE_USED_OR_NOT_CHANGE(130042,"您输入的新号码与原号码相同或该新号码已他人被注册"),
	MOBILE_DIFFERENT(130043,"您输入的原号码与实际登录号码不同"),

	/* =============数据维护 ================= **/
	TOTAL_SYNC_RECORD_EXITS(110001,"已执行过全量同步！"),
	TOTAL_SYNC_RECORD_NOT_EXITS(110002,"还没执行全量同步！");


	public final int code;
	public String message;

	ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
