package com.ych.web.controller;

import java.io.File;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.CarouselModel;
import com.ych.web.model.FileModel;
import com.ych.web.model.NewsModel;

@Control(controllerKey = "/carousel")
public class CarouselController extends BaseController {

	private static final Logger LOG = Logger.getLogger(CarouselController.class);

	public void index() {
		setAttr("n_titles", JsonKit.toJson(NewsModel.dao.getAll_id_title()).replace("\"", "'"));
		render("carousel/carousel_index");
	}

	public void list() {
		Pager pager = createPager();
		Page<CarouselModel> page = CarouselModel.dao.page(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	@Before(Tx.class)
	public void add() {
		Map<String, Object> result = getResultMap();
		try {
			CarouselModel Top = getModelWithOutModelName(CarouselModel.class, true);
			Top.save();
			result.put(RESULT, true);
			result.put(MESSAGE, "Carousel添加成功！");
		} catch (Exception e) {
			LOG.debug("Carousel添加失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "Carousel添加失败！");
		}
		renderJson(result);
	}

	@Before(Tx.class)
	public void update() {
		Map<String, Object> result = getResultMap();
		try {
			CarouselModel Top = getModelWithOutModelName(CarouselModel.class, true);
			Top.update();
			result.put(RESULT, true);
			result.put(MESSAGE, "Carousel更新成功！");
		} catch (Exception e) {
			LOG.debug("Carousel更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "Carousel更新失败！");
		}
		renderJson(result);
	}

	@Before(Tx.class)
	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			// 删除图片
			String[] ids = getPara("ids").split("\\|");
			for (String idStr : ids) {
				Integer id = Integer.valueOf(idStr);
				CarouselModel carouselModel = CarouselModel.dao.findById(id);
				if(null!=carouselModel){
					FileModel fileModel = FileModel.dao.findById(carouselModel.getInt("f_id"));
					if(null!=fileModel){
						String path = fileModel.getStr("l_path");
						if(StrKit.notBlank(path)){
							FileKit.delete(new File(path));
						}
						fileModel.delete();
					}
				}
			}
			
			CarouselModel.dao.batchDel(getPara("ids"));
			result.put(RESULT, true);
			result.put(MESSAGE, "Carousel批量删除成功！");
		} catch (Exception e) {
			LOG.debug("Carousel批量删除失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "Carousel批量删除失败！");
		}
		renderJson(result);
	}

	public void modify() {
		Integer id = getParaToInt("id");
		Integer state = getParaToInt("state");
		boolean b = CarouselModel.dao.modify(id, state);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
}
