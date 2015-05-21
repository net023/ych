package com.ych.web.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.ych.base.common.BaseController;
import com.ych.base.common.Pager;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.DateTools;
import com.ych.tools.SysConstants;
import com.ych.web.model.NewsClassModel;
import com.ych.web.model.NewsModel;

@Control(controllerKey = "/news")
public class NewsController extends BaseController {

	private static final Logger LOG = Logger.getLogger(NewsController.class);

	public void index() {
		List<NewsClassModel> list = NewsClassModel.dao.getAll();
		List<NewsClassModel> tempNewsClass = new ArrayList<NewsClassModel>(list);
		NewsClassModel cdf = new NewsClassModel();
		cdf.set("id", "");
		cdf.set("t_name", "全部");
		tempNewsClass.add(0, cdf);

		setAttr("newsClass1", JsonKit.toJson(tempNewsClass).replace("\"", "'"));
		setAttr("newsClass2", JsonKit.toJson(list).replace("\"", "'"));
		render("news/news_index");
	}

	public void list() {
		Pager pager = createPager();
		pager.addParam("n_title", getPara("n_title"));
		 pager.addParam("n_type", getPara("n_type"));
		Page<NewsModel> page = NewsModel.dao.page(pager);
		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	@Before(Tx.class)
	public void add() {
		Map<String, Object> result = getResultMap();
		try {
			String html = getPara("n_content");
			Document doc = Jsoup.parseBodyFragment(html);
			Elements imgs = doc.select("img");
			System.out.println(imgs.size());
			for (Element ele : imgs) {
				if(ele.attr("src").indexOf("emot")==-1){
					if("".equals(ele.attr("width"))){
						ele.attr("width", "80");
					}
					if("".equals(ele.attr("height"))){
						ele.attr("height","80");
					}
					System.out.println(ele.attr("width")+"----------"+ele.attr("height"));
				}
			}
			
			new NewsModel().set("n_title", getPara("n_title")).set("n_content", doc.body().html()).set("n_type", getParaToInt("n_type"))
				.set("c_time", new Date()).save();
			result.put(RESULT, true);
			result.put(MESSAGE, "news添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("news添加失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "添加失败！");
		}
		renderJson(result);
	}

	@Before(Tx.class)
	public void update() {
		Map<String, Object> result = getResultMap();
		try {
			Integer id = getParaToInt("id");
			NewsModel qm = NewsModel.dao.findById(id);
			
			String html = getPara("n_content");
			Document doc = Jsoup.parseBodyFragment(html);
			Elements imgs = doc.select("img");
			System.out.println(imgs.size());
			for (Element ele : imgs) {
				if(ele.attr("src").indexOf("emot")==-1){
					if("".equals(ele.attr("width"))){
						ele.attr("width", "80");
					}
					if("".equals(ele.attr("height"))){
						ele.attr("height","80");
					}
					System.out.println(ele.attr("width")+"----------"+ele.attr("height"));
				}
			}
			
			qm.set("n_title", getPara("n_title")).set("n_content", doc.body().html()).set("n_type", getParaToInt("n_type")).update();
			
			result.put(RESULT, true);
			result.put(MESSAGE, "news更新成功！");
		} catch (Exception e) {
			LOG.debug("news更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "更新失败！");
		}
		renderJson(result);
	}

	@Before(Tx.class)
	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			NewsModel.dao.batchDel(getPara("ids"));
			result.put(RESULT, true);
			result.put(MESSAGE, "news批量删除成功！");
		} catch (Exception e) {
			LOG.debug("news批量删除失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "批量删除失败！");
		}
		renderJson(result);
	}
	
	@Before(Tx.class)
	public void modify(){
		Integer id = getParaToInt("id");
		Integer status = getParaToInt("state");
		boolean b = NewsModel.dao.modify(id, status);
		Map<String, Object> result = getResultMap();
		result.put(RESULT, b);
		result.put(MESSAGE, b ? "保存成功" : "保存失败，请联系管理员！");
		renderJson(result);
	}
	
	
	public void upimg() {
		Map<String, Object> result = getResultMap();
		try {
			//imgs
			String imgPath = this.getRequest().getServletContext().getRealPath("/imgs");
			UploadFile upload = getFile("filedata", imgPath, SysConstants.MAX_POST_SIZE);
			String fileName = upload.getOriginalFileName();
			String newFileName = DateTools.format(new Date(), DateTools.yyyyMMddHHmmssSSS) + fileName.substring(fileName.lastIndexOf("."), fileName.length());
			upload.getFile().renameTo(new File(imgPath+ File.separator + newFileName));
			
			result.put("err", "");
			result.put("msg", "imgs/"+newFileName);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("上传失败！" + e.getMessage());
			result.put("err", "上传失败");
			result.put("msg", "");
		}
		//renderJson(result);
		String json = JsonKit.toJson(result);
		//renderJavascript("<script>parent.notice("+json+");</script>");
		renderHtml("<script>parent.notice("+json+");</script>");
	}
	
	
	public void upimgByIframe(){
		render("news/uploadgui");
	}
	
}
