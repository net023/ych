package com.ych.web.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
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
import com.ych.web.model.BrakingBrandModel;
import com.ych.web.model.BrakingModel;
import com.ych.web.model.BrakingTypeModel;

@Control(controllerKey = "/braking")
public class BrakingController extends BaseController {
	private static Logger LOG = Logger.getLogger(BrakingController.class);

	public void index() {
		try {
			setAttr("brands", ModelTools.putFirstDataRender("id", "name", "", "全部", BrakingBrandModel.dao.getAll()));
			setAttr("types", ModelTools.putFirstDataRender("id", "name", "", "全部", BrakingTypeModel.dao.getAll()));
		} catch (Exception e) {
			LOG.debug("braking_index 失败!" + e.getMessage());
		}
		render("braking/braking_index");
	}
	
	public void list() {
		Pager pager = createPager();
		pager.addParam("brand", getParaToInt("brand"));
		pager.addParam("type", getParaToInt("type"));
		pager.addParam("status", getParaToInt("status"));
		pager.addParam("recmd", getParaToInt("recmd"));
		if(StrKit.notBlank(getPara("snumber"))){
			pager.addParam("snumber", getPara("snumber").trim());
		}
		if(StrKit.notBlank(getPara("lyid"))){
			pager.addParam("lyid", getPara("lyid").trim());
		}
		Page<?> page = BrakingModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	
	@Before(Tx.class)
	public void batchup(){
		Map<String, Object> result = getResultMap();
		try {
			File excelFile = getFile("batchup", SysConstants.IMG_DIR, SysConstants.MAX_POST_SIZE).getFile();
			
			String absolutePath = excelFile.getAbsolutePath();
			for (int ii = 2; ii < 7; ii++) {
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
							//保存天合
							dataHandler(row,j,ii,lyid);
							continue;
						case 2:
							//保存菲罗多
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

	/**
	 * 
	 * @param row 当前的一行
	 * @param bid 当前的品牌id
	 * @param tid 当前的类型id
	 * @param lyid 当前的力洋id
	 */
	private void dataHandler(List<String> row,int bid,int tid,String lyid) {
		//编号
		String snumb = row.get(bid);
		//---
		if(StrKit.notBlank(snumb)){
			String[] snumbs = snumb.split(";");
			for (int k = 0; k < snumbs.length; k++) {
				if(StrKit.notBlank(snumbs[k])&&!snumbs[k].equals("null")){
					BrakingModel model = new BrakingModel();
					if(snumbs[k].indexOf(":")!=-1){
						model.set("name", snumbs[k].substring(3)).set("ly_id", lyid).set("t_id", tid).set("b_id", bid).save();
					}else{
						model.set("name", snumbs[k]).set("ly_id", lyid).set("t_id", tid).set("b_id", bid).save();
					}
				}
			}
		}
	}
	
	@Before(Tx.class)
	public void batchEdit(){
		Map<String, Object> result = getResultMap();
		OutputStreamWriter ow = null;
		BufferedWriter bw = null;
		try {
			File excelFile = getFile("scslb", SysConstants.IMG_DIR, SysConstants.MAX_POST_SIZE).getFile();
			
			String absolutePath = excelFile.getAbsolutePath();
			XxlsPrint howto = new XxlsPrint();
			howto.processOneSheet(absolutePath, 1);
			List<List> data = howto.getMsg();
			List<String> row = null;
			File tempFile = File.createTempFile("上传结果_"+System.currentTimeMillis()/1000, ".txt", new File(SysConstants.IMG_DIR));
			ow = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8");
			bw = new BufferedWriter(ow);
			for (int i = 1; i < data.size(); i++) {
				row = data.get(i);
				String productNum = row.get(0);
				String priceStr = row.get(1);
				if(StrKit.notBlank(productNum,priceStr)){
					BigDecimal price = new BigDecimal(priceStr);
					boolean isUP = BrakingModel.dao.updatePriceByName(price, productNum);
					if(!isUP){
						bw.write(productNum+"	数据库中不存在");
						bw.newLine();
					}
				}
			}
			
			result.put(RESULT, true);
			result.put("f", tempFile.getName());
			result.put(MESSAGE, "上传成功！");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("文件上传失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "上传失败！");
		}finally{
			try {
				if(null!=bw){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		render(new JsonRender(result).forIE());
	}
	
	
	
	@Before(Tx.class)
	public void modify(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = BrakingModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	@Before(Tx.class)
	public void recmd(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = BrakingModel.dao.recmd(id, status);
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
			boolean isUpdate = BrakingModel.dao.findById(uid).set("price", new BigDecimal(price)).update();
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
	
	public static void main(String[] args) {
		String ss = "D1:FSB408";
		System.out.println(ss.indexOf(":"));
		System.out.println(ss.substring(2));
	}
	
	
}
