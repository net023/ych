package com.ych.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.ych.web.model.CityModel;
import com.ych.web.model.ProvinceModel;

public class GEOTool {
//	private static String provinceSql = "insert into province(`name`,`sort`) values(?,?)";
//	private static String citySql = "insert into city(`name`,`sort`,p_id) values(?,?,?)";
	private static String countrySql = "insert into county(`name`,`sort`,c_id) values(?,?,?)";
	public static void inToDB(String json){
		//{"Items":{"0":[],"0_1":[],"0_0_1":[]}}
		JSONObject jsonObject = JSONObject.parseObject(json);
		JSONObject items = jsonObject.getJSONObject("Items");
		Object[] provinces = items.getJSONArray("0").toArray();
		for (int i = 0; i < provinces.length; i++) {
			//插入省份
//			Db.update(provinceSql, provinces[i],i);
			ProvinceModel province = new ProvinceModel();
			province.set("name", provinces[i]).set("sort", i).save();
			//插入城市
			JSONArray n1 = items.getJSONArray("0_"+i);
			if(null==n1) continue;
			Object[] cities = n1.toArray();
			for (int j = 0; j < cities.length; j++) {
//				Db.update(citySql, cities[j],j,i);
				CityModel city = new CityModel();
				city.set("name", cities[j]).set("sort", j).set("p_id", province.get("id")).save();
				JSONArray n2 = items.getJSONArray("0_"+i+"_"+j);
				if(null == n2) continue;
				Object[] counties = n2.toArray();
				//插入区县
				for (int k = 0; k < counties.length; k++) {
					Db.update(countrySql, counties[k],k,city.get("id"));
				}
			}
		}
	}
}
