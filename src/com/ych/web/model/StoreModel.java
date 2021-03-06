package com.ych.web.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "store")
public class StoreModel extends Model<StoreModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final StoreModel dao = new StoreModel();

	public Page<StoreModel> getPager(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ",
				SqlXmlKit.getSql("Store.pager", pager.getParamsMap(), param),
				param.toArray());
	}
	
	
	public int batchDel(String ids){
		 ids = Arrays.toString(ids.split("\\|"));
		 ids = ids.replace("[", "(");
		 ids  = ids.replace("]", ")");
		 return Db.update("delete from store where id in"+ids);
	 }
	
	public boolean modify(Integer id,Integer status){
		return Db.update("update store set status = ? where id = ?",status,id)==1;
	}
	
	public List<StoreModel> getAll(){
		String sql = "select id,`name` from store where status =0";
		return this.find(sql);
	}
	
	public List<StoreModel> getAll2(){
		String sql = "select id,`name`,lon,lat,concat(city,county,address) 'ads' from store where status =0";
		return this.find(sql);
	}
	
	
}
