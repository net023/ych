package com.ych.web.model;

import com.jfinal.plugin.activerecord.Model;
import com.ych.core.plugin.annotation.Table;

@Table(tableName = "file")
public class FileModel extends Model<FileModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final FileModel dao = new FileModel();

}
