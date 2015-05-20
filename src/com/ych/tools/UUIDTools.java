package com.ych.tools;

import java.util.UUID;

/**
 * UUID工具类
 */
public class UUIDTools {

	/**
	 * 获取32位uuid主键
	 * 
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
