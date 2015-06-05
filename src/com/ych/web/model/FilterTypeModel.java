package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "filter_type")
public class FilterTypeModel extends Model<FilterTypeModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final FilterTypeModel dao = new FilterTypeModel();
	
	public List<FilterTypeModel> getAll(){
		String sql = "select id,`name` from filter_type where status =0";
		return this.find(sql);
	}
	
	public Integer getOIDByNewID(Integer nid){
		String sql = "select o_id from filter_type where id=?";
		return Db.queryInt(sql, nid);
	}
	
	public FilterTypeModel getModelByName(String name,Integer sid){
		String sql = "select * from filter_type where name=? and s_id=?";
		return this.findFirst(sql, name,sid);
	}

}
