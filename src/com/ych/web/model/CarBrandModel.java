package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "car_brand")
public class CarBrandModel extends Model<CarBrandModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CarBrandModel dao = new CarBrandModel();

	public CarBrandModel findByName(String name){
		return this.findFirst("select * from car_brand where name=?", name);
	}
	
}
