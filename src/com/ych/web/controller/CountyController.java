package com.ych.web.controller;

import com.ych.base.common.BaseController;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.CountyModel;

@Control(controllerKey="/county")
public class CountyController extends BaseController {
	public void getAll(){
		renderJson(CountyModel.dao.getAll(getParaToInt("cid")));
	}
}
