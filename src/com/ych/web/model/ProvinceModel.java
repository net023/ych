package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "province")
public class ProvinceModel extends Model<ProvinceModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final ProvinceModel dao = new ProvinceModel();
	
	public List<ProvinceModel> getAll(){
		String sql = "select id,`name` from province order by sort";
		return this.find(sql);
	}

}
