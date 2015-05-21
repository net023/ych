package com.ych.web.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.aop.Before;
import com.jfinal.kit.FileKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.web.model.NewsClassModel;
import com.ych.web.model.NewsModel;

@Control(controllerKey = "/newsClass")
public class NewsClassController extends BaseController {

	private static final Logger LOG = Logger.getLogger(NewsClassController.class);

	public void index() {
		render("newsClass/newsClass_index");
	}

	public void list() {
		Pager pager = createPager();
		Page<NewsClassModel> page = NewsClassModel.dao.page(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	@Before(Tx.class)
	public void add() {
		Map<String, Object> result = getResultMap();
		try {
			NewsClassModel newsClass = getModelWithOutModelName(NewsClassModel.class, true);
			newsClass.save();
			result.put(RESULT, true);
			result.put(MESSAGE, "添加成功！");
		} catch (Exception e) {
			LOG.debug("添加失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "添加失败！");
		}
		renderJson(result);
	}

	@Before(Tx.class)
	public void update() {
		Map<String, Object> result = getResultMap();
		try {
			NewsClassModel newsClass = getModelWithOutModelName(NewsClassModel.class, true);
			newsClass.update();
			result.put(RESULT, true);
			result.put(MESSAGE, "更新成功！");
		} catch (Exception e) {
			LOG.debug("更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "更新失败！");
		}
		renderJson(result);
	}


	@Before(Tx.class)
	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			//删除文章中的图片
			List<NewsModel> newModes = NewsModel.dao.getByType(getPara("ids"));
			String rootRealPath = this.getRequest().getServletContext().getRealPath("/");
			for (NewsModel newsModel : newModes) {
				String content = newsModel.getStr("n_content");
				Document doc = Jsoup.parseBodyFragment(content);
				Elements imgs = doc.select("img");
				for (Element img : imgs) {
					if(img.attr("src").indexOf("emot")==-1){
						String src = img.attr("src");
						FileKit.delete(new File(rootRealPath,src));
					}
				}
			}
			
			NewsModel.dao.delByType(getPara("ids"));
			NewsClassModel.dao.batchDel(getPara("ids"));
			result.put(RESULT, true);
			result.put(MESSAGE, "批量删除成功！");
		} catch (Exception e) {
			LOG.debug("批量删除失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "批量删除失败！");
		}
		renderJson(result);
	}
}
