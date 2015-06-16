package com.ych.tools;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

public class BaiDuLBSTool {
	public static final String CREATEGEOTABLE = "http://api.map.baidu.com/geodata/v3/geotable/create";
	public static final String CREATEPOI = "http://api.map.baidu.com/geodata/v3/poi/create";
	public static final String LISTPOI = "http://api.map.baidu.com/geodata/v3/poi/list";
	public static final String SEARCHLOCAL = "http://api.map.baidu.com/geosearch/v3/local";
	public static final String NEARBY = "http://api.map.baidu.com/geosearch/v3/nearby";
	public static final String UPDATEPOI = "http://api.map.baidu.com/geodata/v3/poi/update";
	public static final String DELETEPOI = "http://api.map.baidu.com/geodata/v3/poi/delete";
	// 百度ak
	public static final String AK = "EBee53c88a82e5213476c7d9961396bc";
	// 周围40千米(40公里)
	public static final String RADIUS = "400000";
	// 数据库id
	public static final String GEOID = "107962";

	// 创建数据库
	public static String createGeoTable(String name, Integer geotype,
			Integer is_published, String ak) {
		HttpResponse send = HttpRequest.post(CREATEGEOTABLE).form("name", name)
				.form("geotype", geotype).form("is_published", is_published)
				.form("ak", ak).send();

		return send.body();
	}

	// 上传poi
	public static String createPoi(String title, String address, String tags,
			Double latitude, Double longitude, String geotable_id, String ak,
			Integer sid) {
		HttpResponse send = HttpRequest.post(CREATEPOI).form("title", title)
				.form("address", address).form("tags", tags)
				.form("latitude", latitude).form("longitude", longitude)
				.form("coord_type", 3).form("geotable_id", geotable_id)
				.form("sid", sid).form("ak", ak).send();
		return send.body();
	}
	
	//更新poi
	public static String updatePoi(String geotable_id, String ak,String title, String address, String tags,Double latitude, Double longitude,Integer id){
		HttpResponse send = HttpRequest.post(UPDATEPOI).form("title", title)
				.form("address", address).form("tags", tags)
				.form("latitude", latitude).form("longitude", longitude)
				.form("coord_type", 3).form("geotable_id", geotable_id).form("ak", ak).form("id", id).send();
		return send.body();
	}
	
	//删除poi
	public static String deletePoi(String geotable_id, String ak,Integer id){
		HttpResponse send = HttpRequest.post(DELETEPOI).form("geotable_id", geotable_id).form("ak", ak).form("id", id).send();
		return send.body();
	}


	// poi周边搜索
	public static String local(String ak, String geotable_id, String q,
			String region, String tags, String sortby, String filter,
			Integer page_index, Integer page_size) {
		HttpResponse send = HttpRequest.get(SEARCHLOCAL).query("ak", ak)
				.query("geotable_id", geotable_id).query("q", q)
				.query("coord_type", 3).query("region", region)
				.query("tags", tags).query("sortby", sortby)
				.query("filter", filter).query("page_index", page_index)
				.query("page_size", page_size).send();
		return send.body();
	}

	// poi周边搜索
	public static String nearby(String ak, String geotable_id, String q,
			String location, String radius, String sortby, String page_index,
			String page_size) {
		HttpResponse send = HttpRequest.get(NEARBY).query("ak", ak)
				.query("geotable_id", geotable_id).query("q", q)
				.query("location", location).query("radius", radius)
				.query("sortby", sortby).query("page_index", page_index)
				.query("page_size", page_size).send();
		return send.body();
	}

	public static void main(String[] args) {
		// 创建数据库
		// System.out.println(createGeoTable("快卡", 1, 1, YchConstants.AK));
		// 107962

		// 上传poi
//		 System.out.println(createPoi("测试1", "重庆观音桥", null, 106.32, 60.55,
//		 "107962", AK,11));

		// 搜索附近 按距离排序
		// System.out.println(local(YchConstants.AK, "107962", "", "重庆", null,
		// "distance:1", null, 0, 20));

		// 查询 指定经纬度附近的门店 按距离排序
		// System.out.println(nearby(AK, "107962", "", "106.56004,29.580797",
		// RADIUS, "distance:1", "0", "20"));
		 
		 //更新poi
		 System.out.println(updatePoi("107962", AK, "xxx", "观音桥xxx", null, 106.32, 60.55, 945969865));

	}
}
