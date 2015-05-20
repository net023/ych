package com.ych.web.model;

import java.util.LinkedList;
import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "mtser")
public class MtSerModel extends Model<MtSerModel> {
	
	private static final long serialVersionUID = 1L;
	public static final MtSerModel dao = new MtSerModel();

	public Page<MtSerModel> getPager(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ",
				SqlXmlKit.getSql("WXUser.pager", pager.getParamsMap(), param),
				param.toArray());
	}
	
	
	public List<MtSerModel> getAll(){
		String sql = "select `id`,`name` from mtser";
		return this.find(sql);
	}
	
}
