package com.njwd.entity.kettlejob;

import com.njwd.common.KettleJobConstant;
import lombok.Data;

/**
 * @Description 微生活 返回实体
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class CrmWebHookResult {

	private Integer errcode = KettleJobConstant.WebHookSuccess.ERRCODE;

	private String errmsg = KettleJobConstant.WebHookSuccess.ERRMSG;

	public CrmWebHookResult() {

	}

	public CrmWebHookResult(Integer errcode, String errmsg) {
		this.errcode = errcode;
		this.errmsg = errmsg;
	}

	void setCrmWebHookResult(Integer errcode, String errmsg) {
		this.errcode = errcode;
		this.errmsg = errmsg;
	}

}
