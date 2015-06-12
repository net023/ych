package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "oil_pic")
public class OilPicModel extends Model<OilPicModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OilPicModel dao = new OilPicModel();
	
	public OilPicModel getByBidType(Integer bid,Integer type,Integer mid,Integer eid){
		return this.findFirst("select * from oil_pic where b_id=? and type=? and m_id=? and e_id=?", bid,type,mid,eid);
	}
	
}
