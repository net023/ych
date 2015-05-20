package com.ych.base.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_user_post` (
 * `user_id` int(11) NOT NULL,
 * `post_id` int(11) NOT NULL,
 * PRIMARY KEY (`user_id`,`post_id`),
 * KEY `sys_user_post_ibfk_2` (`post_id`) USING BTREE,
 * CONSTRAINT `sys_user_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
 * CONSTRAINT `sys_user_post_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `sys_posts` (`post_id`) ON DELETE CASCADE ON UPDATE CASCADE
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_user_post")
public class SysUserPostModel extends Model<SysUserPostModel> {

	public static final SysUserPostModel dao = new SysUserPostModel();

	public int deleteByUser(Object uid) {
		return Db.update("delete from sys_user_post where userID = ? ", uid);
	}

}
