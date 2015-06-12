package com.ych.web.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.ych.web.model.SparkBrandModel;
import com.ych.web.model.SparkModel;
import com.ych.web.model.SparkModelModel;
import com.ych.web.model.SparkPicModel;
import com.ych.web.model.SparkSeriesModel;

@Control(controllerKey = "/spark")
public class SparkController extends BaseController {
	private static Logger LOG = Logger.getLogger(SparkController.class);

	public void index() {
		try {
			String df = "[{'id':'','name':'全部'}]";
			setAttr("brands", ModelTools.putFirstDataRender("id", "name", "", "全部", SparkBrandModel.dao.getAll()));
			setAttr("pinpai", JsonKit.toJson(SparkBrandModel.dao.getAll()).replace("\"", "'"));
			setAttr("series", df);
			setAttr("models", df);
		} catch (Exception e) {
			LOG.debug("spark_index 失败!" + e.getMessage());
		}
		render("spark/spark_index");
	}
	
	public void getAllSeries(){
		renderJson(ModelTools.putFirstDataRenderJson("id", "name", "", "全部", SparkSeriesModel.dao.getAll(getParaToInt("bid"))));
	}
	public void getAllModels(){
		renderJson(ModelTools.putFirstDataRenderJson("id", "name", "", "全部", SparkModelModel.dao.getAll(getParaToInt("sid"))));
	}
	

