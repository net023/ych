package com.ych.web.model;

import java.util.Arrays;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "car")
public class CarModel extends Model<CarModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CarModel dao = new CarModel();

	public Page<CarModel> getPager(Pager pager) {
//		LinkedList<Object> param = new LinkedList<Object>();
		/*String bsql = SqlXmlKit.getSql("Car.pager");
		String sql = SqlXmlKit.getSql("Car.pager", pager.getParamsMap(), param);
		List<CarModel> data = dao.find("select * "+sql);
		bsql = bsql.replace("limit #$page$#,#$rows$#", "");
		bsql = "select count(*) "+bsql;
		bsql = SqlXmlKit.sqlHandler(bsql, pager.getParamsMap(),param);
		Long totalRow = Db.queryLong(bsql);
		int totalPage = 0;
	    totalPage = (int)(totalRow / pager.getPageSize());
		
		return new Page(data, pager.getPageNo(), pager.getPageSize(), totalPage, Integer.valueOf(totalRow.toString()));*/
		
		/*String bsql = SqlXmlKit.getSql("Car.count");
		String sql = SqlXmlKit.getSql("Car.pager", pager.getParamsMap(), param);
		List<CarModel> data = dao.find(sql);
		bsql = SqlXmlKit.sqlHandler(bsql, pager.getParamsMap(),param);
		Long totalRow = Db.queryLong(bsql, param.toArray());
		int totalPage = 0;
	    totalPage = (int)(totalRow / pager.getPageSize());
	    
		return new Page(data, pager.getPageNo(), pager.getPageSize(), totalPage, Integer.valueOf(totalRow.toString()));*/
		
		return Pager.pageHandler("Car.pager", "Car.count", pager.getParamsMap(), pager.getParamsMap(), this);
		
		/*String sql = SqlXmlKit.getSql("Car.pager1", pager.getParamsMap(), param);
		return dao.paginate(pager.getPageNo(), pager.getPageSize()," select * ", sql, param.toArray());*/
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from car where id in" + ids);
	}

	public boolean modify(Integer id, Integer status) {
		return Db.update("update car set status = ? where id = ?", status, id) == 1;
	}

}
