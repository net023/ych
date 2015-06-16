package com.ych.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.DateTools;
import com.ych.tools.ModelTools;
import com.ych.web.model.OrderModel;
import com.ych.web.model.StoreModel;

@Control(controllerKey = "/order")
public class OrderController extends BaseController {

	private static final Logger LOG = Logger.getLogger(OrderController.class);

	public void index() {
		Calendar c = DateTools.getNowCalendar();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 7);
		setAttr("sDate", DateTools.formatDate(c.getTime()));
		setAttr("eDate" , DateTools.getDate(new Date()));
		setAttr("stores", ModelTools.putFirstDataRender("id", "name", "", "全部", StoreModel.dao.getAll()));
		render("order/order_index");
	}

	public void list() {
		Pager pager = createPager();
		pager.addParam("orderNum", getPara("orderNum"));
		pager.addParam("wxNickName", getPara("wxNickName"));
		pager.addParam("startDate", getPara("startDate"));
		pager.addParam("endDate", getPara("endDate"));
		pager.addParam("status", getParaToInt("status"));
		pager.addParam("store", getParaToInt("store"));
		Page<OrderModel> page = OrderModel.dao.page(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	public void modify() {
		Integer id = getParaToInt("id");
		Integer state = getParaToInt("state");
		boolean b = OrderModel.dao.modify(id, state);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	
	public void editDate(){
		Map<String, Object> result = getResultMap();
		try {
			//2015-06-17 23:59:00
			String dateStr = getPara("res_time");
			Date date = DateTools.parseDate(dateStr, DateTools.yyyy_MM_dd_HH_mm_ss);
			Integer uid = getParaToInt("id");
			boolean isUpdate = OrderModel.dao.findById(uid).set("res_time", date).update();
			if(isUpdate){
				result.put(RESULT, true);
				result.put(MESSAGE, "预约时间更新成功！");
			}else{
				result.put(RESULT, false);
				result.put(MESSAGE, "预约时间更新失败！");
			}
		} catch (Exception e) {
			LOG.debug("预约时间更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "预约时间更新失败！");
		}
		renderJson(result);
	}
	
	public static void main(String[] args) throws ParseException {
		String str = "2015-06-17 23:59:00";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date parse = sf.parse(str);
		System.out.println(parse);
	}
	
}
