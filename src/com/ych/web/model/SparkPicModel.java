package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "spark_pic")
public class SparkPicModel extends Model<SparkPicModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final SparkPicModel dao = new SparkPicModel();
	
	public SparkPicModel getByBidType(Integer bid){
		return this.findFirst("select * from spark_pic where b_id=?", bid);
	}
	
}
