package com.ych.web.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.render.JsonRender;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.PinYin4JTool;
import com.ych.tools.SysConstants;
import com.ych.tools.excel.XxlsPrint;
import com.ych.web.model.CarBrandModel;
import com.ych.web.model.CarManfModel;
import com.ych.web.model.CarModel;
import com.ych.web.model.CarSeriesModel;
import com.ych.web.model.CarTypeModel;

@Control(controllerKey = "/car")
public class CarController extends BaseController {
	private static Logger LOG = Logger.getLogger(CarController.class);

	public void index() {
		render("car/car_index");
	}

	public void list() {
		Pager pager = createPager();
		if (StrKit.notBlank(getPara("name"))) {
			pager.addParam("name", getPara("name"));
		}
		if (StrKit.notBlank(getPara("ly_id"))) {
			pager.addParam("ly_id", getPara("ly_id"));
		}
		Page<?> page = CarModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	
	public void batchup(){
		Map<String, Object> result = getResultMap();
		try {
			File excelFile = getFile("batchup", SysConstants.IMG_DIR, SysConstants.MAX_POST_SIZE).getFile();
			//org.apache.poi.poifs.filesystem.OfficeXmlFileException: The supplied data appears to be in the Office 2007+ XML. You are calling the part of POI that deals with OLE2 Office Documents. You need to call a different part of POI to process this data (eg XSSF instead of HSSF)
			/*String fileName = excelFile.getName();
			FileInputStream finput = new FileInputStream(excelFile);
			if(fileName.endsWith("xlsx")){
				XSSFWorkbook wb = new XSSFWorkbook(finput);
				SXSSFWorkbook workbook = new SXSSFWorkbook(wb);
				Sheet sheet0 = workbook.getSheetAt(0);
//				XSSFSheet sheet0 = wb.getSheetAt(0);
				for (int i = 2; i <= sheet0.getLastRowNum(); i++) {
					Row rowx = sheet0.getRow(i);
//					XSSFRow rowx = sheet0.getRow(i);
					if (rowx == null) {
						continue;
					}
					for (int j = 0; j < rowx.getLastCellNum(); j++) {
						Cell cellx = rowx.getCell(j);
//						XSSFCell cellx = rowx.getCell(j);
					}
				}
			}else{
				HSSFWorkbook wb = new HSSFWorkbook(finput);
				HSSFSheet sheet0 = wb.getSheetAt(0);
				for (int i = 2; i <= sheet0.getLastRowNum(); i++) {
					HSSFRow rowx = sheet0.getRow(i);
					if (rowx == null) {
						continue;
					}
					for (int j = 0; j < rowx.getLastCellNum(); j++) {
						HSSFCell cellx = rowx.getCell(j);
					}
				}
			}*/
			
			XxlsPrint howto = new XxlsPrint();
			String absolutePath = excelFile.getAbsolutePath();
			howto.processOneSheet(absolutePath, 1);
			List<List> data = howto.getMsg();
			List<String> row = null;
			CarManfModel manf = null;
			CarBrandModel brand = null;
			CarSeriesModel series = null;
			CarTypeModel type = null;
			CarModel car = null;
			for (int i = 1; i < data.size(); i++) {
				row = data.get(i);
				car = new CarModel();
				for (int j = 0; j < row.size(); j++) {
					switch(j){
					case 0:
						//保存力洋ID
						car.set("ly_id", row.get(j));
						continue;
					case 1:
						//保存厂商
						CarManfModel findManfModel = CarManfModel.dao.findByName(row.get(j));
						if(null!=findManfModel){
							manf = findManfModel;
						}else{
							manf = new CarManfModel();
							manf.set("name", row.get(j)).save();
						}
						continue;
					case 2:
						//保存品牌(pinyin4j处理)
						CarBrandModel findBrandModel = CarBrandModel.dao.findByName(row.get(j));
						if(null!=findBrandModel){
							brand = findBrandModel;
						}else{
							brand = new CarBrandModel();
							System.out.println(row);
							System.out.println(row.get(j));
							char charAt0 = PinYin4JTool.getCharAt0(row.get(j));
							System.out.println(charAt0);
							brand.set("name", row.get(j)).set("m_id", manf.getInt("id")).set("sort",new String(new char[]{charAt0}).toLowerCase()).save();
						}
						continue;
					case 3:
						//保存车系
						CarSeriesModel findSeriesModel = CarSeriesModel.dao.findByName(row.get(j));
						if(null!=findSeriesModel){
							series = findSeriesModel;
						}else{
							series = new CarSeriesModel();
							series.set("name", row.get(j)).set("b_id", brand.getInt("id")).save();
						}
						continue;
					case 4:
						//保存车型
						CarTypeModel findTypeModel = CarTypeModel.dao.findByName(row.get(j));
						if(null!=findTypeModel){
							type = findTypeModel;
						}else{
							type = new CarTypeModel();
							type.set("name", row.get(j)).set("s_id", series.getInt("id")).save();
						}
						car.set("t_id", type.getInt("id"));
						continue;
					case 5:
						//保存销售名称
						car.set("name", row.get(j));
						continue;
					case 6:
						//保存年款
						car.set("year", row.get(j));
						continue;
					case 7:
						//保存排放标准
						car.set("es", row.get(j));
						continue;
					case 8:
						//保存车辆类型
						car.set("type", row.get(j));
						continue;
					case 9:
						//保存上市年份
						car.set("listed_y", row.get(j));
						continue;
					case 10:
						//保存上市月份
						car.set("listed_m", row.get(j));
						continue;
					case 11:
						//保存生产年份
						car.set("pdc_y", row.get(j));
						continue;
					case 12:
						//保存停产年份
						car.set("spdc_y", row.get(j));
						continue;
					case 13:
						//保存生产状态
						car.set("pdc_status", row.get(j));
						continue;
					case 14:
						//保存国别
						car.set("country", row.get(j));
						continue;
					case 15:
						//保存国产合资进口
						car.set("from", row.get(j));
						continue;
					case 16:
						//保存气缸容积
						car.set("cl_cpt", row.get(j));
						continue;
					case 17:
						//保存排量(升)
						car.set("disp", row.get(j));
						continue;
					case 18:
						//保存进气形式
						car.set("air_type", row.get(j));
						continue;
					case 19:
						//保存燃料类型
						car.set("fuel_type", row.get(j));
						continue;
					case 20:
						//保存燃油标号
						car.set("fuel_oil", row.get(j));
						continue;
					case 21:
						//保存最大马力(ps)
						car.set("max_horsepower", row.get(j));
						continue;
					case 22:
						//保存最大功率(kW)
						car.set("max_power", row.get(j));
						continue;
					case 23:
						//保存气缸排列形式
						car.set("cl_type", row.get(j));
						continue;
					case 24:
						//保存气缸数(个)
						if(StrKit.notBlank(row.get(j))){
							car.set("cl_count", Integer.valueOf(row.get(j)));
						}
						continue;
					case 25:
						//保存每缸气门数(个)
						if(StrKit.notBlank(row.get(j))){
							car.set("qm_count", Integer.valueOf(row.get(j)));
						}
						continue;
					case 26:
						//保存速器类型
						car.set("ts_type", row.get(j));
						continue;
					case 27:
						//保存变速器描述
						car.set("ts_desc", row.get(j));
						continue;
					case 28:
						//保存档位数
						car.set("gear", row.get(j));
						continue;
					case 29:
						//保存前制动器类型
						car.set("fb_type", row.get(j));
						continue;
					case 30:
						//保存后制动器类型
						car.set("rb_type", row.get(j));
						continue;
					case 31:
						//保存助力类型
						car.set("bt_type", row.get(j));
						continue;
					case 32:
						//保存发动机位置
						car.set("engine_lc", row.get(j));
						continue;
					case 33:
						//保存驱动方式
						car.set("drive_type", row.get(j));
						continue;
					case 34:
						//保存轴距(mm)
						car.set("whd", row.get(j));
						continue;
					case 35:
						//保存车门数
						car.set("door", row.get(j));
						continue;
					case 36:
						//保存座位数(个)
						car.set("seat", row.get(j));
						continue;
					case 37:
						//保存前轮胎规格
						car.set("f_tyre", row.get(j));
						continue;
					case 38:
						//保存后轮胎规格
						car.set("b_tyre", row.get(j));
						continue;
					case 39:
						//保存前轮毂规格
						car.set("f_hub", row.get(j));
						continue;
					case 40:
						//保存后轮毂规格
						car.set("b_hub", row.get(j));
						continue;
					case 41:
						//保存轮毂材料
						car.set("hub_mate", row.get(j));
						continue;
					//可能为空。。。
					case 42:
						//保存备胎规格
						car.set("spare_wh", row.get(j));
						continue;
					case 43:
						//保存电动天窗
						car.set("power_sun", row.get(j));
						continue;
					case 44:
						//保存全景天窗
						car.set("full_sum", row.get(j));
						continue;
					case 45:
						//保存氙气大灯
						car.set("xenon_light", row.get(j));
						continue;
					case 46:
						//保存前雾灯
						car.set("ffl", row.get(j));
						continue;
					case 47:
						//保存后雨刷
						car.set("rw", row.get(j));
						continue;
					case 48:
						//保存空调
						car.set("air_c", row.get(j));
						continue;
					case 49:
						//保存自动空调
						car.set("auto_airc", row.get(j));
						continue;
					}
				}
				car.save();
			}
			
			result.put(RESULT, true);
			result.put(MESSAGE, "上传成功！");
		} catch (Exception e) {
			LOG.debug("文件上传失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "上传失败！");
		}
		render(new JsonRender(result).forIE());
	}
	
	@Before(Tx.class)
	public void modify(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = CarModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	
	
	public static void main(String[] args) {
		String dd = "[CGD0118M0033, 吉利汽车, 帝豪, EC7, EC7, 1.8 手动 GSG版, 2012, 国4, 轿车, 2011, 9, 2011, 2013, 停产, 中国, 国产, 1808;1799, 1.8, 自然吸气, 汽油, 93#, 133, 98, L, 4, 4, 手动, 手动变速器(MT), 5, 通风盘式, 盘式, 机械液压助力, 前置, 前轮驱动, 2650, 四门, 5, 205/55 R16, 205/55 R16, 16英寸, 16英寸, 铝合金, 全尺寸, 有, 无, 无, 有, 无, 有, 有]";
		String substring = dd.substring(1, dd.length()-1);
		System.out.println(substring);
	}
	
	
}
