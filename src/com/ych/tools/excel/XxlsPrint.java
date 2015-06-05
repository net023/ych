package com.ych.tools.excel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class XxlsPrint extends XxlsAbstract{

	private List msg = new ArrayList();
	 
    public List getMsg() {
        return msg;
    }
 
    public void setMsg(List msg) {
        this.msg = msg;
    }
 
    public void optRows(int sheetIndex,int curRow, List<String> rowlist) throws SQLException {
        if(rowlist!=null&&rowlist.size()!=0){
            List list = new ArrayList();
            list.addAll(rowlist);
            msg.add(list);
        }
    }
    
    public static void main(String[] args) throws Exception {
    	long start = System.currentTimeMillis();
        XxlsPrint howto = new XxlsPrint();
        String fileName = "C:\\Users\\Administrator\\Downloads\\力洋数据--快卡养车0527\\0527\\力洋保养数据--重庆快卡养车0527\\test.xlsx";
        howto.processOneSheet1(fileName, 3);
//        howto.process(fileName);
        System.out.println("行数："+howto.getMsg().size());
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

}
