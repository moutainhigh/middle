package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 乐才对接接口返回结果
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrResult<T> {

	/**
	 * status 0	正常：表示逻辑正常，不代表任何具体操作结果
	 */
	@JSONField(name = "Status")
	private Integer status = -1;

	/**
	 * count
	 */
	@JSONField(name = "Count")
	private Long count = 0L;

	/**
	 * message
	 */
	@JSONField(name = "Msg")
	private String message;

	/**
	 * error message
	 */
	@JSONField(name = "ErrorMsg")
	private String errorMessage;

	/**
	 * 返回内容主体
	 */
	@JSONField(name = "ResultContent")
	private List<T> resultContent = new ArrayList<>();

	/**
	 * 返回内容主体 2
	 */
	@JSONField(name = "Data")
	private T data;

	/**
	 * 接口的请求时间
	 */
	private Date date;
}
