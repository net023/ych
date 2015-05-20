package com.ych.base.controller;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.kit.EncryptionKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ych.base.common.BaseController;
import com.ych.base.common.DisplayTool;
import com.ych.base.common.Pager;
import com.ych.base.model.SysRolesModel;
import com.ych.base.model.SysUserModel;
import com.ych.base.model.SysUserPostModel;
import com.ych.base.model.SysUserRoleModel;
import com.ych.core.plugin.annotation.Control;
import com.ych.tools.SysConstants;

/**
 * 用户
 * 
 *
 */
@Control(controllerKey = "/user")
public class UserController extends BaseController {

	private static final Logger LOG = Logger.getLogger(UserController.class);

	public void index() {
//		setAttr("posts", SysPostsModel.dao.allPosts());
		setAttr("rolesJson", JsonKit.toJson(SysRolesModel.dao.allRoles()));
		render("tissue/user_mgr");
	}
	public void list() {
		SysUserModel user = getUser();
		Pager pager = createPager();

		pager.addParam("username", getPara("username"));
		pager.addParam("status", getPara("status"));
		pager.addParam("roleID", getPara("roleID"));
		pager.addParam("nick", getPara("nick"));
		pager.addParam("role", user.get("roleID"));

		Page<?> page = SysUserModel.dao.page(pager);

		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}

	public void roleChecked() {
		Boolean checked = getParaToBoolean("checked");
		SysUserRoleModel userRole = getModelWithOutModelName(SysUserRoleModel.class, true);
		if (checked != null && userRole.get("roleID") != null && userRole.get("userID") != null) {
			setAttr(RESULT, true);
			SysUserRoleModel.dao.changeUserRole(userRole, checked);
			setAttr(MESSAGE, "修改成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "参数错误，获取数据失败！");
		}
		renderJson();
	}

	@Before(Tx.class)
	public void add() {
		SysUserModel user = getModelWithOutModelName(SysUserModel.class, true);
		user.set("password", EncryptionKit.md5Encrypt(SysConstants.DEFAULT_PASSWORD));
		user.set("createDate", new Date());
		user.save();

//		modifyAuth(user);

		setAttr(RESULT, true);
		setAttr(MESSAGE, "新增成功！");
		setAttr("id", user.get("id"));
		DisplayTool.removeCache(DisplayTool.USER_KEY);
		renderJson();
	}
	@Before(Tx.class)
	public void update() {
		SysUserModel user = getModelWithOutModelName(SysUserModel.class, true);
		if (user.get("id") == null) {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "更新失败！");
			renderJson();
			return;
		}
		user.set("updateDate", new Date());
		user.update();

		SysUserModel _user = getUser();
		if (user.get("id").equals(_user.get("id"))) {//更新session
			_user.set("nick", user.get("nick"));
			_user.set("email", user.get("email"));
		}

//		modifyAuth(user);

		setAttr(RESULT, true);
		setAttr(MESSAGE, "更新成功！");
		DisplayTool.removeCache(DisplayTool.USER_KEY);
		renderJson();
	}

	/**
	 * 修改用户权限关联 当前权限仅由角色控制 且单用户单角色模式
	 * 
	 * 2014-11-19 弃用，用户编辑时不影响权限，权限单独分配
	 * 
	 * @Author Sean 2014-11-17 上午11:40:24
	 * @param user
	 */
	@Deprecated
	private void modifyAuth(SysUserModel user) {
		String roleID = getPara("roleID");
		String postID = getPara("postID");

		// 分配角色
		if (StrKit.notBlank(roleID)) {
			SysUserRoleModel.dao.deleteByUser(user.get("id"));
			SysUserRoleModel userRole = new SysUserRoleModel();
			userRole.set("roleID", roleID);
			userRole.set("userID", user.get("id"));
			userRole.save();
		}
		// 分配岗位
		if (StrKit.notBlank(postID)) {
			SysUserPostModel.dao.deleteByUser(user.get("id"));
			SysUserPostModel userPost = new SysUserPostModel();
			userPost.set("postID", postID);
			userPost.set("userID", user.get("id"));
			userPost.save();
		}
	}
	public void del() {
		Integer id = getParaToInt("id");
		if (id != null && SysUserModel.dao.deleteById(id)) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "删除成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "删除失败！");
		}
		renderJson();
	}
	public void userMessage() {
		setAttr("user", SysUserModel.dao.findById(getUser().get("id")));
		render("tissue/user_message");
	}
	public void resetPassword() {
		SysUserModel user = new SysUserModel();
		String id = getPara("id");
		user.set("id", id);
		user.set("password", EncryptionKit.md5Encrypt(SysConstants.DEFAULT_PASSWORD));
		if (StrKit.notBlank(id) && user.update()) {
			setAttr(RESULT, true);
			setAttr(MESSAGE, "密码重置成功！");
		} else {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "密码重置失败！");
		}
		renderJson();
	}

	public void password() {
		String oldPassword = getPara("oldPassword");
		String newPassword = getPara("newPassword");
		String entPassword = getPara("entPassword");

		boolean isError = true;
		if (StrKit.isBlank(oldPassword)) {
			setAttr(ERROR, "旧密码不能为空！");
		} else if (StrKit.isBlank(newPassword)) {
			setAttr(ERROR, "新密码不能为空！");
		} else if (StrKit.isBlank(entPassword)) {
			setAttr(ERROR, "确认密码不能为空！");
		} else if (!newPassword.equals(entPassword)) {
			setAttr(ERROR, "两次密码不相同！");
		} else {
			isError = false;
		}

		if (isError) {
			renderJson();
			return;
		}

		SysUserModel user = getUser();
		String oldPass = EncryptionKit.md5Encrypt(oldPassword);
		if (!user.getStr("password").equals(oldPass)) {
			setAttr(ERROR, "旧密码错误！");
		} else {
			user.set("password", EncryptionKit.md5Encrypt(newPassword));
			if (user.update()) {
				setAttr(RESULT, true);
				setAttr(MESSAGE, "密码修改成功！");
			} else {
				setAttr(RESULT, false);
				setAttr(ERROR, "密码修改失败！");
			}
		}
		renderJson();
	}
	
	
	public void batch_del() {
		String ids = getPara("userIds");
		try {
			String[] arrIds = ids.split("\\|");
			for (String id : arrIds) {
				if ("".equals(id))
					continue;
				SysUserModel.dao.deleteById(id);
			}
			setAttr(RESULT, true);
			setAttr(MESSAGE, "删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			setAttr(RESULT, false);
			setAttr(MESSAGE, "删除失败！");
		}
		renderJson();
	}

}
