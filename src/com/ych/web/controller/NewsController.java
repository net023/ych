package com.ych.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.aop.Before;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
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
			String html = getPara("b_content");
			Document doc = Jsoup.parseBodyFragment(html);
			/*System.out.println("1"+doc.baseUri());
			System.out.println("2"+doc.html());
			System.out.println("3"+doc.location());
			System.out.println("4"+doc.nodeName());
			System.out.println("5"+doc.outerHtml());
			System.out.println("6"+doc.ownText());
			System.out.println("7"+doc.text());
			System.out.println("8"+doc.title());
			System.out.println("9"+doc.toString());
			System.out.println("10"+doc.val());*/
			Document doc2 = Jsoup.parseBodyFragment(html);
			Elements imgs = doc.select("img");
			Elements imgs2 = doc2.select("img");
			System.out.println(imgs.size());
			for (Element ele : imgs) {
				ele.attr("src", SysConstants.IMGPRE+ele.attr("src"));
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
			
			for (Element ele : imgs2) {
				if(ele.attr("src").indexOf("emot")==-1){
					if("".equals(ele.attr("width"))){
						ele.attr("width", "80");
					}
					if("".equals(ele.attr("height"))){
						ele.attr("height","80");
					}
				}
			}
			
			new NewsModel().set("n_title", getPara("n_title")).set("n_content", doc.body().html()).set("n_type", getParaToInt("n_type"))
				.set("c_time", new Date()).set("b_content", doc2.body().html()).save();
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
			
			String html = getPara("b_content");
			
			delIMgHandler(qm.getStr("b_content"), html);
			
			Document doc = Jsoup.parseBodyFragment(html);
			Document doc2 = Jsoup.parseBodyFragment(html);
			Elements imgs = doc.select("img");
			Elements imgs2 = doc2.select("img");
			System.out.println(imgs.size());
			for (Element ele : imgs) {
				ele.attr("src", SysConstants.IMGPRE+ele.attr("src"));
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
			for (Element ele : imgs2) {
				if(ele.attr("src").indexOf("emot")==-1){
					if("".equals(ele.attr("width"))){
						ele.attr("width", "80");
					}
					if("".equals(ele.attr("height"))){
						ele.attr("height","80");
					}
				}
			}
			
			qm.set("n_title", getPara("n_title")).set("n_content", doc.body().html()).set("n_type", getParaToInt("n_type")).set("b_content", doc2.body().html()).update();
			
			result.put(RESULT, true);
			result.put(MESSAGE, "news更新成功！");
		} catch (Exception e) {
			LOG.debug("news更新失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "更新失败！");
		}
		renderJson(result);
	}
	
	
	private void delIMgHandler(String old,String now){
		Document old_doc = Jsoup.parseBodyFragment(old);
		Document now_doc = Jsoup.parseBodyFragment(now);
		Elements old_imgs = old_doc.select("img");
		Elements now_imgs = now_doc.select("img");
		List<String> old_src = new ArrayList<String>();
		List<String> now_src = new ArrayList<String>();
		for (Element element : old_imgs) {
			if(element.attr("src").indexOf("emot")==-1){
				String src = element.attr("src");
				old_src.add(src);
			}
		}
		for (Element element : now_imgs) {
			if(element.attr("src").indexOf("emot")==-1){
				String src = element.attr("src");
				now_src.add(src);
			}
		}
		List<String> temp_old_src = new ArrayList<String>(old_src);
		//交集
		old_src.retainAll(now_src);
		if(old_src.size()<temp_old_src.size()){
			//差集
			temp_old_src.removeAll(old_src);
			//删除图片
			String rootRealPath = this.getRequest().getServletContext().getRealPath("/");
			for (String img : temp_old_src) {
				FileKit.delete(new File(rootRealPath,img));
				try {
					//删除微信项目下的图片
					File rootFile = new File(rootRealPath);
					String ych_wx_path = rootFile.getParentFile().getCanonicalPath()+SysConstants.YCH_WX;
					FileKit.delete(new File(ych_wx_path,img));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	@Before(Tx.class)
	public void batchDel() {
		Map<String, Object> result = getResultMap();
		try {
			//删除图片
			String rootRealPath = this.getRequest().getServletContext().getRealPath("/");
			String[] ids = getPara("ids").split("\\|");
			for (String idStr : ids) {
				String content = NewsModel.dao.findById(Integer.valueOf(idStr)).getStr("b_content");
				Document doc = Jsoup.parseBodyFragment(content);
				Elements imgs = doc.select("img");
				for (Element img : imgs) {
					if(img.attr("src").indexOf("emot")==-1){
						String src = img.attr("src");
						FileKit.delete(new File(rootRealPath,src));
						//删除微信项目下的图片
						File rootFile = new File(rootRealPath);
						String ych_wx_path = rootFile.getParentFile().getCanonicalPath()+SysConstants.YCH_WX;
						FileKit.delete(new File(ych_wx_path,src));
					}
				}
			}
			
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
//			System.out.println(getRequest().getServletPath());
//			System.out.println(getRequest().getServletContext().getRealPath("/"));
			String root = getRequest().getServletContext().getRealPath("/");
			File rootFile = new File(root);
//			System.out.println(rootFile.getParent());
//			System.out.println(rootFile.getParentFile().getAbsolutePath());
//			System.out.println(rootFile.getParentFile().getCanonicalPath());
//			System.out.println(rootFile.getParentFile().getName());
//			System.out.println(rootFile.getParentFile().getPath());
			String ych_wx_path = rootFile.getParentFile().getCanonicalPath()+SysConstants.YCH_WX+"/imgs/";
			
			
			
//			System.out.println(getRequest().getServletContext().getRealPath(""));
//			System.out.println(this.getClass().getResource("/"));
//			System.out.println(this.getClass().getClassLoader().getResource("/").getPath());
//			System.out.println(System.getProperty("user.dir"));
			UploadFile upload = getFile("filedata", imgPath, SysConstants.MAX_POST_SIZE);
			String fileName = upload.getOriginalFileName();
			String newFileName = DateTools.format(new Date(), DateTools.yyyyMMddHHmmssSSS) + fileName.substring(fileName.lastIndexOf("."), fileName.length());
			
			FileUtils.copyFile(upload.getFile(), new File(ych_wx_path+newFileName));
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
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
	}
	
}
