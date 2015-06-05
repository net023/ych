package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "filter_brand")
public class FilterBrandModel extends Model<FilterBrandModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final FilterBrandModel dao = new FilterBrandModel();
	
	public List<FilterBrandModel> getAll() {
		String sql = "select `id`,`name` from filter_brand where status = 0";
		return this.find(sql);
	}

}
