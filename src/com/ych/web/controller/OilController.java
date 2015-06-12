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
import com.ych.web.model.OilBrandModel;
import com.ych.web.model.OilEtalonModel;
import com.ych.web.model.OilModel;
import com.ych.web.model.OilModelModel;
import com.ych.web.model.OilPicModel;
import com.ych.web.model.OilPriceModel;

@Control(controllerKey = "/oil")
public class OilController extends BaseController {
	private static Logger LOG = Logger.getLogger(OilController.class);

	public void index() {
		try {
			setAttr("brands", ModelTools.putFirstDataRender("id", "name", "", "全部", OilBrandModel.dao.getAll()));
			setAttr("pinpai", JsonKit.toJson(OilBrandModel.dao.getAll()).replace("\"", "'"));
			setAttr("etalon", ModelTools.putFirstDataRender("id", "name", "", "全部", OilEtalonModel.dao.getAll()));
			setAttr("models", ModelTools.putFirstDataRender("id", "name", "", "全部", OilModelModel.dao.getAll()));
		} catch (Exception e) {
			LOG.debug("oil_index 失败!" + e.getMessage());
		}
		render("oil/oil_index");
	}
	
	public void priceList() {
		Pager pager = createPager();
		pager.addParam("brand", getParaToInt("brand"));
		pager.addParam("type", getParaToInt("type"));
		pager.addParam("etalon", getParaToInt("etalon"));
		pager.addParam("model", getParaToInt("model"));
		pager.addParam("status", getParaToInt("status"));
		pager.addParam("recmd", getParaToInt("recmd"));
		
		Page<?> page = OilPriceModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	
	public void list(){
		Pager pager = createPager();
		pager.addParam("brand", getParaToInt("brand"));
		pager.addParam("type", getParaToInt("type"));
		pager.addParam("etalon", getParaToInt("etalon"));
		pager.addParam("model", getParaToInt("model"));
		
		if(StrKit.notBlank(getPara("lyid"))){
			pager.addParam("lyid", getPara("lyid").trim());
		}
		Page<?> page = OilModel.dao.getPager(pager);
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
			XxlsPrint howto = new XxlsPrint();
			howto.processOneSheet(absolutePath, 1);
			List<List> data = howto.getMsg();
			List<String> row = null;
			for (int i = 2; i < data.size(); i++) {
				row = data.get(i);
				if(row.size()<=2){continue;}
				String lyid = null;
				Double fill = null;
				Integer type = null;
				//规格
				List<OilEtalonModel> etalons = new ArrayList<OilEtalonModel>();
				System.out.println(row);
				for (int j = 0; j < row.size(); j++) {
					switch(j){
					case 0:
						//保存力洋ID
						lyid = row.get(j);
						continue;
					case 1:
						//保存加注量
						String fillStr = row.get(j);
						String sf = null;
						if(StrKit.notBlank(fillStr)){
							//4.7(BJH);4.5(BWG)
							if(fillStr.indexOf(";")!=-1){
								String ss = fillStr.split(";")[0];
								//4.0(4JB1TC)
								if(ss.indexOf("(")!=-1){
									sf = ss.substring(0, ss.indexOf("("));
								}else{
									sf = ss;
								}
							}else{
								//3.5-4.0(4JB1TC)  4.0(4JB1TC)  3.0(DA462-1A)
								int index = fillStr.indexOf("-");
								
								int index2 = fillStr.indexOf("(");
								if(index<index2){//index -1
									//3.5-4.0(4JB1TC)  4.0(4JB1TC)
									if(index!=-1){
										String sbStr = fillStr.substring(index+1);
										if(sbStr.indexOf("(")!=-1){
											sf = sbStr.substring(0, sbStr.indexOf("("));
										}else{
											sf = sbStr;
										}
									}else{
										if(fillStr.indexOf("(")!=-1){
											sf = fillStr.substring(0, fillStr.indexOf("("));
										}else{
											sf = fillStr;
										}
									}
								}else if(index>index2){//index2 -1
									//3.0(DA462-1A)  3.0-3.5
									if(index2!=-1){
										sf = fillStr.substring(0, fillStr.indexOf("("));
									}else{
										sf = fillStr.substring(index+1);
									}
								}else{
									sf = fillStr;
								}
								
								
							}
						}
						if(StrKit.notBlank(sf)){
							fill = Double.valueOf(sf);
						}
						continue;
					case 2:
						//保存基础油类型
						String typeStr = row.get(j);
						if("全合成".equals(typeStr)){
							type=1;
						}else if("半合成".equals(typeStr)){
							type=2;
						}else{
							type=3;
						}
						continue;
					case 3:continue;
					case 4:
						//保存规格
						dataHandler(row.get(j),etalons);
						continue;
					}
				}
				for (OilEtalonModel oile : etalons) {
					//机油
					OilModel model = new OilModel().set("ly_id", lyid).set("fill", fill).set("type", type).set("e_id", oile.getInt("id"));
					model.save();
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
	
	private void dataHandler(String etalon, List<OilEtalonModel> etalons) {
		//5W-20;5W-30
		String[] es = etalon.split(";");
		for (String e : es) {
			//5W-30()
			if(e.indexOf("(")!=-1){
				e = e.substring(0, e.indexOf("("));
			}
			OilEtalonModel model = OilEtalonModel.dao.getModelByName(e);
			if(null==model){
				model=new OilEtalonModel();
				model.set("name", e).save();
			}
			etalons.add(model);
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
					boolean isUP = OilModel.dao.updatePriceByName(price, productNum);
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
		boolean b = OilPriceModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	@Before(Tx.class)
	public void recmd(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = OilPriceModel.dao.recmd(id, status);
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
			boolean isUpdate = OilPriceModel.dao.findById(uid).set("price", new BigDecimal(price)).update();
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
			Integer type = getParaToInt("type");
			Integer mm = getParaToInt("model");
			Integer eid = getParaToInt("eid");
			OilPicModel model = OilPicModel.dao.getByBidType(brand, type,mm,eid);
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
				model.set("b_id", brand).set("type", type).set("p_id", sysFile.getInt("id")).set("m_id", mm).set("e_id", eid).update();
			}else{
				new OilPicModel().set("b_id", brand).set("type", type).set("p_id", sysFile.getInt("id")).set("m_id", mm).set("e_id", eid).save();
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
		Integer mm = getParaToInt("model");
		Integer eid = getParaToInt("eid");
		OilPicModel model = OilPicModel.dao.getByBidType(brand, type,mm,eid);
		if(null!=model){
			Integer fid = model.getInt("p_id");
			FileModel sf = FileModel.dao.findById(fid);
			renderFile(new File(sf.getStr("l_path")));
		}else{
			renderNull();
		}
	}
	
	public void getAllModel(){
		renderJson(OilModelModel.dao.getAllByBid(getParaToInt("bid")));
	}
	
	public void getAllEtalon(){
		Integer bid = getParaToInt("bid");
		Integer mid = getParaToInt("mid");
		List<OilEtalonModel> list = OilEtalonModel.dao.getMOdelByBidMid(bid, mid);
		renderJson(list);
	}
	
	
	public static void main(String[] args) throws Exception {
		/*String str = "%E4%B8%8A%E4%BC%A0%E7%BB%93%E6%9E%9C_14338440626701421417820739167.txt";
		String decode = URLDecoder.decode(str, "UTF-8");
		System.out.println(decode);*/
		/*
		for (int i = 0; i < 5; i++) {
			switch(i){
			case 1:System.out.println(i);
			case 2:System.out.println(i);continue;
			case 3:System.out.println(i);continue;
			case 4:System.out.println(i);continue;
			}
		}*/
		
		String s = "3.5-4.0(4JB1TC)";
		System.out.println(s.substring(s.indexOf("-")+1));
		s = "4.0(4JB1TC)";
		System.out.println(s.substring(0, s.indexOf("(")));
		
	}
	
	
}
