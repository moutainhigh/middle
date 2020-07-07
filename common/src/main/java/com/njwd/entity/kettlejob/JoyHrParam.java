package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.utils.StringUtil;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description 乐才对接接口参数
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrParam {

	private Integer pageSize = 1000;

	private Integer pageIndex = 1;

	/**
	 * 当前时间，同时对应时间戳时间
	 */
	private Date nowDate;
	private Date apiDate;
	/**
	 * URL
	 */
	private String url;

	/**
	 * APP KEY
	 */
	private String appKey;

	/**
	 * REQUEST JSON
	 */
	private JSONObject jsonObject;

	/**
	 * 是否增量
	 */
	private Boolean isAdd;

	/**
	 * 参数是否通过校验
	 */
	private Boolean isPass = false;

	public JoyHrParam() {
	}


	public JoyHrParam(String appId, String enteId, Date date, String apiUrl, Map<String, Object> paramMap, List<String> canList, List<String> checkList) {
		Object urlObj = paramMap.get(ScheduleConstant.AppInterface.URL);
		Object appKeyObj = paramMap.get(ScheduleConstant.AppInterface.APPKEY);

		// 必传校验
		if (appId == null || StringUtil.isEmpty(appId) || enteId == null || StringUtil.isEmpty(enteId)
				|| urlObj == null || StringUtil.isEmpty(urlObj.toString())
				|| appKeyObj == null || StringUtil.isEmpty(appKeyObj.toString())) {
			return;
		}
		this.url = urlObj.toString() + apiUrl;
		this.appKey = appKeyObj.toString();
		this.jsonObject = new JSONObject();

		//循环获取到能传入的参数
		Object canObject;
		for (String canValue : canList) {
			canObject = paramMap.get(canValue);
			//判断这个参数是否需要进行必填校验
			if (checkList.contains(canValue)) {
				if (canObject == null || StringUtil.isEmpty(canObject.toString())) {
					return;
				}
			} else if (canObject == null) {
				canObject = "null";
			}
			this.jsonObject.put(canValue, canObject);
		}

		//签名
		this.nowDate = date;
		this.apiDate = new Date();
		String ts = apiDate.getTime() + "";
		jsonObject.put(Constant.Joyhr.STAMP, ts);
		jsonObject.put(Constant.Joyhr.SIGN, DigestUtils.md5Hex(ts + appKey).toLowerCase());
		// 页数
		jsonObject.put(Constant.Joyhr.PAGE_SIZE, this.pageSize);
		jsonObject.put(Constant.Joyhr.PAGE_INDEX, this.pageIndex);
		this.isPass = true;
	}

	/**
	 * @Description 下一个要查的页
	 * @Author 郑勇浩
	 * @Data 2020/2/24 16:14
	 * @Param []
	 * @return void
	 */
	public void addPageIndex() {
		this.pageIndex++;
		this.jsonObject.put(Constant.Joyhr.PAGE_INDEX, this.pageIndex);
	}

}
