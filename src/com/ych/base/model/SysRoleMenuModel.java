package com.ych.base.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_role_menu` (
 * `role_id` int(11) NOT NULL COMMENT '角色编号',
 * `menu_id` int(11) NOT NULL COMMENT '菜单编号',
 * PRIMARY KEY (`role_id`,`menu_id`),
 * KEY `FK_menu_role_key` (`menu_id`) USING BTREE,
 * CONSTRAINT `sys_role_menu_ibfk_1` FOREIGN KEY (`menu_id`) REFERENCES `sys_menus` (`menu_id`) ON DELETE CASCADE ON UPDATE CASCADE,
 * CONSTRAINT `sys_role_menu_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_roles` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 4096 kB; (`menu_id`) REFER `no_icon_app/sys_menus`(`menu_id`) ON UP';
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_role_menu")
public class SysRoleMenuModel extends Model<SysRoleMenuModel> {

	public static final SysRoleMenuModel dao = new SysRoleMenuModel();

	public void changeRoleMenu(SysRoleMenuModel roleMenu, Boolean checked) {
		if (checked) {
			roleMenu.save();
		} else {
			Db.update("delete from sys_role_menu where menuID = ? and roleID = ? ", roleMenu.get("menuID"), roleMenu.get("roleID"));
		}
	}

	public List<SysRoleMenuModel> getRoleMenus(Integer roleID) {
		return dao.find("select * from sys_role_menu where roleID = ?", roleID);
	}

}
