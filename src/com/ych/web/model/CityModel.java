package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "city")
public class CityModel extends Model<CityModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CityModel dao = new CityModel();
	
	public List<CityModel> getAll(Integer pid){
		String sql = "select id,`name` from city where p_id = ? order by sort";
		return this.find(sql,pid);
	}

}
