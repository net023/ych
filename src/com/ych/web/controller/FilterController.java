package com.ych.web.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.DateTools;
import com.ych.tools.ModelTools;
import com.ych.tools.SysConstants;
import com.ych.tools.excel.XxlsPrint;
import com.ych.web.model.FileModel;
import com.ych.web.model.FilterBrandModel;
import com.ych.web.model.FilterModel;
import com.ych.web.model.FilterPicModel;
import com.ych.web.model.FilterPriceModel;
import com.ych.web.model.FilterTypeModel;

@Control(controllerKey = "/filter4")
public class FilterController extends BaseController {
	private static Logger LOG = Logger.getLogger(FilterController.class);

	public void index() {
		try {
			setAttr("brands", ModelTools.putFirstDataRender("id", "name", "", "全部", FilterBrandModel.dao.getAll()));
			setAttr("types", ModelTools.putFirstDataRender("id", "name", "", "全部", FilterTypeModel.dao.getAll()));
			setAttr("brands2", JsonKit.toJson(FilterBrandModel.dao.getAll()).replace("\"", "'"));
			setAttr("types2", JsonKit.toJson(FilterTypeModel.dao.getAll()).replace("\"", "'"));
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
	
	public void priceList() {
		Pager pager = createPager();
		pager.addParam("brand", getParaToInt("brand"));
		pager.addParam("type", getParaToInt("type"));
		pager.addParam("status", getParaToInt("status"));
		pager.addParam("recmd", getParaToInt("recmd"));
		
		Page<?> page = FilterPriceModel.dao.getPager(pager);
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
	
	@Before(Tx.class)
	public void batchEdit(){
		Map<String, Object> result = getResultMap();
//		OutputStreamWriter ow = null;
//		BufferedWriter bw = null;
		try {
			File excelFile = getFile("upFile1", SysConstants.IMG_DIR, SysConstants.MAX_POST_SIZE).getFile();
			Integer brand = getParaToInt("brand");
			Integer type = getParaToInt("type");
			
			String absolutePath = excelFile.getAbsolutePath();
			XxlsPrint howto = new XxlsPrint();
			howto.processOneSheet(absolutePath, 1);
			List<List> data = howto.getMsg();
			List<String> row = null;
//			File tempFile = File.createTempFile("上传结果_"+System.currentTimeMillis()/1000, ".txt", new File(SysConstants.IMG_DIR));
//			ow = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8");
//			bw = new BufferedWriter(ow);
			for (int i = 1; i < data.size(); i++) {
				row = data.get(i);
				String productNum = row.get(0);
				String priceStr = row.get(1);
				System.out.println(row);
				if(StrKit.notBlank(productNum,priceStr)){
					BigDecimal price = new BigDecimal(priceStr);
					FilterPriceModel model = FilterPriceModel.dao.getModelByBrandTypeNum(brand, type, productNum);
					if(null!=model){
						model.set("price", price).update();
					}else{
						new FilterPriceModel().set("b_id", brand).set("t_id", type).set("price", price).set("num", productNum).save();
					}
					/*boolean isUP = FilterModel.dao.updatePriceByName(price, productNum);
					if(!isUP){
						bw.write(productNum+"	数据库中不存在");
						bw.newLine();
					}*/
				}
			}
			
//			bw.write("恭喜你上传成功了!!");
			
			result.put(RESULT, true);
//			result.put("f", tempFile.getName());
			result.put(MESSAGE, "上传成功！");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("文件上传失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "上传失败！");
		}
		/*finally{
			try {
				if(null!=bw){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
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
		boolean b = FilterPriceModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	@Before(Tx.class)
	public void recmd(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = FilterPriceModel.dao.recmd(id, status);
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
			boolean isUpdate = FilterPriceModel.dao.findById(uid).set("price", new BigDecimal(price)).update();
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
	
	
	@Before(Tx.class)
	public void upload() {
		Map<String, Object> result = getResultMap();
		try {
			UploadFile upload = getFile("upFile", SysConstants.IMG_DIR, SysConstants.MAX_POST_SIZE);
			String fileName = upload.getOriginalFileName();
			String newFileName = DateTools.format(new Date(), DateTools.yyyyMMddHHmmssSSS) + fileName.substring(fileName.lastIndexOf("."), fileName.length());
			//修改上传文件的文件名称
			upload.getFile().renameTo(new File(SysConstants.IMG_DIR + File.separator + newFileName));
			FileModel sysFile = new FileModel();
			sysFile.set("o_name", fileName).set("n_name", newFileName).set("l_path", upload.getFile().getParentFile().getAbsolutePath()+"/"+newFileName).set("size", upload.getFile().length())
				.set("u_time", new java.sql.Date(System.currentTimeMillis())).set("type",1).save();
			Integer brand = getParaToInt("brand");
			Integer tid = getParaToInt("tid");
			FilterPicModel model = FilterPicModel.dao.getByBidTid(brand,tid);
			if(null!=model){
				//删除旧图片  更新model
				Integer pid = model.getInt("p_id");
				FileModel fileModel = FileModel.dao.findById(pid);
				if(null!=fileModel){
					String localPath = fileModel.getStr("l_path");
					if(StrKit.notBlank(localPath)){
						FileKit.delete(new File(localPath));
					}	
					fileModel.delete();
				}
				model.set("b_id", brand).set("t_id", tid).set("p_id", sysFile.getInt("id")).update();
			}else{
				new FilterPicModel().set("b_id", brand).set("t_id", tid).set("p_id", sysFile.getInt("id")).save();
			}
			result.put("fID", sysFile.getInt("id"));
			result.put(RESULT, true);
			result.put(MESSAGE, "上传成功！");
		} catch (Exception e) {
			LOG.debug("文件上传失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "上传失败！");
		}
		render(new JsonRender(result).forIE());
	}
	
	public void download(){
		Integer brand = getParaToInt("brand");
		Integer type = getParaToInt("type");
		FilterPicModel model = FilterPicModel.dao.getByBidTid(brand, type);
		if(null!=model){
			Integer fid = model.getInt("p_id");
			FileModel sf = FileModel.dao.findById(fid);
			renderFile(new File(sf.getStr("l_path")));
		}else{
			renderNull();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		String str = "%E4%B8%8A%E4%BC%A0%E7%BB%93%E6%9E%9C_14338440626701421417820739167.txt";
		String decode = URLDecoder.decode(str, "UTF-8");
		System.out.println(decode);
		
	}
	
	
}
