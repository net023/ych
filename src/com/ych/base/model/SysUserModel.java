package com.ych.base.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.EncryptionKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_user` (
 * `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
 * `nick` varchar(30) DEFAULT NULL COMMENT '昵称',
 * `username` varchar(30) NOT NULL COMMENT '用户名',
 * `password` varchar(32) NOT NULL COMMENT '密码',
 * `email` varchar(100) DEFAULT NULL COMMENT 'Email',
 * `qq` varchar(20) DEFAULT NULL COMMENT 'QQ',
 * `phone` varchar(20) DEFAULT NULL COMMENT '电话',
 * `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态 0-停用 1-正常',
 * `updateDate` datetime DEFAULT NULL COMMENT '修改时间',
 * `createDate` datetime NOT NULL COMMENT '创建时间',
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_user")
public class SysUserModel extends Model<SysUserModel> {

	public static final SysUserModel dao = new SysUserModel();

	public SysUserModel login(String username, String password) {
		return dao.findFirst("select * from sys_user where status=1 and username=? and password=?", username, EncryptionKit.md5Encrypt(password));
	}
	public List<SysUserModel> getAllUserName() {
		return dao.find("select id,nick from sys_user");
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public Page<SysUserModel> page(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		Page<SysUserModel> page = dao.paginate(pager.getPageNo(), pager.getPageSize(), " select *,(select GROUP_CONCAT(DISTINCT(roleID)) from sys_user_role ur where u.id = ur.userID) roles ",
				SqlXmlKit.getSql("SysUser.pager", (Map) pager.getParamsMap(), param), param.toArray());
		return page;
	}
}
