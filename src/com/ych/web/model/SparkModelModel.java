package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "spark_model")
public class SparkModelModel extends Model<SparkModelModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final SparkModelModel dao = new SparkModelModel();
	
	public List<SparkModelModel> getAll(Integer sid){
		String sql = "select id,`name` from spark_model where s_id = ? and status =0";
		return this.find(sql,sid);
	}
	
	public Integer getOIDByNewID(Integer nid){
		String sql = "select o_id from spark_model where id=?";
		return Db.queryInt(sql, nid);
	}
	
	public SparkModelModel getModelByName(String name,Integer sid){
		String sql = "select * from spark_model where name=? and s_id=?";
		return this.findFirst(sql, name,sid);
	}

}
