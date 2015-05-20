package com.ych.base.controller;

import java.util.List;

import com.jfinal.kit.JsonKit;
import com.jfinal.log.Logger;
import com.ych.base.common.BaseController;
import com.ych.base.common.DisplayTool;
import com.ych.base.model.SysMenusModel;
import com.ych.base.model.SysRoleMenuModel;
import com.ych.base.model.SysRolesModel;
import com.ych.core.plugin.annotation.Control;

/**
 * 角色
 */
@Control(controllerKey = "/role")
public class RoleController extends BaseController {

	private static final Logger LOG = Logger.getLogger(RoleController.class);

	public void index() {
		List<SysRolesModel> role = SysRolesModel.dao.allRoles();
		List<SysMenusModel> menu = SysMenusModel.dao.allMenus();
		setAttr("rolesJson", JsonKit.toJson(role));
		setAttr("menusJson", JsonKit.toJson(menu));
		render("tissue/role_mgr");
	}

	public void list() {
		setAttr(DATA, SysRolesModel.dao.getRoleByParent(getUser().getInt("roleID")));
		setAttr(RESULT, true);
		renderJson();
	}

	public void add() {
		SysRolesModel menu = getModelWithOutModelName(SysRolesModel.class, true);
		if (menu.save()) {
			setAttr(MESSAGE, "新增成功！");
			setAttr("id", menu.get("id"));
			setAttr(RESULT, true);
			DisplayTool.removeCache(DisplayTool.ROLE_KEY);
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "新增失败！");
		}
		renderJson();
	}
	public void update() {
		SysRolesModel role = getModelWithOutModelName(SysRolesModel.class, true);
		if (role.get("id") != null && role.update()) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "更新成功！");
			DisplayTool.removeCache(DisplayTool.ROLE_KEY);
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "更新失败！");
		}
		renderJson();
	}
	public void del() {
		Integer id = getParaToInt("id");
		if (id != null && SysRolesModel.dao.deleteById(id)) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "删除成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "删除失败！");
		}
		renderJson();
	}

	public void roleMenu() {
		Integer id = getParaToInt("id");
		if (id != null) {
			setAttr(RESULT, true);
			setAttr(DATA, SysRoleMenuModel.dao.getRoleMenus(id));
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "参数错误，获取数据失败！");
		}
		renderJson();
	}

	public void menuChecked() {
		Boolean checked = getParaToBoolean("checked");
		SysRoleMenuModel roleMenu = getModelWithOutModelName(SysRoleMenuModel.class, true);
		if (checked != null && roleMenu.get("roleID") != null && roleMenu.get("menuID") != null) {
			setAttr(RESULT, true);
			SysRoleMenuModel.dao.changeRoleMenu(roleMenu, checked);
			setAttr(MESSAGE, "修改成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "参数错误，获取数据失败！");
		}
		renderJson();
	}

}
