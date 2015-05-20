package com.ych.base.common;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beetl.core.Tag;

import com.jfinal.kit.JsonKit;

/**
 * 字典select
 * 
 */
public class DisplayTag extends Tag {

	private static final Logger LOG = Logger.getLogger(DisplayTag.class);

	@SuppressWarnings("unchecked")
	@Override
	public void render() {
		Map<String, String> params = (Map<String, String>) args[1];
		String type = params.get("type");
		String key = params.get("key");
		Map<String, Object> display = null;
		try {
			if (type == null) {
				ctx.byteWriter.writeString("");
			} else if ("user".equals(type.toLowerCase())) {
				display = DisplayTool.instance.displayUser();
			} else if ("role".equals(type.toLowerCase())) {
				display = DisplayTool.instance.displayRole();
			} else if ("post".equals(type.toLowerCase())) {
				display = DisplayTool.instance.displayPost();
			}
			if (display != null) {
				if (key == null) {
					ctx.byteWriter.writeString(JsonKit.toJson(display));
				} else {
					String name = display.get(key).toString();
					ctx.byteWriter.writeString(name == null ? "无" : name);
				}
			} else {
				ctx.byteWriter.writeString("{}");
			}
		} catch (Exception e) {
			try {
				ctx.byteWriter.writeString("{}");
				LOG.error("Beetl翻译异常");
			} catch (IOException e1) {
				LOG.error("Beetl翻译异常!");
			}
		}
	}

}