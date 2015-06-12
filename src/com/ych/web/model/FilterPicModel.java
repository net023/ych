package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "filter_pic")
public class FilterPicModel extends Model<FilterPicModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final FilterPicModel dao = new FilterPicModel();
	
	public FilterPicModel getByBidTid(Integer bid,Integer tid){
		return this.findFirst("select * from filter_pic where b_id=? and t_id=?", bid,tid);
	}
	
}
