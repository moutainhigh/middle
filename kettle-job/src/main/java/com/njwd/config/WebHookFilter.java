package com.njwd.config;

import com.alibaba.fastjson.JSON;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.KettleJobConstant;
import com.njwd.entity.kettlejob.CrmWebHookResult;
import com.njwd.entity.kettlejob.vo.CrmWebHookVo;
import com.njwd.exception.ResultCode;
import com.njwd.kettlejob.service.CrmWebHookService;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.SpringUtils;
import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description WebHookFilter验证拦截
 * @Date 2020/5/8 14:16
 * @Author 郑勇浩
 */
@WebFilter(filterName = "webHookFilter", urlPatterns = "/CrmWebHook/*")
public class WebHookFilter implements Filter {

	private final static Logger LOGGER = LoggerFactory.getLogger(WebHookFilter.class);

	CrmWebHookService crmWebHookService;

	/**
	 * @Description webHook 验证拦截
	 * @Author 郑勇浩
	 * @Data 2020/5/8 14:19
	 * @Param [request, response, chain]
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (crmWebHookService == null) {
			this.crmWebHookService = SpringUtils.getBean(CrmWebHookService.class);
		}

		HttpServletRequest req = (HttpServletRequest) request;
		response.setCharacterEncoding(CharEncoding.UTF_8);
		response.setContentType(KettleJobConstant.WebHook.CONTENT_TYPE);

		// head X-Hub-Signature MD5验证
		String token = req.getHeader(KettleJobConstant.WebHook.SIGNATURE);
		// 验证不通过
		if (token == null || !token.contains(Constant.Character.EQUALS)) {
			authoritativeFail(response);
			return;
		}
		String[] tokenList = token.split(Constant.Character.EQUALS);
		if (tokenList.length < Constant.Number.TWO || !tokenList[Constant.Number.ZERO].trim().equals(KettleJobConstant.WebHook.MD5)) {
			authoritativeFail(response);
			return;
		}
		// 对应秘钥是否存在
		Object redisObj = RedisUtils.getObj(KettleJobConstant.WebHook.webHookKey);
		List<CrmWebHookVo> secretList;
		if (redisObj == null) {
			secretList = crmWebHookService.getCrmWebHookVoList();
			RedisUtils.set(KettleJobConstant.WebHook.webHookKey, secretList);
		} else {
			secretList = (List<CrmWebHookVo>) redisObj;
		}

		CrmWebHookVo compareData = secretList.stream().filter(o -> o.getSecret().equals(tokenList[Constant.Number.ONE])).findFirst().orElse(null);
		if (compareData == null) {
			authoritativeFail(response);
			return;
		}
		req.setAttribute(KettleJobConstant.KettleJobParam.enteId, compareData.getEnteId());
		req.setAttribute(KettleJobConstant.KettleJobParam.appId, compareData.getAppId());
		chain.doFilter(req, response);
	}

	/**
	 * @Description 验证不通过返回信息
	 * @Author 郑勇浩
	 * @Data 2020/5/9 10:37
	 * @Param []
	 */
	private void authoritativeFail(ServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		CrmWebHookResult result = new CrmWebHookResult();
		result.setErrcode(ResultCode.UNAUTHORIZED.code);
		result.setErrmsg(ResultCode.UNAUTHORIZED.message);
		out.append(JSON.toJSONString(result));
	}

}
