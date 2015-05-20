package com.ych.base.interceptor;

import java.text.ParseException;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.EncryptionKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.ych.tools.DateTools;
import com.ych.tools.SysConstants;

/**
 * 权限拦截器规则:
 * 1.有后门字段bdKey直接通过
 * 2.上报无t和ak字段,不通过
 * 3.上报时间与服务器时间相差5分钟,不通过
 * 4.计算localKey与上传ak不同,不通过
 * 5.其他通过
 * 
 */
public class AuthenticationInterceptor implements Interceptor {

	private static final Logger LOG = Logger.getLogger(AuthenticationInterceptor.class);

	@Override
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController();
		String backDoorKey = controller.getPara("bdk"); // 后门字段 不验证
		if (SysConstants.AUTH_LOCAL_KEY.equals(backDoorKey)) {
			LOG.warn("后门请求[谨慎]:actionKey=" + ai.getActionKey() + ",backDoorKey=" + backDoorKey);
			ai.invoke();
		} else {
			String time = controller.getPara("t"); // yyyyMMddHHmmss
			String authKey = controller.getPara("ak"); // MD5加密后字符串
			boolean returnB = false;
			if (StrKit.notBlank(time, authKey)) {
				try {
					// 调用接口时间与当前时间相差60s,视为恶意请求
					if (Math.abs(System.currentTimeMillis() - DateTools.parseDate(time, DateTools.yyyyMMddHHmmss).getTime()) < SysConstants.AUTH_INTERVAL_TIME) {
						String localKey = EncryptionKit.md5Encrypt(time + SysConstants.AUTH_LOCAL_KEY);
						if (authKey.equals(localKey)) {
							returnB = true;
							ai.invoke();
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (!returnB) {
				LOG.warn("恶意请求[注意]:actionKey=" + ai.getActionKey() + ",time=" + time + ",authKey=" + authKey);
				controller.setAttr("isok", "false");
				controller.setAttr("msg", "访问权限不足");
				controller.renderJson();
			}
		}
	}

}
