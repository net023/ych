package com.ych.web.model;

import java.util.Arrays;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.Pager;
import com.ych.core.kit.SqlXmlKit;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "news")
public class NewsModel extends Model<NewsModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7121422951808579639L;
	public static final NewsModel dao = new NewsModel();

	public Page<NewsModel> page(Pager pager) {
		LinkedList<Object> param = new LinkedList<Object>();
		return dao
				.paginate(pager.getPageNo(), pager.getPageSize(),
						" select *  ", SqlXmlKit.getSql("News.pager",
								pager.getParamsMap(), param), param.toArray());
	}
	
	public int delByType(String ids){
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from news where n_type in" + ids);
	}

	public int batchDel(String ids) {
		ids = Arrays.toString(ids.split("\\|"));
		ids = ids.replace("[", "(");
		ids = ids.replace("]", ")");
		return Db.update("delete from news where id in" + ids);
	}

	public int batchAdd(String[]... strings) {
		StringBuilder sb = new StringBuilder(
				"insert into news(id, desc, answer, type, state) values");
		for (int j = 0; j < strings[0].length; j++) {
			sb.append("(");
			for (int i = 0; i < strings.length; i++) {
				if (i == strings.length - 1) {
					sb.append(strings[i][j]);
				} else {
					sb.append(strings[i][j] + ",");
				}
			}
			if (j == strings[0].length - 1) {
				sb.append(")");
			} else {
				sb.append("),");
			}
		}
		return Db.update(sb.toString());
	}
	
	public boolean modify(Integer id,Integer status){
		return Db.update("update news set status = ? where id = ?",status,id)==1;
	}

}
