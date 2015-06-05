package com.ych.web.model;

import java.util.Arrays;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "filter_product")
public class FilterModel extends Model<FilterModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final FilterModel dao = new FilterModel();

	public Page<FilterModel> getPager(Pager pager) {
		/*LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ",
				SqlXmlKit.getSql("Filter.pager", pager.getParamsMap(), param),
				param.toArray());*/
		return Pager.pageHandler("Filter.pager", "Filter.count", pager.getParamsMap(), pager.getParamsMap(), this);
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from filter_product where id in" + ids);
	}

	public boolean modify(Integer id, Integer status) {
		return Db.update("update filter_product set status = ? where id = ?", status, id) == 1;
	}

	public boolean recmd(Integer id, Integer status) {
		return Db.update("update filter_product set recmd = ? where id = ?", status, id) == 1;
	}

}
