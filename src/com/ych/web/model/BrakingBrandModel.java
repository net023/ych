package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "braking_brand")
public class BrakingBrandModel extends Model<BrakingBrandModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final BrakingBrandModel dao = new BrakingBrandModel();
	
	public List<BrakingBrandModel> getAll() {
		String sql = "select `id`,`name` from braking_brand where status = 0";
		return this.find(sql);
	}

}
