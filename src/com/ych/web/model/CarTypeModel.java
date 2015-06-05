package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "car_type")
public class CarTypeModel extends Model<CarTypeModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CarTypeModel dao = new CarTypeModel();
	
	public CarTypeModel findByName(String name){
		return this.findFirst("select * from car_type where name=?", name);
	}
}
