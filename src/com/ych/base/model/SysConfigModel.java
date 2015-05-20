package com.ych.base.model;

import java.util.LinkedList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Table;
import com.ych.tools.EhcacheConstants;

/**
 * 
 * CREATE TABLE `sys_config` (
 * `id` int(11) NOT NULL AUTO_INCREMENT,
 * `key` varchar(100) DEFAULT NULL COMMENT 'key',
 * `field` varchar(100) DEFAULT NULL COMMENT '字段',
 * `val` varchar(100) DEFAULT NULL COMMENT '值',
 * `remark` varchar(255) DEFAULT NULL COMMENT '备注',
 * `sort` int(11) DEFAULT NULL COMMENT '排序号',
 * `state` int(11) DEFAULT NULL COMMENT '状态（0，停用，1，正常）',
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典表'
 * 
 * @author lx
 *
 */

@SuppressWarnings("serial")
@Table(tableName = "sys_config")
public class SysConfigModel extends Model<SysConfigModel> {

	public static final SysConfigModel dao = new SysConfigModel();

	/**
	 * 获取字典表的列表数据
	 * 
	 * @return
	 */
	public Page<SysConfigModel> pageList(Pager pager) {
		StringBuffer sqlExecpt = new StringBuffer(" from sys_config where 1=1 ");
		LinkedList<Object> params = new LinkedList<Object>();
		if (pager.getParamsMap().get("key") != null && !"".equals(pager.getParamsMap().get("key"))) {
			sqlExecpt.append(" and `key` like CONCAT('%',?,'%') ");
			params.add(pager.getParamsMap().get("id"));
		}
		if (pager.getParamsMap().get("state") != null && !"".equals(pager.getParamsMap().get("state"))) {
			sqlExecpt.append(" and state = ? ");
			params.add(pager.getParamsMap().get("state"));
		}
		sqlExecpt.append("  order by `key` asc,sort asc ");
		return dao.paginate(pager.getPageNo(), pager.getPageSize(), " SELECT id, `key` , `field`, val,remark,state,sort ", sqlExecpt.toString(), params.toArray());
	}

	/**
	 * 删除指定ID的字典数据
	 * 
	 * @param ids
	 * @return
	 */
	public int batchDel(String ids) {
		int count = 0;
		String[] idsArr = ids.split("\\|");
		for (String id : idsArr) {
			if ("".equals(id))
				continue;
			Db.update(" delete from sys_config where 1=1  and id=?  ", id);
			count++;
		}
		return count;
	}

	/**
	 * 根据指定key获取config
	 * 
	 * @param key
	 * @return
	 */
	public List<SysConfigModel> getConfigs(String key) {
		return dao.findByCache(EhcacheConstants.SYS_CONFIG, "KEY_" + key, " SELECT id, `key`, `field`, val,remark,state,sort from sys_config where state=1 and `key`=? order by sort ", key);
	}

	/**
	 * 根据指定key和val获取config
	 * 
	 * @param key
	 * @return
	 */
	public SysConfigModel getConfigs(String key, String val) {
		List<SysConfigModel> configs = getConfigs(key);
		for (SysConfigModel config : configs) {
			if (val != null && val.equals(config.get("val"))) {
				return config;
			}
		}
		return null;
	}

	/**
	 * 获取单个配置值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public SysConfigModel getConfig(String key, String field) {
		List<SysConfigModel> SysConfigModelList = dao.findByCache(EhcacheConstants.SYS_CONFIG, key + "_" + field, "select `key`,field,val from sys_config where `key`=? and field=? and state=1", key,
				field);
		if (null != SysConfigModelList && SysConfigModelList.size() > 0) {
			return SysConfigModelList.get(0);
		}
		return null;
	}

	public String getConfigVal(String key, String field) {
		SysConfigModel configModel = getConfig(key, field);
		return (configModel == null) ? "" : configModel.getStr("val");
	}
}
