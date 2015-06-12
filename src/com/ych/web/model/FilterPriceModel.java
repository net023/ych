package com.ych.web.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "filter_price")
public class FilterPriceModel extends Model<FilterPriceModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final FilterPriceModel dao = new FilterPriceModel();
	public Page<FilterPriceModel> getPager(Pager pager) {
		/*LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ",
				SqlXmlKit.getSql("FilterPrice.pager", pager.getParamsMap(), param),
				param.toArray());*/
		return Pager.pageHandler("FilterPrice.pager", "FilterPrice.count", pager.getParamsMap(), pager.getParamsMap(), this);
	}
	
	public boolean modify(Integer id, Integer status) {
		return Db.update("update filter_price set status = ? where id = ?", status, id) == 1;
	}

	public boolean recmd(Integer id, Integer status) {
		return Db.update("update filter_price set recmd = ? where id = ?", status, id) == 1;
	}
	
	public FilterPriceModel getModelByBrandTypeNum(Integer brand,Integer type,String num){
		return this.findFirst("select * from filter_price where b_id=? and t_id=? and num=?", brand,type,num);
	}
	
	
}
