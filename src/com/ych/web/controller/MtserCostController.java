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
import com.ych.web.model.MtserCostModel;

@Control(controllerKey = "/mtser_cost")
public class MtserCostController extends BaseController {
	private static Logger LOG = Logger.getLogger(MtserCostController.class);

	public void index() {
		List<MtSerModel> allFirsts = MtSerModel.dao.getAllFirsts();
		List<MtSerModel> tempFirsts = new ArrayList<MtSerModel>(allFirsts);

		MtSerModel defaultMtSer = new MtSerModel();
		defaultMtSer.set("id", "").set("name", "选择一级类目");

		tempFirsts.add(0, defaultMtSer);
		setAttr("firstCategories", JsonKit.toJson(tempFirsts)
				.replace("\"", "'"));

		setAttr("secondCategories", "[{'id':'','name':'选择二级类目'}]");
		render("mtser_cost/mtser_cost_index");
	}

	public void list() {
		Pager pager = createPager();
		Page<?> page = MtserCostModel.dao.getPager(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	public void addOrUpdate() {
		Map<String, Object> result = getResultMap();
		MtserCostModel model = null;		
		try {
			String firstCategory = getPara("firstCategory");
			String secondCategory = getPara("secondCategory");
			String unitCost = getPara("unitCost");
			if (StrKit.isBlank(firstCategory) && StrKit.isBlank(secondCategory)) {
				result.put(RESULT, false);
				result.put(MESSAGE, "请选择保养类别");
				renderJson(result);
				return;
			}
			if (StrKit.isBlank(unitCost)) {
				result.put(RESULT, false);
				result.put(MESSAGE, "工时费不能为空");
				renderJson(result);
				return;
			}

			// 取数据库中记录
			model = MtserCostModel.dao.getByMsId(Integer
					.parseInt(StrKit.notBlank(secondCategory) ? secondCategory
							: firstCategory));
			
			if (model == null) {
				model = new MtserCostModel();
				model.set("ms_id", Integer.parseInt(StrKit.notBlank(secondCategory) ? secondCategory
						: firstCategory));
				model.set("unit_cost", unitCost);
				model.save();
				result.put(RESULT, true);
				result.put(MESSAGE, "MtserCost添加成功！");
			} else {
				model.set("unit_cost", unitCost);
				model.update();
				result.put(RESULT, true);
				result.put(MESSAGE, "MtserCost修改成功");
			}
		} catch (Exception e) {
			if (model == null) {
				LOG.debug("MtserCost添加失败！" + e.getMessage());
				result.put(RESULT, false);
				result.put(MESSAGE, "添加失败！");
			} else {
				LOG.debug("MtserCost添加失败！" + e.getMessage());
				result.put(RESULT, false);
				result.put(MESSAGE, "添加失败！");
			}
		}
		renderJson(result);
	}
	
	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			MtserCostModel.dao.batchDel(getPara("ids"));
			result.put(RESULT, true);
			result.put(MESSAGE, "MtserCost批量删除成功！");
		} catch (Exception e) {
			LOG.debug("MtserBrand批量删除失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "MtserCost批量删除失败！");
		}
		renderJson(result);
	}
}