	public void list() {
		Pager pager = createPager();
		pager.addParam("brand", getParaToInt("brand"));
		pager.addParam("series", getParaToInt("series"));
		pager.addParam("model", getParaToInt("model"));
		pager.addParam("status", getParaToInt("status"));
		pager.addParam("recmd", getParaToInt("recmd"));
		if(StrKit.notBlank(getPara("snumber"))){
			pager.addParam("snumber", getPara("snumber").trim());
		}
		if(StrKit.notBlank(getPara("lyid"))){
			pager.addParam("lyid", getPara("lyid").trim());
		}
		Page<?> page = SparkModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	
	@Before(Tx.class)
	public void batchup(){
		Map<String, Object> result = getResultMap();
		try {
			File excelFile = getFile("batchup", SysConstants.IMG_DIR, SysConstants.MAX_POST_SIZE).getFile();
			
			XxlsPrint howto = new XxlsPrint();
			String absolutePath = excelFile.getAbsolutePath();
			howto.processOneSheet(absolutePath, 1);
			List<List> data = howto.getMsg();
			List<String> row = null;
			for (int i = 2; i < data.size(); i++) {
				row = data.get(i);
				//产品数组
				List<SparkModel> products = new ArrayList<SparkModel>();
				//型号数组
				List<SparkModelModel> models = new ArrayList<SparkModelModel>();
				System.out.println("ctj____"+row);
				String lyid = null;
				if(row.size()<=2){continue;}
				for (int j = 0; j < row.size(); j++) {
					System.out.println("ctj___"+j);
					switch(j){
					case 0:
						//保存力洋ID
						lyid = row.get(j);
						continue;
					case 1:
						//保存博士->迅能编号
						dataHandler(row, products, models, j,1);
						continue;
					case 2:
						//保存博士->迅能型号
						continue;
					case 3:
						//保存博士->超能(编号)
						dataHandler(row, products, models, j,2);
						continue;
					case 4:
						//保存博士->超能(型号)
						continue;
					case 5:
						//保存博士->锐能(编号)
						dataHandler(row, products, models, j,3);
						continue;
					case 6:
						//保存博士->锐能(型号)
						continue;
					case 7:
						//保存NGK->推荐NGK火花塞(编号)
						dataHandler(row, products, models, j,4);
						continue;
					case 8:
						//保存NGK->推荐NGK火花塞(型号)
						continue;
					case 9:
						//保存NGK->VX火花塞(编号)
						dataHandler(row, products, models, j,5);
						continue;
					case 10:
						//保存NGK->VX火花塞(型号)
						continue;
					case 11:
						//保存NGK->IX铱合金(编号)
						dataHandler(row, products, models, j,6);
						continue;
					case 12:
						//保存NGK->IX铱合金火花塞(型号)
						continue;
					case 13:
						//保存NGK->CX烈焰(编号)
						dataHandler(row, products, models, j,7);
						continue;
					case 14:
						//保存NGK->CX烈焰(型号)
						continue;
					}
				}
				for (SparkModel sparkModel : products) {
					sparkModel.set("ly_id", lyid).save();
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

	private void dataHandler(List<String> row, List<SparkModel> products,
			List<SparkModelModel> models, int j,int sid) {
		models = new ArrayList<SparkModelModel>();
		//编号
		String snumb = row.get(j);
		//型号
		String mnumb = row.get(j+1);
		if(StrKit.notBlank(mnumb)){
			String[] mnumbs = mnumb.split(";");
			for (int k = 0; k < mnumbs.length; k++) {
				int indexOf = mnumbs[k].indexOf("(");
				/*if(indexOf!=-1){
					//old
					String s1 = mnumbs[k].substring(indexOf+1, mnumbs[k].length()-1);
					//new
					String s2 = mnumbs[k].substring(0, indexOf);
					SparkModelModel m1 = SparkModelModel.dao.getModelByName(s1,sid);
					if(null==m1){
						m1 = new SparkModelModel();
						m1.set("name", s1).set("s_id", sid).save();
					}
					SparkModelModel m2 = SparkModelModel.dao.getModelByName(s2,sid);
					if(null==m2){
						m2 = new SparkModelModel();
						m2.set("name", s2).set("s_id", sid).set("o_id", m1.getInt("id")).save();
					}
					models.add(m2);
				}*/
				
				if(indexOf!=-1){
					if(mnumbs[k].startsWith("(")){
						//old
						String s1 = mnumbs[k].substring(1, mnumbs[k].indexOf(")"));
						//new
						String s2 = mnumbs[k].substring(mnumbs[k].indexOf(")")+1);
						SparkModelModel m1 = SparkModelModel.dao.getModelByName(s1,sid);
						if(null==m1){
							m1 = new SparkModelModel();
							m1.set("name", s1).set("s_id", sid).save();
						}
						SparkModelModel m2 = SparkModelModel.dao.getModelByName(s2,sid);
						if(null==m2){
							m2 = new SparkModelModel();
							m2.set("name", s2).set("s_id", sid).set("o_id", m1.getInt("id")).save();
						}
						models.add(m2);
					}else{
						//old
						String s1 = mnumbs[k].substring(indexOf+1, mnumbs[k].length()-1);
						//new
						String s2 = mnumbs[k].substring(0, indexOf);
						SparkModelModel m1 = SparkModelModel.dao.getModelByName(s1,sid);
						if(null==m1){
							m1 = new SparkModelModel();
							m1.set("name", s1).set("s_id", sid).save();
						}
						SparkModelModel m2 = SparkModelModel.dao.getModelByName(s2,sid);
						if(null==m2){
							m2 = new SparkModelModel();
							m2.set("name", s2).set("s_id", sid).set("o_id", m1.getInt("id")).save();
						}
						models.add(m2);
					}
				}
				
				if(StrKit.notBlank(mnumbs[k])&&!mnumbs[k].equals("null")){
					SparkModelModel m = SparkModelModel.dao.getModelByName(mnumbs[k],sid);
					if(null==m){
						m = new SparkModelModel();
						m.set("name", mnumbs[k]).set("s_id", sid).save();
					}
					models.add(m);
				}
			}
		}
		//---
		if(StrKit.notBlank(snumb)){
			String[] snumbs = snumb.split(";");
			int flag = 0;
			for (int k = 0; k < snumbs.length; k++) {
				String[] sn = snumbs[k].split(",");
				for (String ss : sn) {
					if(StrKit.notBlank(ss)&&!ss.equals("null")){
						SparkModel model = new SparkModel();
						Integer moid = SparkModelModel.dao.getOIDByNewID(models.get(k-flag).getInt("id"));
						model.set("s_id", sid).set("s_number", ss).set("m_id", models.get(k-flag).getInt("id")).set("m_oid", moid);
						products.add(model);
					}else{
						flag ++;
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
					boolean isUP = SparkModel.dao.updatePriceByName(price, productNum);
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
		boolean b = SparkModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	@Before(Tx.class)
	public void recmd(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = SparkModel.dao.recmd(id, status);
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
			boolean isUpdate = SparkModel.dao.findById(uid).set("price", new BigDecimal(price)).update();
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
			SparkPicModel model = SparkPicModel.dao.getByBidType(brand);
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
				model.set("b_id", brand).set("p_id", sysFile.getInt("id")).update();
			}else{
				new SparkPicModel().set("b_id", brand).set("p_id", sysFile.getInt("id")).save();
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
		SparkPicModel model = SparkPicModel.dao.getByBidType(brand);
		if(null!=model){
			Integer fid = model.getInt("p_id");
			FileModel sf = FileModel.dao.findById(fid);
			renderFile(new File(sf.getStr("l_path")));
		}else{
			renderNull();
		}
	}
	
	
	public static void main(String[] args) {
		/*String dd = "[CGD0118M0033, 吉利汽车, 帝豪, EC7, EC7, 1.8 手动 GSG版, 2012, 国4, 轿车, 2011, 9, 2011, 2013, 停产, 中国, 国产, 1808;1799, 1.8, 自然吸气, 汽油, 93#, 133, 98, L, 4, 4, 手动, 手动变速器(MT), 5, 通风盘式, 盘式, 机械液压助力, 前置, 前轮驱动, 2650, 四门, 5, 205/55 R16, 205/55 R16, 16英寸, 16英寸, 铝合金, 全尺寸, 有, 无, 无, 有, 无, 有, 有]";
		String substring = dd.substring(1, dd.length()-1);
		System.out.println(substring);*/
		/*String ss = "FGR 7 DQE+(FGR 7 DQ+)";
		int indexOf = ss.indexOf("(");
		System.out.println(ss.substring(0, indexOf));
		System.out.println(ss.substring(indexOf+1, ss.length()-1));*/
		/*String ss = "sss,sss";
		System.out.println(ss.split(",").length);*/
		
		/*String ss = "(504)WR 78 X";
		System.out.println(ss.substring(1, ss.indexOf(")")));
		System.out.println(ss.substring(ss.indexOf(")")+1));*/
		
		/*String ss = "null";
		System.out.println(ss.split(",")[0]);*/
		
		List<String> l1 = new ArrayList<String>();
		String ss1 = ";BKR6E-11";
		String[] sps = ss1.split(";");
		for (String str : sps) {
			if(StrKit.notBlank(str)&&!str.equals("null")){
				l1.add(str);
			}
		}
		
		String sss = ";2756,6465,90888";
		int flag = 0;
		if(StrKit.notBlank(sss)){
			String[] snumbs = sss.split(";");
			System.out.println(snumbs.length);
			for (int k = 0; k < snumbs.length; k++) {
				String[] sn = snumbs[k].split(",");
				for (String ss : sn) {
//					System.out.println("__"+ss);
					if(StrKit.notBlank(ss)&&!ss.equals("null")){
						System.out.println(ss);
						System.out.println(l1.get(k-flag));
						/*SparkModel model = new SparkModel();
						Integer moid = SparkModelModel.dao.getOIDByNewID(models.get(k).getInt("id"));
						model.set("s_id", sid).set("s_number", ss).set("m_id", models.get(k).getInt("id")).set("m_oid", moid);
						products.add(model);*/
					}else{
						flag++;
					}
				}
			}
		}
		System.out.println(flag);
	}
	
	
}
