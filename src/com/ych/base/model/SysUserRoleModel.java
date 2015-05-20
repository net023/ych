package com.ych.base.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_user_role` (
 * `user_id` int(11) NOT NULL COMMENT '用户编号',
 * `role_id` int(11) NOT NULL COMMENT '角色编号',
 * PRIMARY KEY (`user_id`,`role_id`),
 * KEY `FK_role_user_key` (`role_id`) USING BTREE,
 * CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_roles` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
 * CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 4096 kB; (`role_id`) REFER `no_icon_app/sys_roles`(`role_id`) ON UP';
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_user_role")
public class SysUserRoleModel extends Model<SysUserRoleModel> {

	public static final SysUserRoleModel dao = new SysUserRoleModel();

	public int deleteByUser(Object uid) {
		return Db.update("delete from sys_user_role where userID = ? ", uid);
	}

	public void changeUserRole(SysUserRoleModel userRole, Boolean checked) {
		if (checked) {
			userRole.save();
		} else {
			Db.update("delete from sys_user_role where userID = ? and roleID = ? ", userRole.get("userID"), userRole.get("roleID"));
		}
	}

}
