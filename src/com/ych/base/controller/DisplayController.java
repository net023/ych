package com.ych.base.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.ych.base.common.BaseController;
import com.ych.base.common.DisplayTool;
import com.ych.core.plugin.annotation.Control;

@Control(controllerKey = "displayjs")
public class DisplayController extends BaseController {

	private static final Logger LOG = Logger.getLogger(DisplayController.class);

	public void index() {
		String types = getPara("type");
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
		if (StrKit.notBlank(types)) {
			List<String> arr = Arrays.asList(types.split(","));
			for (String type : arr) {
				Map<String, Object> display = null;
				if ("user".equals(type.toLowerCase())) {
					display = DisplayTool.instance.displayUser();
				} else if ("role".equals(type.toLowerCase())) {
					display = DisplayTool.instance.displayRole();
				} else if ("post".equals(type.toLowerCase())) {
					display = DisplayTool.instance.displayPost();
				} else if (type.startsWith("confs[")) {
					type = type.replace("confs[", "").replace("]", "");
					String[] confs = type.split("\\|");
					Map<String, Object> confsMap = new HashMap<String, Object>();
					for (String conf : confs) {
						if ("".equals(conf))
							continue;
						confsMap.put(conf, DisplayTool.instance.displayConfig(conf));
					}
					type = "confs";
					display = confsMap;
				}
				map.put(type, display);
			}
		}
		String json = JsonKit.toJson(map);
		String result = "(function($){$.fn.display = " + json + ";})(jQuery);";
		renderJavascript(result);
	}
}
