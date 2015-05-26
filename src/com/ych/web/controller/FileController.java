package com.ych.web.controller;

import java.io.File;
import java.util.Date;
import java.util.Map;

import com.jfinal.kit.FileKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;
import com.ych.base.common.BaseController;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.DateTools;
import com.ych.tools.SysConstants;
import com.ych.web.model.FileModel;

@Control(controllerKey="/file")
public class FileController extends BaseController {
	private static Logger LOG = Logger.getLogger(FileController.class);
	
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
			result.put("fID", sysFile.getInt("id"));
			result.put(RESULT, true);
			result.put(MESSAGE, "上传成功！");
		} catch (Exception e) {
			LOG.debug("文件上传失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "上传失败！");
		}
		//renderJson(result);
		render(new JsonRender(result).forIE());
	}
	
	public void download(){
		//通过 fID查找图片文件路径 返回
		Integer fid = getParaToInt("fID");
		FileModel sf = FileModel.dao.findById(fid);
		renderFile(new File(sf.getStr("l_path")));
	}
	
	public void delete(){
		Map<String, Object> result = getResultMap();
		try {
			Integer fid = getParaToInt("fID");
			FileModel sf = FileModel.dao.findById(fid);
			String lPaht = sf.getStr("l_path");
			if(StrKit.notBlank(lPaht)){
				FileKit.delete(new File(lPaht));
			}
			sf.delete();
			result.put(RESULT, true);
			result.put(MESSAGE, "删除成功！");
		} catch (Exception e) {
			LOG.debug("文件删除失败！" + e.getMessage());
			result.put(RESULT, false);
			result.put(MESSAGE, "删除失败！");
		}
		render(new JsonRender(result).forIE());
	}
	
}
