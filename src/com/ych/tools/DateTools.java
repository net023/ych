package com.ych.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 并发时间工具类
 * 
 */
public class DateTools {

	public static ThreadLocal<DateFormat> yyyy_MM_dd = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	public static ThreadLocal<DateFormat> yyyy_MM_dd_HH = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH");
		}
	};
	public static ThreadLocal<DateFormat> yyyy_MM_dd_HH_mm_ss = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	public static ThreadLocal<DateFormat> yyyyMMdd = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMdd");
		}
	};
	public static ThreadLocal<DateFormat> yyyyMMddHH = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHH");
		}
	};
	public static ThreadLocal<DateFormat> yyyyMMddHHmmss = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHHmmss");
		}
	};
	public static ThreadLocal<DateFormat> HH = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("HH");
		}
	};

	/**
	 * 通过搜索时间 获取小时数 天数等
	 * 
	 * @param searchTime
	 * @param dateFormat
	 * @param calendarType
	 * @return
	 * @throws ParseException
	 */
	public static int getTimeBySearchTime(String searchTime, ThreadLocal<DateFormat> dateFormat, int calendarType) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(searchTime, dateFormat));
		return calendar.get(calendarType);
	}

	/**
	 * 通过搜索时间获取天
	 * 
	 * @param searchTime
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateBySearchTime(String searchTime, ThreadLocal<DateFormat> dateFormat) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(searchTime, dateFormat));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 字符串转换为日期格式
	 * 
	 * @param sDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String sDate) throws ParseException {
		return yyyy_MM_dd.get().parse(sDate);
	}

	/**
	 * 字符串转换为日期格式
	 * 
	 * @param sDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateTime(String sDate) throws ParseException {
		return yyyy_MM_dd_HH_mm_ss.get().parse(sDate);
	}

	/**
	 * 根据模版输出date
	 * 
	 * @Author zyz 2014年8月21日 下午3:56:43
	 * @param sDate
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String sDate, ThreadLocal<DateFormat> dateFormat) throws ParseException {
		return dateFormat.get().parse(sDate);
	}

	/**
	 * 格式化日期输出字符串
	 * 
	 * @param date
	 * @return yyyy-MM-dd
	 * @throws ParseException
	 */
	public static String formatDate(Date date) {
		return yyyy_MM_dd.get().format(date);
	}

	/**
	 * 格式化日期时间输出字符串
	 * 
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 * @throws ParseException
	 */
	public static String formatDateTime(Date date) {
		return yyyy_MM_dd_HH_mm_ss.get().format(date);
	}

	public static ThreadLocal<DateFormat> yyyyMMddHHmmssSSS = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHHmmssSSS");
		}
	};
	/**
	 * 根据模版输出时间字符串
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String format(Date date, ThreadLocal<DateFormat> dateFormat) {
		return dateFormat.get().format(date);
	}

	public static String getDate(Date date, String format) {

		String dtstr = "";
		if (date == null) {
			return dtstr;
		}

		if (format == null || "".equals(format.trim())) {
			format = "yyyy-MM-dd";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		dtstr = sdf.format(date);
		return (dtstr == null ? "" : dtstr);

	}
	public static String getDate(Date date) {
		return getDate(date, "yyyy-MM-dd");
	}
	/**
	 * 取得当前日期对象
	 * 
	 * @return 返回java.util.Date日期对象
	 */
	public static Date getNow() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 获取当前时间前hourNum个小时时间字符串
	 * 
	 * @param dateFormat
	 * @param hourNum
	 * @return
	 */
	public static String getHourBeforeTimeStr(ThreadLocal<DateFormat> dateFormat, int hourNum) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hourNum);
		return dateFormat.get().format(calendar.getTime());
	}

	/**
	 * 获取当前时间前hourNum个小时时间
	 * 
	 * @param hourNum
	 * @return
	 */
	public static Date getHourBeforeTime(int hourNum) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hourNum);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取当前时间前dayNum天时间字符串
	 * 
	 * @param dateFormat
	 * @param dayNum
	 * @return
	 */
	public static String get1DayBeforeTimeStr(ThreadLocal<DateFormat> dateFormat, int dayNum) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - dayNum);
		return dateFormat.get().format(calendar.getTime());
	}

	/**
	 * 获取当前时间日历对象
	 * 
	 * @return 返回java.util.Calendar日期对象
	 */
	public static Calendar getNowCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * 计算到下一天hour的秒数（单位:秒）
	 * 
	 * @return interval int （单位:秒）
	 */
	public static int getNextDayHourSecond(int hour) {
		int interval = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.AM_PM, Calendar.AM);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// 一天秒数远小于Integer.MAX_VALUE, 不考虑溢出, 强转
		interval = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / (1000));
		if (interval < 0) {
			interval += (60 * 60 * 24);
		}
		return interval;
	}

	/**
	 * 计算到下一个整点的秒数
	 * 
	 * @return
	 */
	public static int getNextHourSecond() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		int interval = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / (1000));
		return interval;
	}

}
