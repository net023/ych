package com.ych.web.model;

import java.math.BigDecimal;
import java.util.Arrays;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "braking_product")
public class BrakingModel extends Model<BrakingModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final BrakingModel dao = new BrakingModel();

	public Page<BrakingModel> getPager(Pager pager) {
		return Pager.pageHandler("Braking.pager", "Braking.count", pager.getParamsMap(), pager.getParamsMap(), this);
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from braking_product where id in" + ids);
	}

	public boolean modify(Integer id, Integer status) {
		return Db.update("update braking_product set status = ? where id = ?", status, id) == 1;
	}

	public boolean recmd(Integer id, Integer status) {
		return Db.update("update braking_product set recmd = ? where id = ?", status, id) == 1;
	}
	
	public boolean updatePriceByName(BigDecimal price,String name){
		return Db.update("update braking_product set price=?,`status`=0,recmd=0 where name=?", price,name) >0;
	}

}
