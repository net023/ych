package com.ych.tools.gennercode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.beetl.core.BeetlKit;

@SuppressWarnings("all")
public class SwingGenneratorCode {

	private static String BASEPACKAGE = "";
	private static Connection CONNECTION = null;

	private static ArrayList<List> readConst(String ddl, Connection connection) throws Exception {
		PreparedStatement statement = connection.prepareStatement(ddl);
		ResultSet set = statement.executeQuery();

		ResultSetMetaData metaData = set.getMetaData();

		ArrayList list = new ArrayList();
		ArrayList<List> list2 = new ArrayList<>();
		for (int i = 0; i < metaData.getColumnCount(); i++) {
			String label = metaData.getColumnLabel(i + 1);
			list.add(label);
		}
		list2.add(list);

		int index = metaData.getColumnCount();
		if (index == 1) {
			while (set.next()) {
				list = new ArrayList<>();
				String label = set.getString(1);
				list.add(label);
				list2.add(list);
			}
		} else {
			while (set.next()) {
				list = new ArrayList<>();
				for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
					String label = set.getString(i);
					list.add(label);
				}
				list2.add(list);
			}
		}

		return list2;
	}

	private static void initConnection(URL url) throws Exception {
		Properties pps = new Properties();
		final InputStream is = url.openStream();
		pps.load(is);
		String jdbcDriver = pps.getProperty("jdbcDriver");
		String jdbcUrl = pps.getProperty("jdbcUrl");
		String jdbcUserName = pps.getProperty("jdbcUserName");
		String jdbcPassword = pps.getProperty("jdbcPassword");
		BASEPACKAGE = pps.getProperty("basePackage");
		Class.forName(jdbcDriver);
		is.close();
		CONNECTION = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
	}

	private static void destroyConnection(Connection connection) {
		try {
			if (null != connection) {
				connection.close();
			}
		} catch (Exception e) {
		}
	}

	private static List<String> readTables(ArrayList<List> list) {
		List<String> result = new ArrayList<String>();

		for (int i = 1; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).size(); j++) {
				result.add((String) list.get(i).get(j));
			}
		}
		return result;
	}

	private static List<String> readFileds(ArrayList<List> list) {
		List<String> result = new ArrayList<String>();

		for (int i = 1; i < list.size(); i++) {
			result.add((String) list.get(i).get(0));
		}
		return result;
	}

	private static String changeFirstCharToLower(String src) {
		char c = src.charAt(0);
		String regex = "[^A-Z]";
		boolean notUpper = Pattern.matches(regex, c + "");
		if (notUpper) {
			return src;
		}
		return src.replaceFirst(src.charAt(0) + "", (char) (src.charAt(0) + 32) + "");
	}

	private static String changeArrBracketPair(Object[] arr) {
		String str = Arrays.toString(arr);
		str = str.replace("[", "(");
		str = str.replace("]", ")");
		return str;
	}

	private static void renderFile(String templateFileName, Map<String, Object> paraMap, String filePath, String type) {
		try {

			//InputStream controllerInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateFileName);
			InputStream controllerInputStream = SwingGenneratorCode.class.getResourceAsStream(templateFileName);

			int count = 0;
			while (count == 0) {
				count = controllerInputStream.available();
			}

			byte[] bytes = new byte[count];
			int readCount = 0; // 已经成功读取的字节的个数
			while (readCount < count) {
				readCount += controllerInputStream.read(bytes, readCount, count - readCount);
			}

			String template = new String(bytes);

//			BeetlKit.gt.registerTag("compress", TrimHtml.class);
			String javaSrc = BeetlKit.render(template, paraMap);
			if ("html".equals(type)) {
				String start = "<#compress>\n";
				start += javaSrc;
				String end = "</#compress>\n";
				start += end;
				javaSrc = null;
				javaSrc = start;
			}
			//--
			/*
			 * ClasspathResourceLoader loader = new ClasspathResourceLoader();
			 * Configuration cfg = Configuration.defaultConfiguration();
			 * // GroupTemplate groupTemplate = BeetlRenderFactory.groupTemplate;
			 * GroupTemplate groupTemplate = new GroupTemplate(loader, cfg);
			 * groupTemplate.registerTag("compress", TrimHtml.class);
			 * Template t = groupTemplate.getTemplate(template);
			 * t.binding(paraMap);
			 * String javaSrc = t.render();
			 */
			//--

			File file = new File(filePath);

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(javaSrc);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeStringFileToDeskForWebRoot(Map<String, Object> data) {
		try {
			String controllerKey = (String) data.get("controllerKey");
			String type = (String) data.get("type");
			String fileName = controllerKey + "." + type;

			if (fileName.indexOf("Report") != -1 || fileName.indexOf("report") != -1) {
				fileName = "report/" + fileName;
			} else {
				fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "/" + fileName;
			}

			String webRootPath = null;
			String webContentPath = null;

			if ("js".equals(type)) {
				webRootPath = System.getProperty("user.dir") + "/WebRoot/assets/biz-logic/js/" + fileName;
				webContentPath = System.getProperty("user.dir") + "/WebContent/assets/biz-logic/js/" + fileName;
			} else {
				webRootPath = System.getProperty("user.dir") + "/WebRoot/pager/biz-logic/" + fileName;
				webContentPath = System.getProperty("user.dir") + "/WebContent/pager/biz-logic/" + fileName;
			}

			boolean rootIsExists = new File(System.getProperty("user.dir") + "/WebRoot/").exists();
			boolean contentIsExists = new File(System.getProperty("user.dir") + "/WebContent/").exists();

			if ("js".equals(type)) {
				if (rootIsExists) {
					renderFile("genner_js.html", data, webRootPath, null);
				} else if (contentIsExists) {
					renderFile("genner_js.html", data, webContentPath, null);
				} else if (!rootIsExists && !contentIsExists) {
					//如果两个目录都不存在的话webRootPath优先
					renderFile("genner_js.html", data, webRootPath, null);
				}
			} else {
				if (rootIsExists) {
					renderFile("genner_html.html", data, webRootPath, "html");
				} else if (contentIsExists) {
					renderFile("genner_html.html", data, webContentPath, "html");
				} else if (!rootIsExists && !contentIsExists) {
					//如果两个目录都不存在的话webRootPath优先
					renderFile("genner_html.html", data, webRootPath, "html");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//map{controllerKey:'',packageName:'',className:'',tableName:'',reportName:'',fields:['','','']}
	private static boolean gennerCodeHandler(Map<String, Object> data) {
		String controllerKey = changeFirstCharToLower(data.get("className") + "");
		data.put("controllerKey", controllerKey);

		//---------------
		//1:生成Model类
		gennerModel(data);
		//2:生成Controller类
		gennerController(data);
		//3:生成validator类
		gennerValidator(data);
		//4:生成sql.xml文件
		gennerSqlFile(data);
		//5:生成html文件
		gennerHtmlFile(data);
		//6:生成js文件
		gennerJsFile(data);
		//---------------
		return true;
	}

	private static void gennerJsFile(Map<String, Object> data) {
		// TODO Auto-generated method stub
		data.put("type", "js");
		writeStringFileToDeskForWebRoot(data);
	}

	private static void gennerHtmlFile(Map<String, Object> data) {
		// TODO Auto-generated method stub
		data.put("type", "html");
		writeStringFileToDeskForWebRoot(data);
	}

	private static void gennerController(Map<String, Object> data) {
		// TODO Auto-generated method stub
		data.put("packageName", BASEPACKAGE + ".web.controller");
		String filePath = System.getProperty("user.dir") + "/src/" + ((BASEPACKAGE + ".web.controller.").replace(".", "/")) + data.get("className") + "Controller.java";
		renderFile("genner_controller.html", data, filePath, null);
	}

	private static void gennerSqlFile(Map<String, Object> data) {
		// TODO Auto-generated method stub
		String filePath = System.getProperty("user.dir") + "/src/" + ((BASEPACKAGE + ".sqlxml.").replace(".", "/")) + data.get("className") + ".sql.xml";
		renderFile("genner_sql.html", data, filePath, null);
	}

	private static void gennerValidator(Map<String, Object> data) {
		// TODO Auto-generated method stub
		data.put("packageName", BASEPACKAGE + ".web.validator");
		String filePath = System.getProperty("user.dir") + "/src/" + ((BASEPACKAGE + ".web.validator.").replace(".", "/")) + data.get("className") + "Validator.java";
		renderFile("genner_validator.html", data, filePath, null);
	}

	private static void gennerModel(Map<String, Object> data) {
		// TODO Auto-generated method stub
		data.put("packageName", BASEPACKAGE + ".web.model");
		String filePath = System.getProperty("user.dir") + "/src/" + ((BASEPACKAGE + ".web.model.").replace(".", "/")) + data.get("className") + "Model.java";
		renderFile("genner_model.html", data, filePath, null);
	}

	private static String tableName = "";
	public static void main(String[] args) throws IOException {
//		URL url = Thread.currentThread().getContextClassLoader().getResource("genner_code.properties");
		URL url = SwingGenneratorCode.class.getResource("genner_code.properties");
		if (null == url) {
			/*
			 * JDialog dialog = new JDialog();
			 * JPanel contentPane = new JPanel();
			 * contentPane.add(new JLabel("请放入jdbc.properties到src目录下"));
			 * dialog.setSize(300, 100);
			 * dialog.setLocationRelativeTo(null);
			 * dialog.setContentPane(contentPane);
			 * dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			 * dialog.setVisible(true);
			 */
			JOptionPane.showMessageDialog(null, "请放入genner_code.properties到当前Java包所在的目录下");
			System.exit(1);
		} else {
			//读取数据库表数组
			//遍历数组进行创建Jradio
			try {
				initConnection(url);

				List<String> data = readTables(readConst("show tables;", CONNECTION));

				final JFrame frame = new JFrame("数据表");
				frame.setSize(1000, 500);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						//关闭db  io
						destroyConnection(CONNECTION);
						super.windowClosing(e);
					}

				});

				JPanel contentPane = new JPanel();
				final ButtonGroup radioGroup = new ButtonGroup();
				for (int i = 1; i < data.size(); i++) {
					JRadioButton radio = new JRadioButton(data.get(i));
					radioGroup.add(radio);
					contentPane.add(radio);
				}
				JLabel label1 = new JLabel("所选表对应Java类名:");
				final JTextField text1 = new JTextField("请输入Java类名", 20);
				JLabel label2 = new JLabel("基础包路径:");
				final JTextField text2 = new JTextField("默认为 com.yingmob", 20);
				JLabel label3 = new JLabel("报表名称:");
				final JTextField text3 = new JTextField("请输入报表名称", 20);
				JButton confirm = new JButton("start");

				confirm.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Boolean isSelected = false;
						String selectedText = "";
						final String javaClassName = text1.getText();
						String basePackageName = "默认为 com.yingmob".equals(text2.getText()) || "".equals(text2.getText()) ? BASEPACKAGE : text2.getText();
						final String reportName = text3.getText();
						Enumeration<AbstractButton> elements = radioGroup.getElements();
						while (elements.hasMoreElements()) {
							AbstractButton element = elements.nextElement();
							if (element.isSelected()) {
								selectedText = element.getText();
								tableName = selectedText;
								isSelected = true;
								break;
							}
						}

						//验证
						if (!isSelected || "".equals(selectedText)) {
							JOptionPane.showMessageDialog(null, "请选择表!");
							return;
						} else if ("".equals(javaClassName) || "请输入Java类名".equals(javaClassName)) {
							JOptionPane.showMessageDialog(null, "请输入Java类名!");
							text1.setText("请输入Java类名");
							return;
						} else if ("".equals(basePackageName) || "请填写基础包路径".equals(basePackageName)) {
							JOptionPane.showMessageDialog(null, "请填写基础包路径!");
							text2.setText("请填写基础包路径");
							return;
						} else if ("".equals(reportName) || "请输入报表名称".equals(reportName)) {
							JOptionPane.showMessageDialog(null, "请输入报表名称!");
							text3.setText("请输入报表名称");
							return;
						}

						// handler  读表结构 获取字段 然后进行模板生成code

						try {
							List<String> data = readFileds(readConst("desc " + selectedText, CONNECTION));
//							System.out.println(data);
							/*
							 * for (String string : data) {
							 * 
							 * }
							 */
							/*
							 * JFrame f1 = new JFrame("选择需要的字段");
							 * f1.setSize(1000, 500);
							 * f1.setLocationRelativeTo(null);
							 * f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							 * f1.setVisible(true);
							 */
							final JDialog dialog = new JDialog(frame, "选择需要的字段", true);
							dialog.setSize(600, 600);
							final JPanel contentPane = new JPanel();
							//ButtonGroup checkGroup = new ButtonGroup();
							JLabel label1 = new JLabel("表名:" + selectedText + "   类名:" + javaClassName + "   报表名:" + reportName);
							contentPane.add(label1);
							for (String string : data) {
								JCheckBox box = new JCheckBox(string);
								//checkGroup.add(box);
								contentPane.add(box);
							}
							dialog.setLocationRelativeTo(null);
//							dialog.setContentPane(contentPane);
//							dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

							JButton gennerBtn = new JButton("start");
							gennerBtn.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									/*
									 * for (Component c : dialog.getComponents()) {
									 * if (c instanceOf JCheckBox) {
									 * if (((JCheckBox)c).isSelected()) {
									 * System.out.println("selected");
									 * }
									 * }
									 * }
									 */
									HashMap<String, Object> gennerData = new HashMap<String, Object>();
									gennerData.put("className", javaClassName);
									gennerData.put("reportName", reportName);
									gennerData.put("tableName", tableName);
									ArrayList<String> fields = new ArrayList<String>();
									for (int i = 0; i < contentPane.getComponentCount(); i++) {
										if (contentPane.getComponent(i) instanceof JCheckBox) {
											if (((JCheckBox) contentPane.getComponent(i)).isSelected()) {
												fields.add(((JCheckBox) contentPane.getComponent(i)).getText());
											}
										}
									}
									gennerData.put("fields", fields);
									gennerData.put("packageName", BASEPACKAGE);
									//处理的时候写个接口 可以用模板也可以用java代码来进行生成
									if (gennerCodeHandler(gennerData)) {
										JOptionPane.showMessageDialog(null, "此报表生成完成^_^");
									} else {
										JOptionPane.showMessageDialog(null, "此报表生成失败-_-|||");
									}
								}
							});
							contentPane.add(gennerBtn);
							dialog.setContentPane(contentPane);
							dialog.setVisible(true);
						} catch (Exception e1) {
							e1.printStackTrace();
						}

					}
				});

				contentPane.add(label1);
				contentPane.add(text1);
				contentPane.add(label2);
				contentPane.add(text2);
				contentPane.add(label3);
				contentPane.add(text3);
				contentPane.add(confirm);
				frame.setContentPane(contentPane);
				frame.setVisible(true);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
