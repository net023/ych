package com.ych.web.controller;

import java.util.Calendar;
import java.util.Date;

import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.DateTools;
import com.ych.tools.GEOTool;
import com.ych.web.model.WXUserModel;

@Control(controllerKey="/wx_user")
public class WXUserController extends BaseController {
	private static Logger LOG = Logger.getLogger(WXUserController.class);
	
	public void index(){
		Calendar c = DateTools.getNowCalendar();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 7);
		setAttr("sDate", DateTools.formatDate(c.getTime()));
		setAttr("eDate" , DateTools.getDate(new Date()));
		render("wx_user/wx_user_index");
	}
	
	public void list(){
		Pager pager = createPager();
		if (getPara("startDate") != null && !"".equals(getPara("startDate"))) {
			pager.addParam("startDate", getPara("startDate"));
		}
		if (getPara("endDate") != null && !"".equals(getPara("endDate"))) {
			pager.addParam("endDate", getPara("endDate"));
		}
		if (getPara("openID") != null && !"".equals(getPara("openID"))) {
			pager.addParam("openID", getPara("openID"));
		}
		Page<?> page = WXUserModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	
	public void parseJson(){
		String d = getPara("d");
		GEOTool.inToDB(d);
		renderJson("1");
	}
	
}
