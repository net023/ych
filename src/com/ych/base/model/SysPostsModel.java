package com.ych.base.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_posts` (
 * `id` int(11) NOT NULL COMMENT '权限编号',
 * `postName` varchar(30) NOT NULL COMMENT '权限名称',
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_posts")
public class SysPostsModel extends Model<SysPostsModel> {

	public static final SysPostsModel dao = new SysPostsModel();

	public List<SysPostsModel> allPosts() {
		return dao.find("select id,postName from sys_posts");
	}

}
