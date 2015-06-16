package com.ych.web.model;

import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "order")
public class OrderModel extends Model<OrderModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OrderModel dao = new OrderModel();


	/**
	 * 获取新增待审核的订单数量
	 * @return 
	 */
	public Long getNewCountOrder(){
		return Db.queryLong("select count(*) from `order` where `status`=0");
	}
	
	
	public Page<OrderModel> page(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao
				.paginate(pager.getPageNo(), pager.getPageSize(),
						" select *  ", SqlXmlKit.getSql("Order.pager",
								pager.getParamsMap(), param), param.toArray());
	}
	
	
	public boolean modify(Integer id, Integer status) {
		return Db
				.update("update `order` set status = ? where id = ?", status, id) == 1;
	}


}
