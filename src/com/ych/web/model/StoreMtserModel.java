package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "store_mtser")
public class StoreMtserModel extends Model<StoreMtserModel> {
	
	private static final long serialVersionUID = 1L;
	public static final StoreMtserModel dao = new StoreMtserModel();

}
