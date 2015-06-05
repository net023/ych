package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "spark_series")
public class SparkSeriesModel extends Model<SparkSeriesModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final SparkSeriesModel dao = new SparkSeriesModel();
	
	public List<SparkSeriesModel> getAll(Integer bid){
		String sql = "select id,`name` from spark_series where b_id = ? and status =0";
		return this.find(sql,bid);
	}

}
