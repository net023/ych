package com.ych.base.controller;

import java.util.Map;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.base.model.SysConfigModel;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.EhcacheConstants;

@Control(controllerKey = "config")
public class ConfigController extends BaseController {

	public void index() {
		render("tissue/sys_config");
	}

	/**
	 * 数据字典列表数据
	 */
	public void list() {
		Pager pager = createPager();
		pager.addParam("key", getPara("key"));
		pager.addParam("state", getPara("state"));

		Page<SysConfigModel> page = SysConfigModel.dao.pageList(pager);
		setAttr("rows", page.getList());
		setAttr("total", page.getTotalRow());
		renderJson();
	}

	/**
	 * 添加
	 */
	public void add() {
		Map<String, Object> result = getResultMap();
		SysConfigModel config = getModelWithOutModelName(SysConfigModel.class, true);
		try {
			config.save();
			CacheKit.removeAll(EhcacheConstants.SYS_CONFIG);
			result.put(RESULT, true);
			result.put(MESSAGE, "添加字典成功！");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(RESULT, false);
			result.put(MESSAGE, "添加字典失败！");
		}
		renderJson(result);
	}

	/**
	 * 修改
	 */
	public void update() {
		Map<String, Object> result = getResultMap();
		SysConfigModel config = getModelWithOutModelName(SysConfigModel.class, true);
		try {
			config.update();
			result.put(RESULT, true);
			CacheKit.remove(EhcacheConstants.SYS_CONFIG, "KEY_" + config.get("key"));
			result.put(MESSAGE, "修改字典成功！");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(RESULT, false);
			result.put(MESSAGE, "修改字典失败！");
		}
		renderJson(result);
	}

	/**
	 * 删除
	 */
	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			SysConfigModel.dao.batchDel(getPara("ids"));
			CacheKit.removeAll(EhcacheConstants.SYS_CONFIG);
			result.put(RESULT, true);
			result.put(MESSAGE, "版本删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(RESULT, false);
			result.put(MESSAGE, "版本删除失败！");
		}
		renderJson(result);
	}

}
