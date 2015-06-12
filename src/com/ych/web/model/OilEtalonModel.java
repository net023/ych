package com.ych.web.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "oil_etalon")
public class OilEtalonModel extends Model<OilEtalonModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OilEtalonModel dao = new OilEtalonModel();
	
	public List<OilEtalonModel> getAll(){
		String sql = "select id,`name` from oil_etalon where status =0";
		return this.find(sql);
	}
	
	public Integer getOIDByNewID(Integer nid){
		String sql = "select o_id from oil_etalon where id=?";
		return Db.queryInt(sql, nid);
	}
	
	public OilEtalonModel getModelByName(String name){
		String sql = "select * from oil_etalon where name=?";
		return this.findFirst(sql, name);
	}
	
	public List<OilEtalonModel> getMOdelByBidMid(Integer bid,Integer mid){
		String sql = SqlXmlKit.getSql("OilEtalon.getEtalon");
		return this.find(sql, bid,mid);
	}

}
