package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "oil_brand")
public class OilBrandModel extends Model<OilBrandModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OilBrandModel dao = new OilBrandModel();

	public OilBrandModel findByName(String name){
		return this.findFirst("select * from oil_brand where name=?", name);
	}
	
	public List<OilBrandModel> getAll() {
		String sql = "select `id`,`name` from oil_brand where status = 0";
		return this.find(sql);
	}
	
}
