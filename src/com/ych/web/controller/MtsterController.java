package com.ych.web.controller;

import java.util.List;

import com.ych.base.common.BaseController;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.MtSerModel;

@Control(controllerKey="/mtser")
public class MtsterController extends BaseController {

	public void getAllFirsts() {
		renderJson(MtSerModel.dao.getAllFirsts());
	}
	
	public void getAllSecondsByFirst() {
		List<MtSerModel> mtsers = MtSerModel.dao.getAllSecondCategoriesByParent(getParaToInt("pid"));
		MtSerModel mtser = new MtSerModel();
		mtser.set("id", "").set("name", "选择二级类目");
		mtsers.add(0, mtser);
		renderJson(mtsers);
	}
}
