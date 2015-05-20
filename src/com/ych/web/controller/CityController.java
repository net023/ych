package com.ych.web.controller;

import com.ych.base.common.BaseController;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.CityModel;

@Control(controllerKey="/city")
public class CityController extends BaseController {
	public void getAll(){
		renderJson(CityModel.dao.getAll(getParaToInt("pid")));
	}
}
