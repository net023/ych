package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "county")
public class CountyModel extends Model<CountyModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CountyModel dao = new CountyModel();
	
	public List<CountyModel> getAll(Integer cid){
		String sql = "select id,`name` from county where c_id = ? order by sort";
		return this.find(sql,cid);
	}

}
