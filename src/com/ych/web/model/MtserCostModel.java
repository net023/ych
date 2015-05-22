package com.ych.web.model;

import java.util.Arrays;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "mtser_unit_cost")
public class MtserCostModel extends Model<MtserCostModel> {

	private static final long serialVersionUID = 1L;
	public static final MtserCostModel dao = new MtserCostModel();

	public Page<MtserCostModel> getPager(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ", SqlXmlKit.getSql("MtserCost.pager",
						pager.getParamsMap(), param), param.toArray());
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from mtser_unit_cost where ms_id in" + ids);
	}

	public MtserCostModel getByMsId(Integer msId) {
		return dao
				.findFirst(
						"select `id`, ms_id, unit_cost from mtser_unit_cost where ms_id = ?",
						msId);
	}
}
