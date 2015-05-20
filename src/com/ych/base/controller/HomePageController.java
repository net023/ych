package com.ych.base.controller;

import com.jfinal.log.Logger;
import com.ych.base.common.BaseController;
import com.ych.core.plugin.annotation.Control;

/**
 * 首页
 *
 */
@Control(controllerKey = {"home"})
public class HomePageController extends BaseController {

	private static final Logger LOG = Logger.getLogger(HomePageController.class);

	public void index() {
		render("home");
	}

}
