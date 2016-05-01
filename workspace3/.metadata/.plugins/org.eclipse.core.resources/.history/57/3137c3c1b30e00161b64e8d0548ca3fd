package com.jb.genemap.dist.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jb.genemap.dist.model.BranchTranAndPoleCime;
import com.jb.genemap.dist.model.DistDeviceInfo;
import com.jb.genemap.dist.model.Substation;
import com.jb.genemap.dist.model.TranAndPoleCime;
import com.jb.genemap.next.service.utils.JdbcDao;

/**
 * 从数据库获取svg图形拓扑信息类（一个站）
 * @author yjl
 *
 */
public class DistDBToCime {
	private String lineId = "407707999";
	private JdbcDao jdbcDao = new JdbcDao();
	List<Substation> sublist = new ArrayList<Substation>();
	List<TranAndPoleCime> tranAndPoleCimeList = new ArrayList<TranAndPoleCime>();
	List<BranchTranAndPoleCime> branchTranAndPoleCimeList = new ArrayList<BranchTranAndPoleCime>();
	//存储所有绘图设备的信息
	List<DistDeviceInfo> svgList=new ArrayList<DistDeviceInfo>();
	
	public DistDBToCime(String lineId){
		this.lineId=lineId;
		setSubstationInfo();
		setMainPoleInfo();
	}
	
