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

@Table(tableName = "news_type")
public class NewsClassModel extends Model<NewsClassModel>{
	  
	 /**
	 * 
	 */
	private static final long serialVersionUID = -7588560165246012421L;
	public static final NewsClassModel dao = new NewsClassModel();
	  
	 public Page<NewsClassModel> page(Pager pager) {
		 LinkedList<Object> param = new LinkedList<Object>();
		 return dao.paginate(pager.getPageNo(), pager.getPageSize(), " select *  ", SqlXmlKit.getSql("NewsClass.pager", pager.getParamsMap(), param), param.toArray());
	 }
	 
	 public List<NewsClassModel> getAll(){
		 String sql = "select * from news_type";
		 return this.find(sql);
	 }
	  
	 public int batchDel(String ids){
		 ids = Arrays.toString(ids.split("\\|"));
		 ids = ids.replace("[", "(");
		 ids  = ids.replace("]", ")");
		 return Db.update("delete from news_type where id in"+ids);
	 }
	  
	 public int batchAdd(String[] ...strings){
		 StringBuilder sb = new StringBuilder("insert into news_type(id, t_name) values");
		 for (int j = 0; j < strings[0].length; j++) {
			 sb.append("(");
			 for (int i = 0; i < strings.length; i++) {
				 if(i==strings.length-1){
					 sb.append(strings[i][j]);
				 }else{
					 sb.append(strings[i][j]+",");
				 }
			 }
			 if(j==strings[0].length-1){
				 sb.append(")");
			 }else{
				 sb.append("),");
			 }
		 }
		 return Db.update(sb.toString());
	 }
	  
}
