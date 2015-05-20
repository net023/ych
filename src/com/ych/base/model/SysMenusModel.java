package com.ych.base.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

/**
 * CREATE TABLE `sys_menus` (
 * `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单编号',
 * `menuName` varchar(30) NOT NULL COMMENT '菜单名称',
 * `remark` varchar(30) DEFAULT NULL COMMENT '备注',
 * `isParent` tinyint(1) NOT NULL COMMENT '是否为父节点',
 * `parentID` int(11) NOT NULL COMMENT '父节点编号',
 * `url` varchar(200) DEFAULT NULL COMMENT 'URL',
 * `status` int(11) NOT NULL COMMENT '状态',
 * `sort` int(11) DEFAULT NULL COMMENT '排序',
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_menus")
public class SysMenusModel extends Model<SysMenusModel> {

	public static final SysMenusModel dao = new SysMenusModel();

	public List<SysMenusModel> allMenus() {
		return dao.find("SELECT IF(a.remark, CONCAT(a.menuName,'[',a.remark,']'), a.menuName) AS showName, a.* FROM sys_menus a ORDER BY sort ASC");
	}

	public List<Record> getMenusByUserID(Integer uid) {
		List<Record> list = Db.find(SqlXmlKit.getSql("SysMenus.listMenuByUserID"), uid);
		Stack<Record> stack = new Stack<Record>();
		List<Record> parents = new ArrayList<Record>();
		for (Record record : list) {
			if (record.getBoolean("isParent")) {
				stack.push(record);
				parents.add(record);
			} else {
				Record parent = null;
				while (parent == null && !stack.isEmpty()) {
					Record temp = stack.peek();
					if (record.get("parentID").equals(temp.get("id"))) {
						parent = temp;
					} else {
						stack.pop();
					}
				}
				if (parent != null) {
					List<Record> childrens = parent.get("childrens");
					if (childrens == null) {
						childrens = new ArrayList<Record>();
						parent.set("childrens", childrens);
					}
					childrens.add(record);
				} else {
					System.out.println("no parents found!" + record);
				}
			}
		}
		return parents;
	}
}
