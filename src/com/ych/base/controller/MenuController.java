package com.ych.base.controller;

import java.util.List;

import com.jfinal.kit.JsonKit;
import com.jfinal.log.Logger;
import com.ych.base.common.BaseController;
import com.ych.base.model.SysMenusModel;
import com.ych.core.plugin.annotation.Control;

/**
 * 菜单
 */
@Control(controllerKey = "/menu")
public class MenuController extends BaseController {

	private static final Logger LOG = Logger.getLogger(MenuController.class);

	public void index() {
		List<SysMenusModel> menus = SysMenusModel.dao.allMenus();
		setAttr("menusJson", JsonKit.toJson(menus));
		render("tissue/menu_mgr");
	}
	public void add() {
		SysMenusModel menu = getModelWithOutModelName(SysMenusModel.class, true);
		if (menu.save()) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "新增成功！");
			setAttr("id", menu.get("id"));
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "新增失败！");
		}
		renderJson();
	}
	public void update() {
		SysMenusModel menu = getModelWithOutModelName(SysMenusModel.class, true);
		if (menu.get("id") != null && menu.update()) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "更新成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "更新失败！");
		}
		renderJson();
	}
	public void del() {
		Integer id = getParaToInt("id");
		if (id != null && SysMenusModel.dao.deleteById(id)) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "删除成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "删除失败！");
		}
		renderJson();
	}

}
