package com.ych.base.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.ych.base.common.BaseController;
import com.ych.base.model.SysLoginLogModel;
import com.ych.base.model.SysMenusModel;
import com.ych.base.model.SysUserModel;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.SysConstants;

/**
 * 无图标app
 */
@Control(controllerKey = {"/login", "/"})
public class LoginController extends BaseController {

	private static final Logger LOG = Logger.getLogger(LoginController.class);

	public void index() {
		render("login");
	}

	public void loginCheck() {
		Map<String, Object> result = getResultMap();
		String username = getPara("username");
		String password = getPara("password");
		if (StrKit.isBlank(username) || StrKit.isBlank(password)) {
			result.put(ERROR, "用户名密码错误");
			renderJson(result);
		}
		try {
			SysUserModel user = SysUserModel.dao.login(username, password);
			if (null != user) {
				result.put(RESULT, true);
				user.put("menus", SysMenusModel.dao.getMenusByUserID(user.getInt("id")));
				getSession().setAttribute(SysConstants.SESSION_USER, user);
				new SysLoginLogModel().set("userID", user.get("id")).set("username", user.get("username")).set("loginIP", getRealIpAddr(getRequest())).set("loginDate", new Date()).save();
			}
		} catch (Exception e) {
			result.put(ERROR, "登录失败");
			LOG.error("登录失败", e);
		}
		renderJson(result);
	}

	public void logout() {
		getSession().removeAttribute(SysConstants.SESSION_USER);
		redirect("/");
	}

	public void main() {
		SysUserModel user = getUser();
		if(user==null){
			render("login");
		}else{
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			setAttr("sysDate", format.format(Calendar.getInstance().getTime()));
			setAttr("menus", user.get("menus"));
			setAttr("nick", user.get("nick"));
			render("main");
		}
	}
}
