package com.ych.web.controller;

import com.ych.base.common.BaseController;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.ProvinceModel;

@Control(controllerKey="/province")
public class ProvinceController extends BaseController {
	public void getAll(){
		renderJson(ProvinceModel.dao.getAll());
	}
}