	public void distLineData2CIME(String fileName, String entityName, String type, String CalTime) {
	
		File file = new File(fileName);
		FileWriter writer = null;
		int index = 1;//序号
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
//				System.out.println(fileName + "文本文件创建成功");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(fileName);
			// 题目<!Entity=泉州 type=电网模型 time='2014-03-13T02:00:12'!>
			String txt = "<!Entity=" + entityName + " type=" + type + "   time='" + CalTime + "'!>";
			writer.write(txt + "\r\n");
			
			/**
			 * 厂站信息<Substation::上暮S220线></CalParameter::上暮S220线>
			 */
			index = 1;
			txt = "<Substation::" + entityName + ">";
			writer.write(txt + "\r\n");
			txt = "@Num      substationId          substationName";
			writer.write(txt + "\r\n");
			txt = "//序号   变电站id       变电站名称 ";
			writer.write(txt + "\r\n");
			txt = "$-    -    -";
			writer.write(txt + "\r\n");
			for (Substation  s: sublist) {
				txt = "# " + (index++) + "    "
						+ s.getId() + "    "
						+ s.getName();
				writer.write(txt + "\r\n");
			}
			txt = "</Substation::" + entityName + ">";
			writer.write(txt + "\r\n");
			
			/**
			 * 主干线配变和杆塔信息<TranAndPole::上暮S220线></BaseVoltage::上暮S220线>
			 */
			index = 1;
//			txt = "//基准电压";
//			writer.write(txt + "\r\n");
			txt = "<TranAndPole::" + entityName + ">";
			writer.write(txt + "\r\n");
			txt = "@Num      tranId           tranName           poleId          poleName";
			writer.write(txt + "\r\n");
			txt = "//序号    配变id           配变名称     杆塔id       杆塔名称";
			writer.write(txt + "\r\n");
			txt = "$-    -    -    -    -";
			writer.write(txt + "\r\n");
			for (TranAndPoleCime t : tranAndPoleCimeList) {
				txt = "# " + (index++) + "    "
						+ t.getTranId() + "    "
						+ t.getTranName() + "    "
						+ t.getPoleId() + "    "
						+ t.getPoleName();
				writer.write(txt + "\r\n");
			}
			txt = "</TranAndPole::" + entityName + ">";
			writer.write(txt + "\r\n");

			/**
			 * 分支线配变和杆塔信息<BranchTranAndPole::上暮S220线></BranchTranAndPole::上暮S220线>
			 */
			index = 1;
//			txt = "//地区";
//			writer.write(txt + "\r\n");
			txt = "<BranchTranAndPole::" + entityName + ">";
			writer.write(txt + "\r\n");
			txt = "@Num      startPole           tranId                          tranName         poleId         poleName";
			writer.write(txt + "\r\n");
			txt = "//序号    起点杆塔id           配变id                      配变名称       杆塔id	杆塔名称";
			writer.write(txt + "\r\n");
			txt = "$-    -    -    -    -    -";
			writer.write(txt + "\r\n");
			for(BranchTranAndPoleCime bt : branchTranAndPoleCimeList){
				txt = "# " + (index++) + "    "
						+ bt.getStartPole() + "    "
						+ bt.getTranId() + "    "
						+ bt.getTranName() + "    "
						+ bt.getPoleId() + "    "
						+ bt.getPoleName();
				writer.write(txt + "\r\n");
			}
			txt = "</BranchTranAndPole::" + entityName + ">";
			writer.write(txt + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置厂站图形信息
	 * @return
	 */
	public void setSubstationInfo(){
		List<Substation> result = new ArrayList<Substation>();
		String sql = "SELECT T.OBJ_ID, T.BDZMC FROM T_SB_ZNYC_DZ T WHERE T.OBJ_ID IN(SELECT QDWZ FROM T_SB_ZWYC_XL WHERE OBJ_ID='"+lineId+"')";
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for(Map<String, String> map : list){
			String id = map.get("OBJ_ID");
			String name = map.get("BDZMC");
			Substation s = new Substation();
			s.setId(id);
			s.setName(name);
			sublist.add(s);
		}
	}
	
	/**
	 * 获取主干线路上杆塔信息
	 * @return
	 */
	public void setMainPoleInfo(){
		String sql = "SELECT (SELECT OBJ_ID FROM T_SB_ZWYC_ZSBYQ WHERE SSGT = T.OBJ_ID) TRANID," +
				"(SELECT SBMC FROM T_SB_ZWYC_ZSBYQ  WHERE SSGT = T.OBJ_ID) TRANNAME,T.OBJ_ID,T.GTBH FROM T_SB_ZWYC_GT T WHERE T.SSXL='"+lineId+"' AND t.SSFDXL IS NULL ORDER BY T.GTPLXH";
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for(int i=0;i<list.size();i++){
			Map<String, String> map = list.get(i);
			String tranId = map.get("TRANID");
			String tranName = map.get("TRANNAME");
			String id = map.get("OBJ_ID");
			String name = map.get("GTBH");
			TranAndPoleCime t = new TranAndPoleCime();
			t.setTranId(tranId);
			t.setTranName(tranName);
			t.setPoleId(id);
			t.setPoleName(name);
			tranAndPoleCimeList.add(t);
			String branchPoleSql = "SELECT (SELECT OBJ_ID FROM T_SB_ZWYC_ZSBYQ WHERE SSGT = T.OBJ_ID) TRANID," +
					" (SELECT SBMC FROM T_SB_ZWYC_ZSBYQ WHERE SSGT = T.OBJ_ID) TRANNAME," +
					" T.OBJ_ID, T.GTBH FROM T_SB_ZWYC_GT T WHERE T.SSFDXL IN" +
					" (SELECT T.OBJ_ID FROM T_SB_ZWYC_XL T WHERE T.SSZX = '"+lineId+"' AND T.QDWZ='"+id+"')  ORDER BY T.GTBH";
			List<Map<String, String>> branchPolelist = jdbcDao.queryBySql(branchPoleSql);
			for(int j=0;j<branchPolelist.size();j++){
				Map<String, String> branchPoleMap = branchPolelist.get(j);
				String branchTranId = branchPoleMap.get("TRANID");
				String branchTranName = branchPoleMap.get("TRANNAME");
				String branchPoleId = branchPoleMap.get("OBJ_ID");
				String branchPoleName = branchPoleMap.get("GTBH");
				BranchTranAndPoleCime bt = new BranchTranAndPoleCime();
				bt.setStartPole(id);
				bt.setTranId(branchTranId);
				bt.setTranName(branchTranName);
				bt.setPoleId(branchPoleId);
				bt.setPoleName(branchPoleName);
				branchTranAndPoleCimeList.add(bt);
			}
		}
	}	
}
