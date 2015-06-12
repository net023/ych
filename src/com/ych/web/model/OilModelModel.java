package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "oil_model")
public class OilModelModel extends Model<OilModelModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OilModelModel dao = new OilModelModel();

	public OilModelModel findByName(String name){
		return this.findFirst("select * from oil_brand where name=?", name);
	}
	
	public List<OilModelModel> getAllByBid(Integer bid) {
		String sql = "select `id`,`name` from oil_model where b_id=? and status = 0";
		return this.find(sql,bid);
	}
	
	public List<OilModelModel> getAll() {
		String sql = "select `id`,`name` from oil_model where status = 0";
		return this.find(sql);
	}
	
}
