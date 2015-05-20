package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "store_pic")
public class StorePicModel extends Model<StorePicModel> {
	
	private static final long serialVersionUID = 1L;
	public static final StorePicModel dao = new StorePicModel();

}
