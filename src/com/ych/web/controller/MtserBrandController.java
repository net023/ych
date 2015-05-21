package com.ych.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.MtSerModel;
import com.ych.web.model.MtserBrandModel;

@Control(controllerKey = "/mtser_brand")
public class MtserBrandController extends BaseController {
	private static Logger LOG = Logger.getLogger(MtserBrandController.class);

	public void index() {
		List<MtSerModel> allFirsts = MtSerModel.dao.getAllFirsts();
		List<MtSerModel> tempFirsts = new ArrayList<MtSerModel>(allFirsts);

		MtSerModel defaultMtSer = new MtSerModel();
		defaultMtSer.set("id", "").set("name", "选择一级类目");

		tempFirsts.add(0, defaultMtSer);
		setAttr("firstCategories", JsonKit.toJson(tempFirsts)
				.replace("\"", "'"));

		setAttr("secondCategories", "[{'id':'','name':'选择二级类目'}]");
		render("mtser_brand/mtser_brand_index");
	}

	public void list() {
		Pager pager = createPager();
		Page<?> page = MtserBrandModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	public void add() {
		Map<String, Object> result = getResultMap();
		try {
			String firstCategory = getPara("firstCategory");
			String secondCategory = getPara("secondCategory");
			String brandFilter = getPara("brandFilter");
			if (StrKit.isBlank(firstCategory) && StrKit.isBlank(secondCategory)) {
				result.put(RESULT, false);
				result.put(MESSAGE, "请选择保养类别");
				renderJson(result);
				return;
			}
			if (StrKit.isBlank(brandFilter)) {
				result.put(RESULT, false);
				result.put(MESSAGE, "过滤品牌不能为空");
				renderJson(result);
				return;
			}
			if (MtserBrandModel.dao
					.hasFilter(
							Integer.parseInt(StrKit.notBlank(secondCategory) ? secondCategory
									: firstCategory), brandFilter)) {
				result.put(RESULT, false);
				result.put(MESSAGE, "您所提交的过滤品牌已添加过");
				renderJson(result);
				return;
			}
			MtserBrandModel.dao
					.batchAdd(
							Integer.parseInt(StrKit.notBlank(secondCategory) ? secondCategory
									: firstCategory), brandFilter);
			result.put(RESULT, true);
			result.put(MESSAGE, "MtserBrand添加成功！");
		} catch (Exception e) {
			LOG.debug("MtserBrand添加失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "添加失败！");
		}
		renderJson(result);
	}

	public void update() {
		Map<String, Object> result = getResultMap();
		try {
			Integer msId = getParaToInt("id");
			String filter = getPara("filter");
			MtserBrandModel.dao.batchUpdate(msId, filter);
			result.put(RESULT, true);
			result.put(MESSAGE, "MtserBrand更新成功！");
		} catch (Exception e) {
			LOG.debug("Store更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "MtserBrand更新失败！");
		}
		renderJson(result);
	}

	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			MtserBrandModel.dao.batchDel(getPara("ids"));
			result.put(RESULT, true);
			result.put(MESSAGE, "MtserBrand批量删除成功！");
		} catch (Exception e) {
			LOG.debug("MtserBrand批量删除失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "MtserBrand批量删除失败！");
		}
		renderJson(result);
	}
}
