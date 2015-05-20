package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "store_pic")
public class StorePicModel extends Model<StorePicModel> {
	
	private static final long serialVersionUID = 1L;
	public static final StorePicModel dao = new StorePicModel();
	
	public void delBySid(Integer sid){
		String sql = "delete from store_pic where s_id = ?";
		Db.update(sql, sid);
	}
	
	public List<StorePicModel> getBySid(Integer sid){
		String sql = "select * from store_pic where s_id = ?";
		return this.find(sql, sid);
	}

}
