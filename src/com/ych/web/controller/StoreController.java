package com.ych.web.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.EncryptionKit;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.BaiDuLBSTool;
import com.ych.tools.SysConstants;
import com.ych.web.model.FileModel;
import com.ych.web.model.MtSerModel;
import com.ych.web.model.ProvinceModel;
import com.ych.web.model.StoreModel;
import com.ych.web.model.StoreMtserModel;
import com.ych.web.model.StorePicModel;

@Control(controllerKey = "/store")
public class StoreController extends BaseController {
	private static Logger LOG = Logger.getLogger(StoreController.class);

	public void index() {
		List<MtSerModel> allMtSers = MtSerModel.dao.getAll();
		setAttr("allMtSers", allMtSers);
		// setAttr("allMtSers", JsonKit.toJson(allMtSers).replace("\"", "'"));
		// setAttr("channels",
		// "[{'id':'','name':'全部'},{'id':1,'name':'上线'},{'id':2,'name':'下线'}]");
		// 省份
		List<ProvinceModel> province = ProvinceModel.dao.getAll();
		List<ProvinceModel> tempProvince = new ArrayList<ProvinceModel>(
				province);
		ProvinceModel defaultProvince = new ProvinceModel();
		defaultProvince.set("id", "").set("name", "选择省份");
		tempProvince.add(0, defaultProvince);
		setAttr("provinces", JsonKit.toJson(tempProvince).replace("\"", "'"));
		// 地级市
		setAttr("cities", "[{'id':'','name':'选择地级市'}]");
		// 区县
		setAttr("counties", "[{'id':'','name':'选择区县'}]");
		render("store/store_index");
	}

	public void list() {
		Pager pager = createPager();
		if (StrKit.notBlank(getPara("storeName"))) {
			pager.addParam("storeName", getPara("storeName"));
		}
		Page<?> page = StoreModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	@Before(Tx.class)
	public void add() {
		Map<String, Object> result = getResultMap();
		try {
			getFile();
			StoreModel store = getModelWithOutModelName(StoreModel.class, true);
			store.set("password", EncryptionKit.md5Encrypt(SysConstants.BACK_DEFAULT_PASSWORD));
			store.set("c_time", new Date()).save();
			// 图片文件id
			String fsStr = getPara("fs");
			if(StrKit.notBlank(fsStr)){
				String[] fs = fsStr.split("\\|");
				for (String fid : fs) {
					StorePicModel storePic = new StorePicModel();
					storePic.set("s_id", store.getInt("id"))
					.set("f_id", Integer.valueOf(fid)).save();
				}
			}
			Map<String, String[]> paraMap = getParaMap();
			// 服务类型
			String[] ms = paraMap.get("ms");
			if(null!=ms){
				for (String mid : ms) {
					StoreMtserModel storeMtser = new StoreMtserModel();
					storeMtser.set("s_id", store.getInt("id"))
					.set("ms_id", Integer.valueOf(mid)).save();
				}
			}
			//{"status":0,"id":945969865,"message":"\u6210\u529f"}
			String json = BaiDuLBSTool.createPoi(store.getStr("name"), "", "", store.getDouble("lat"), store.getDouble("lon"), BaiDuLBSTool.GEOID, BaiDuLBSTool.AK, store.getInt("id"));
			Integer lbsid = JSONObject.parseObject(json).getInteger("id");
			store.set("lbs_id", lbsid).update();
			result.put(RESULT, true);
			result.put(MESSAGE, "Store添加成功！");
		} catch (Exception e) {
			LOG.debug("Store添加失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "添加失败！");
		}
		renderJson(result);
	}

	@Before(Tx.class)
	public void update() {
		Map<String, Object> result = getResultMap();
		try {
			getFile();
			StoreModel store = getModelWithOutModelName(StoreModel.class, true);
			store.update();
			// 删除之前的所有图片关系
			StorePicModel.dao.delBySid(store.getInt("id"));
			// 删除之前的所有服务类型关系
			StoreMtserModel.dao.delBySid(store.getInt("id"));

			// 图片文件id
			String fsStr = getPara("fs");
			if(StrKit.notBlank(fsStr)){
				String[] fs = fsStr.split("\\|");
				for (String fid : fs) {
					StorePicModel storePic = new StorePicModel();
					storePic.set("s_id", store.getInt("id"))
					.set("f_id", Integer.valueOf(fid)).save();
				}
			}
			Map<String, String[]> paraMap = getParaMap();
			// 服务类型
			String[] ms = paraMap.get("ms");
			if(null!=ms){
				for (String mid : ms) {
					StoreMtserModel storeMtser = new StoreMtserModel();
					storeMtser.set("s_id", store.getInt("id"))
					.set("ms_id", Integer.valueOf(mid)).save();
				}
			}
			
			Integer lbsid = StoreModel.dao.findById(store.getInt("id")).getInt("lbs_id");
			String updatePoi = BaiDuLBSTool.updatePoi(BaiDuLBSTool.GEOID, BaiDuLBSTool.AK, store.getStr("name"), "", "", store.getDouble("lat"), store.getDouble("lon"), lbsid);
			System.out.println(updatePoi);
			
			result.put(RESULT, true);
			result.put(MESSAGE, "Store更新成功！");
		} catch (Exception e) {
			LOG.debug("Store更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "Store更新失败！");
		}
		renderJson(result);
	}
	
	@Before(Tx.class)
	public void updateBackUser(){
		Map<String, Object> result = getResultMap();
		try {
			String username = getPara("username");
			String password = getPara("password");
			Integer uid = getParaToInt("id");
			boolean isUpdate = StoreModel.dao.findById(uid).set("username", username).set("password", EncryptionKit.md5Encrypt(password)).update();
			if(isUpdate){
				result.put(RESULT, true);
				result.put(MESSAGE, "门店后台账户更新成功！");
			}else{
				result.put(RESULT, false);
				result.put(MESSAGE, "门店后台账户更新失败！");
			}
		} catch (Exception e) {
			LOG.debug("门店后台账户更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "门店后台账户更新失败！");
		}
		renderJson(result);
	}
	

	@Before(Tx.class)
	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			String[] ids = getPara("ids").split("\\|");
			// 删除图片关系及图片文件
			for (String id : ids) {
				Integer s_id = Integer.valueOf(id);
				List<StorePicModel> storePics = StorePicModel.dao.getBySid(s_id);
				for (StorePicModel storePicModel : storePics) {
					FileModel fileModel = FileModel.dao.findById(storePicModel.getInt("f_id"));
					if(null!=fileModel){
						String path = fileModel.getStr("l_path");
						if(StrKit.notBlank(path)){
							FileKit.delete(new File(path));
						}
						fileModel.delete();
						storePicModel.delete();
					}
				}
			}
			// 删除服务类型关系
			for (String id : ids) {
				Integer s_id = Integer.valueOf(id);
				StoreMtserModel.dao.delBySid(s_id);
				String deletePoi = BaiDuLBSTool.deletePoi(BaiDuLBSTool.GEOID, BaiDuLBSTool.AK, StoreModel.dao.findById(s_id).getInt("lbs_id"));
				System.out.println(deletePoi);
			}

			StoreModel.dao.batchDel(getPara("ids"));
			result.put(RESULT, true);
			result.put(MESSAGE, "Store批量删除成功！");
		} catch (Exception e) {
			LOG.debug("Store批量删除失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "Store批量删除失败！");
		}
		renderJson(result);
	}
	
	@Before(Tx.class)
	public void modify(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = StoreModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	
	public void getAll(){
		List<StoreModel> data = StoreModel.dao.getAll2();
		renderJson(data);
	}
	

}
