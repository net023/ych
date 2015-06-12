package com.ych.web.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "oil_product")
public class OilModel extends Model<OilModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OilModel dao = new OilModel();

	public Page<OilModel> getPager(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ",
				SqlXmlKit.getSql("Oil.pager", pager.getParamsMap(), param),
				param.toArray());
		//return Pager.pageHandler("Oil.pager", "Oil.count", pager.getParamsMap(), pager.getParamsMap(), this);
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from oil_product where id in" + ids);
	}

	public boolean updatePriceByName(BigDecimal price,String name){
		return Db.update("update oil_product set price=?,`status`=0,recmd=0 where name=?", price,name) >0;
	}

}
