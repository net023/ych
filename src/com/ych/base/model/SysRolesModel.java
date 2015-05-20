package com.ych.base.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_roles` (
 * `id` int(11) NOT NULL COMMENT '角色编号',
 * `roleName` varchar(30) NOT NULL COMMENT '角色名称',
 * `parentRole` int(11) NOT NULL DEFAULT '0',
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_roles")
public class SysRolesModel extends Model<SysRolesModel> {

	public static final SysRolesModel dao = new SysRolesModel();

	public List<SysRolesModel> allRoles() {
		return dao.find("select id, roleName, parentRole from sys_roles");
	}

	public List<SysRolesModel> getRoleByParent(Integer parentID) {
		return allRoles();//dao.find("SELECT role_id, role_name, parent_role FROM sys_roles r WHERE FIND_IN_SET( r.role_id, ?)",parentID);
	}
}
