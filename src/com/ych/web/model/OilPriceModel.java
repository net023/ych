package com.ych.web.model;

import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "oil_price")
public class OilPriceModel extends Model<OilPriceModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OilPriceModel dao = new OilPriceModel();
	public Page<OilPriceModel> getPager(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ",
				SqlXmlKit.getSql("OilPrice.pager", pager.getParamsMap(), param),
				param.toArray());
	}
	
	public boolean modify(Integer id, Integer status) {
		return Db.update("update oil_price set status = ? where id = ?", status, id) == 1;
	}

	public boolean recmd(Integer id, Integer status) {
		return Db.update("update oil_price set recmd = ? where id = ?", status, id) == 1;
	}
	
	public OilPriceModel getModelByBMETL(Integer brand,Integer model,Integer etalon,Integer type,Integer litre){
		return this.findFirst("select * from oil_price where b_id=? and m_id=? and e_id=? and type=? and litre=?", brand,model,etalon,type,litre);
	}
	
}
