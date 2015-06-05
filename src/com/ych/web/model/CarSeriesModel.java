package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "car_series")
public class CarSeriesModel extends Model<CarSeriesModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CarSeriesModel dao = new CarSeriesModel();

	public CarSeriesModel findByName(String name){
		return this.findFirst("select * from car_series where name=?", name);
	}
}
