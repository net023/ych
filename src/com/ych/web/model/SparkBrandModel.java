package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "spark_brand")
public class SparkBrandModel extends Model<SparkBrandModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final SparkBrandModel dao = new SparkBrandModel();
	
	public List<SparkBrandModel> getAll() {
		String sql = "select `id`,`name` from spark_brand where status = 0";
		return this.find(sql);
	}

}
