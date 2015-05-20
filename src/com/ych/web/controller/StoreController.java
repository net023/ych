package com.ych.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.MtSerModel;
import com.ych.web.model.ProvinceModel;
import com.ych.web.model.StoreModel;
import com.ych.web.model.StoreMtserModel;
import com.ych.web.model.StorePicModel;

@Control(controllerKey = "/store")
public class StoreController extends BaseController {
	private static Logger LOG = Logger.getLogger(StoreController.class);

	public void index() {
		List<MtSerModel> allMtSers = MtSerModel.dao.getAll();
		setAttr("allMtSers", allMtSers);
		// setAttr("allMtSers", JsonKit.toJson(allMtSers).replace("\"", "'"));
		// setAttr("channels",
		// "[{'id':'','name':'全部'},{'id':1,'name':'上线'},{'id':2,'name':'下线'}]");
		// 省份
		List<ProvinceModel> province = ProvinceModel.dao.getAll();
		List<ProvinceModel> tempProvince = new ArrayList<ProvinceModel>(
				province);
		ProvinceModel defaultProvince = new ProvinceModel();
		defaultProvince.set("id", "").set("name", "选择省份");
		tempProvince.add(0, defaultProvince);
		setAttr("provinces", JsonKit.toJson(tempProvince).replace("\"", "'"));
		// 地级市
		setAttr("cities", "[{'id':'','name':'选择地级市'}]");
		// 区县
		setAttr("counties", "[{'id':'','name':'选择区县'}]");
		render("store/store_index");
	}

	public void list() {
		Pager pager = createPager();
		if (StrKit.notBlank(getPara("storeName"))) {
			pager.addParam("storeName", getPara("storeName"));
		}
		Page<?> page = StoreModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	public void add() {
		Map<String, Object> result = getResultMap();
		try {
			getFile();
			StoreModel store = getModelWithOutModelName(StoreModel.class, true);
			store.set("c_time", new Date()).save();
			// 图片文件id
			String fsStr = getPara("fs");
			String[] fs = fsStr.split("\\|");
			for (String fid : fs) {
				StorePicModel storePic = new StorePicModel();
				storePic.set("s_id", store.getInt("id"))
						.set("f_id", Integer.valueOf(fid)).save();
			}
			Map<String, String[]> paraMap = getParaMap();
			// 服务类型
			String[] ms = paraMap.get("ms");
			for (String mid : ms) {
				StoreMtserModel storeMtser = new StoreMtserModel();
				storeMtser.set("s_id", store.getInt("id"))
						.set("ms_id", Integer.valueOf(mid)).save();
			}
			result.put(RESULT, true);
			result.put(MESSAGE, "Store添加成功！");
		} catch (Exception e) {
			LOG.debug("Store添加失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "添加失败！");
		}
		renderJson(result);
	}

}
