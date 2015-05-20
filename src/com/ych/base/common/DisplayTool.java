package com.ych.base.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.ehcache.CacheKit;
import com.ych.base.model.SysConfigModel;
import com.ych.base.model.SysPostsModel;
import com.ych.base.model.SysRolesModel;
import com.ych.base.model.SysUserModel;
import com.ych.tools.EhcacheConstants;

public class DisplayTool {

	public static DisplayTool instance = new DisplayTool();

	public static final String USER_KEY = "DISPLAY_USER";
	public static final String ROLE_KEY = "DISPLAY_ROLE";
	public static final String POST_KEY = "DISPLAY_POST";
	public static final String CONFIG_KEY = "DISPLAY_CONF";

	private Object getCache(String key) {
		return CacheKit.get(EhcacheConstants.DISPLAY_DIRECTIVE, key);
	}
	private void putCache(String key, Object value) {
		CacheKit.put(EhcacheConstants.DISPLAY_DIRECTIVE, key, value);
	}

	public static void removeCache(String key) {
		CacheKit.remove(EhcacheConstants.DISPLAY_DIRECTIVE, key);
	}

	public static void removeCacheAll() {
		CacheKit.removeAll(EhcacheConstants.DISPLAY_DIRECTIVE);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> displayUser() {
		Map<String, Object> result = null;
		// 获取缓存对象
		Object cacheResult = getCache(USER_KEY);
		if (cacheResult != null) {
			result = (Map<String, Object>) cacheResult;
		} else {
			result = new HashMap<String, Object>();
			List<SysUserModel> users = SysUserModel.dao.getAllUserName();
			for (SysUserModel user : users) {
				result.put(user.getStr("id"), user.getStr("nick"));
			}
			putCache("DISPLAY_USER", result);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> displayRole() {
		Map<String, Object> result = null;
		// 获取缓存对象
		Object cacheResult = getCache(ROLE_KEY);
		if (cacheResult != null) {
			result = (Map<String, Object>) cacheResult;
		} else {
			result = new HashMap<String, Object>();
			List<SysRolesModel> roles = SysRolesModel.dao.allRoles();
			for (SysRolesModel role : roles) {
				result.put(role.get("id").toString(), role.getStr("roleName"));
			}
			putCache("DISPLAY_ROLE", result);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> displayPost() {
		Map<String, Object> result = null;
		// 获取缓存对象
		Object cacheResult = getCache(POST_KEY);
		if (cacheResult != null) {
			result = (Map<String, Object>) cacheResult;
		} else {
			result = new HashMap<String, Object>();
			List<SysPostsModel> posts = SysPostsModel.dao.allPosts();
			for (SysPostsModel post : posts) {
				result.put(post.get("id").toString(), post.getStr("postName"));
			}
			putCache("DISPLAY_POST", result);
		}
		return result;
	}
	
	
	public Map<String, Object> displayConfig(String key) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 获取缓存对象
		List<SysConfigModel> list = SysConfigModel.dao.getConfigs(key);
		for (SysConfigModel confg : list) {
			result.put(confg.getStr("field"), confg.getStr("val"));
		}
		return result;
	}

}
