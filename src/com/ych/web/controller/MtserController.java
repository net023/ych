package com.ych.web.controller;

import java.util.List;

import com.jfinal.kit.JsonKit;
import com.jfinal.log.Logger;
import com.ych.base.common.BaseController;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.MtSerModel;

@Control(controllerKey = "/mtser")
public class MtserController extends BaseController {
	private static Logger LOG = Logger.getLogger(MtserController.class);

	public void index() {
		List<MtSerModel> allMtsers = MtSerModel.dao.getAllTreeItems();
		setAttr("mtsersJson", JsonKit.toJson(allMtsers));
		render("mtser/mtser_index");
	}

	public void getAllFirsts() {
		renderJson(MtSerModel.dao.getAllFirsts());
	}

	public void getAllSecondsByFirst() {
		List<MtSerModel> mtsers = MtSerModel.dao
				.getAllSecondCategoriesByParent(getParaToInt("pid"));
		MtSerModel mtser = new MtSerModel();
		mtser.set("id", "").set("name", "选择二级类目");
		mtsers.add(0, mtser);
		renderJson(mtsers);
	}

	public void add() {
		MtSerModel mtser = getModelWithOutModelName(MtSerModel.class, true);
		if (mtser.save()) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "新增成功！");
			setAttr("id", mtser.get("id"));
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "新增失败！");
		}
		renderJson();
	}

	public void update() {
		MtSerModel mtser = getModelWithOutModelName(MtSerModel.class, true);
		if (mtser.get("id") != null && mtser.update()) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "更新成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "更新失败！");
		}
		renderJson();
	}

	public void del() {
		Integer id = getParaToInt("id");
		if (id != null && MtSerModel.dao.deleteByIdCascade(id)) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "删除成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "删除失败！");
		}
		renderJson();
	}

}
