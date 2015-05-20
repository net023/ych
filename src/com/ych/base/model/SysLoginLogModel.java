package com.ych.base.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_login_log` (
 * `id` int(11) NOT NULL AUTO_INCREMENT,
 * `username` varchar(50) NOT NULL,
 * `loginIP` varchar(50) NOT NULL,
 * `loginDate` datetime NOT NULL,
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=579 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_login_log")
public class SysLoginLogModel extends Model<SysLoginLogModel> {

	public static final SysLoginLogModel dao = new SysLoginLogModel();

}
