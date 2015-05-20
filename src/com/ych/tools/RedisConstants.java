package com.ych.tools;

/**
 * redis常量
 *
 */
public class RedisConstants {

	/**
	 * 删除缓存时间(单位天)
	 */
	public static final int DEL_CACHETIME = 24;

	/**
	 * 默认缓存时间一天(单位秒)
	 */
	public static final int CACHETIME_DAY = 3600 * 24;

	/**
	 * 默认缓存时间二天(单位秒)
	 */
	public static final int CACHETIME_2DAY = 3600 * 24 * 2;

	/**
	 * 默认缓存时间一周(单位秒)
	 */
	public static final int CACHETIME_WEEK = 3600 * 24 * 7;

	/**
	 * 默认缓存时间一月(单位秒)
	 */
	public static final int CACHETIME_MONTH = 3600 * 24 * 30;

	/**
	 * 默认缓存时间三月(单位秒)
	 */
	public static final int CACHETIME_3MONTH = 3600 * 24 * 90;

	/**
	 * 默认缓存时间六月(单位秒)
	 */
	public static final int CACHETIME_6MONTH = 3600 * 24 * 180;

	/**
	 * 默认缓存时间年(单位秒)
	 */
	public static final int CACHETIME_YEAR = 3600 * 24 * 365;

	/**
	 * 无图标广告上报key
	 */
	public static final String ADS_NOICON_REPORT = "anr_";

	/**
	 * 无图标广告上报详情key
	 */
	public static final String ADS_NOICON_REPORT_DETAIL = "anrd_";

	/**
	 * 无图标广告上报key
	 */
	public static final String ADS_NOICON_PUSH_REPORT = "anpr_";

	/**
	 * 无图标广告上报详情key
	 */
	public static final String ADS_NOICON_PUSH_REPORT_DETAIL = "anprd_";

	/**
	 * 无图标app上报key
	 */
	public static final String ADS_NOICON_APP_REPORT = "anar_";

	/**
	 * 无图标app上报详情key
	 */
	public static final String ADS_NOICON_APP_REPORT_DETAIL = "anard_";

	/**
	 * 用户安装的广告
	 */
	public static final String ADS_NOICON_INSTALL_DETAIL = "anid_";

	/**
	 * 每条无图标广告每日限制展示数的key
	 */
	public static final String ADS_NOICON_DAILYSHOWNUM = "and_";

	/**
	 * app每日活跃key前缀
	 * 用于判断每日活跃量
	 */
	public static final String APP_DAY_ACTIVE = "ada_";

	/**
	 * app永久存活
	 * 用于判断新增量(永久去重)
	 */
	public static final String APP_FOREVER_ACTIVE = "afa_";

	/**
	 * update app每日活跃key前缀
	 * 用于判断每日活跃量
	 */
	public static final String APP_UPDATE_DAY_ACTIVE = "auda_";

	/**
	 * update app永久存活
	 * 用于判断新增量(永久去重)
	 */
	public static final String APP_UPDATE_FOREVER_ACTIVE = "aufa_";

	/**
	 * update app升级成功每日活跃key前缀
	 * 用于判断每日活跃量
	 */
	public static final String APP_UPDATE_SUCCESS_DAY_ACTIVE = "ausda_";

}
