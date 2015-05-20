package com.ych.web.model;

import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "user")
public class WXUserModel extends Model<WXUserModel> {
	
	private static final long serialVersionUID = 1L;
	public static final WXUserModel dao = new WXUserModel();

	public Page<WXUserModel> getPager(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select * ",
				SqlXmlKit.getSql("WXUser.pager", pager.getParamsMap(), param),
				param.toArray());
	}
}
