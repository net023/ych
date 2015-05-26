package com.ych.web.model;

import java.util.Arrays;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "carousel")
public class CarouselModel extends Model<CarouselModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final CarouselModel dao = new CarouselModel();

	public Page<CarouselModel> page(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao
				.paginate(pager.getPageNo(), pager.getPageSize(),
						" select *  ", SqlXmlKit.getSql("Carousel.pager",
								pager.getParamsMap(), param), param.toArray());
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from carousel where id in" + ids);
	}

	public boolean modify(Integer id, Integer status) {
		return Db
				.update("update carousel set status = ? where id = ?", status, id) == 1;
	}

}
