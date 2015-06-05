package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "car_manf")
public class CarManfModel extends Model<CarManfModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CarManfModel dao = new CarManfModel();
	
	public CarManfModel findByName(String name){
		return this.findFirst("select * from car_manf where name=?", name);
	}
	
}
