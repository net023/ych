package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "braking_type")
public class BrakingTypeModel extends Model<BrakingTypeModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final BrakingTypeModel dao = new BrakingTypeModel();
	
	public List<BrakingTypeModel> getAll(){
		String sql = "select id,`name` from braking_type where status =0";
		return this.find(sql);
	}
	
	public Integer getOIDByNewID(Integer nid){
		String sql = "select o_id from braking_type where id=?";
		return Db.queryInt(sql, nid);
	}
	
	public BrakingTypeModel getModelByName(String name,Integer sid){
		String sql = "select * from braking_type where name=? and s_id=?";
		return this.findFirst(sql, name,sid);
	}

}
