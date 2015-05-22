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

@Table(tableName = "mtser_brand_filter")
public class MtserBrandModel extends Model<MtserBrandModel> {

	private static final long serialVersionUID = 1L;
	public static final MtserBrandModel dao = new MtserBrandModel();

	public Page<MtserBrandModel> getPager(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ", SqlXmlKit.getSql("MtserBrand.pager",
						pager.getParamsMap(), param), param.toArray());
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from mtser_brand_filter where ms_id in" + ids);
	}

	public void batchAdd(Integer msId, String filter) {
		String[] strs = filter.split(",");
		for (String str : strs) {
			Db.update(
					"insert into mtser_brand_filter(ms_id, brand_filter) values(?, ?)",
					msId, str);
		}
	}

	public void batchUpdate(Integer msId, String filter) {
		Db.update("delete from mtser_brand_filter where ms_id = ?", msId);
		batchAdd(msId, filter);
	}

	public boolean hasFilter(Integer msId, String filter) {
		String[] strs = filter.split(",");
		List<MtserBrandModel> records = null;
		for (String str : strs) {
			records = this.find(
					"select * from mtser_brand_filter where brand_filter = ? and ms_id = ?",
					str, msId);
			if (!records.isEmpty())
				return true;
		}
		return false;
	}
}
