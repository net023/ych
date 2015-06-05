package com.ych.web.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.ych.tools.ModelTools;
import com.ych.tools.SysConstants;
import com.ych.tools.excel.XxlsPrint;
import com.ych.web.model.FilterBrandModel;
import com.ych.web.model.FilterModel;
import com.ych.web.model.FilterTypeModel;
import com.ych.web.model.SparkModel;
import com.ych.web.model.SparkModelModel;

@Control(controllerKey = "/filter4")
public class FilterController extends BaseController {
	private static Logger LOG = Logger.getLogger(FilterController.class);

	public void index() {
		try {
			setAttr("brands", ModelTools.putFirstDataRender("id", "name", "", "全部", FilterBrandModel.dao.getAll()));
			setAttr("types", ModelTools.putFirstDataRender("id", "name", "", "全部", FilterTypeModel.dao.getAll()));
		} catch (Exception e) {
			LOG.debug("spark_index 失败!" + e.getMessage());
		}
		render("filter/filter_index");
	}
	
	public void list() {
		Pager pager = createPager();
		pager.addParam("brand", getParaToInt("brand"));
		pager.addParam("type", getParaToInt("type"));
		if(StrKit.notBlank(getPara("snumber"))){
			pager.addParam("snumber", getPara("snumber").trim());
		}
		if(StrKit.notBlank(getPara("lyid"))){
			pager.addParam("lyid", getPara("lyid").trim());
		}
		Page<?> page = FilterModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	
	public void batchup(){
		Map<String, Object> result = getResultMap();
		try {
			File excelFile = getFile("batchup", SysConstants.IMG_DIR, SysConstants.MAX_POST_SIZE).getFile();
			
			String absolutePath = excelFile.getAbsolutePath();
			for (int ii = 2; ii < 5; ii++) {
				XxlsPrint howto = new XxlsPrint();
				howto.processOneSheet(absolutePath, ii);
				List<List> data = howto.getMsg();
				List<String> row = null;
				for (int i = 2; i < data.size(); i++) {
					row = data.get(i);
					String lyid = null;
					if(row.size()<2){continue;}
					for (int j = 0; j < row.size(); j++) {
						switch(j){
						case 0:
							//保存力洋ID
							lyid = row.get(j);
							continue;
						case 1:
							//保存曼牌
							dataHandler(row,j,ii,lyid);
							continue;
						case 2:
							//保存豹王
							dataHandler(row,j,ii,lyid);
							continue;
						case 3:
							//保存耐诺思
							dataHandler(row,j,ii,lyid);
							continue;
						}
					}
				}
			}
			
			result.put(RESULT, true);
			result.put(MESSAGE, "上传成功！");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("文件上传失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "上传失败！");
		}
		render(new JsonRender(result).forIE());
	}

	private void dataHandler(List<String> row,int bid,int tid,String lyid) {
		//编号
		String snumb = row.get(bid);
		//---
		if(StrKit.notBlank(snumb)){
			String[] snumbs = snumb.split(";");
			for (int k = 0; k < snumbs.length; k++) {
				if(StrKit.notBlank(snumbs[k])&&!snumbs[k].equals("null")){
					FilterModel model = new FilterModel();
					model.set("name", snumbs[k]).set("ly_id", lyid).set("t_id", tid).set("b_id", bid).save();
				}
			}
		}
	}
	
	@Before(Tx.class)
	public void modify(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = FilterModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	@Before(Tx.class)
	public void recmd(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = FilterModel.dao.recmd(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	
	
	@Before(Tx.class)
	public void editPrice(){
		Map<String, Object> result = getResultMap();
		try {
			String price = getPara("price");
			Integer uid = getParaToInt("id");
			boolean isUpdate = FilterModel.dao.findById(uid).set("price", new BigDecimal(price)).update();
			if(isUpdate){
				result.put(RESULT, true);
				result.put(MESSAGE, "价格更新成功！");
			}else{
				result.put(RESULT, false);
				result.put(MESSAGE, "价格更新失败！");
			}
		} catch (Exception e) {
			LOG.debug("价格更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "价格更新失败！");
		}
		renderJson(result);
	}
	
	
}
