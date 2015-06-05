package com.ych.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("all")
public class ModelTools {
	public static String putFirstDataRender(String k1,String k2,String v1,String v2,List<? extends Model> data){
		try {
			List temp = new ArrayList(data);
			if(temp.size()==0){
				Map first = new HashMap();
				first.put(k1, v1);first.put(k2, v2);
				temp.add(first);
			}else{
				Model first = (Model) temp.get(0).getClass().newInstance();
				first.set(k1, v1).set(k2, v2);
				temp.add(0, first);
			}
			return JsonKit.toJson(temp).replace("\"", "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List putFirstDataRenderJson(String k1,String k2,String v1,String v2,List<? extends Model> data){
		try {
			List temp = new ArrayList(data);
			if(temp.size()==0){
				Map first = new HashMap();
				first.put(k1, v1);first.put(k2, v2);
				temp.add(first);
			}else{
				Model first = (Model) temp.get(0).getClass().newInstance();
				first.set(k1, v1).set(k2, v2);
				temp.add(0, first);
			}
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
