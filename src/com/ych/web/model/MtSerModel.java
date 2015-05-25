package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "mtser")
public class MtSerModel extends Model<MtSerModel> {

	private static final long serialVersionUID = 1L;
	public static final MtSerModel dao = new MtSerModel();

	public List<MtSerModel> getAll() {
		String sql = "select `id`,`name` from mtser";
		return this.find(sql);
	}

	public List<MtSerModel> getAllTreeItems() {
		String sql = "select id, if (`name`, `name`, `name`) as showName, `name`, if (p_id, 0, 1) as isParent, if (p_id, p_id, 0) as parentID from mtser";
		return this.find(sql);
	}

	public List<MtSerModel> getAllFirsts() {
		String sql = "select `id`, `name` from mtser where p_id is null";
		return this.find(sql);
	}

	public List<MtSerModel> getAllSecondCategoriesByParent(Integer pid) {
		String sql = "select `id`, `name` from mtser where p_id = ?";
		return this.find(sql, pid);
	}

	public boolean deleteByIdCascade(Integer id) {
		String sql = "delete from mtser where id = ? or p_id = ?";
		return Db.update(sql, id, id) > 0;
	}

}
