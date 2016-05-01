package com.jb.genemap.dist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jb.genemap.dist.model.DisTran;
import com.jb.genemap.dist.model.DistDeviceInfo;
import com.jb.genemap.dist.model.DistLine;
import com.jb.genemap.dist.model.Pole;
import com.jb.genemap.dist.model.Substation;
import com.jb.genemap.next.service.utils.JdbcDao;

/**
 * 从数据库获取svg图形拓扑信息类（一个站）
 * @author yjl
 *
 */
public class ParseDistDB {
	private String lineId = "407707999";
	private JdbcDao jdbcDao = new JdbcDao();
	private double topDistSvgDistance = 100;//svg图形据画布上方距离
	private double poleAndPoleDistance = 155;//杆塔和杆塔间距离
	//存储所有绘图设备的信息
	List<DistDeviceInfo> svgList=new ArrayList<DistDeviceInfo>();
	
	public ParseDistDB(String lineId){
		this.lineId=lineId;
	}
	
	public List<DistDeviceInfo> getSvgList(){
		setSubstationInfo();
		setMainPoleInfo();
		return svgList;
	}
	
	/**
	 * 设置厂站图形信息
	 * @return
	 */
	public void setSubstationInfo(){
		String sql = "SELECT T.OBJ_ID, T.BDZMC FROM T_SB_ZNYC_DZ T WHERE T.OBJ_ID IN(SELECT QDWZ FROM T_SB_ZWYC_XL WHERE OBJ_ID='"+lineId+"')";
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for(Map<String, String> map : list){
			String id = map.get("OBJ_ID");
			String name = map.get("BDZMC");
			Substation s = new Substation();
			s.setId(id);
			s.setName(name);
			s.setX(0);
			s.setY(topDistSvgDistance);
			svgList.add(s);
			DistLine disLine = new DistLine();
			disLine.setId("");
			disLine.setName("");
			disLine.setX(30);
			disLine.setY(topDistSvgDistance);
			disLine.setOrientation("H");
			disLine.setWidth(poleAndPoleDistance);
			svgList.add(disLine);
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
			if(tranId!=null && tranId.trim().length()!=0){
				DisTran disTran = new DisTran();
				disTran.setId(tranId);
				disTran.setName(tranName);
				disTran.setX(poleAndPoleDistance*(i));
				disTran.setY(topDistSvgDistance+32);
				svgList.add(disTran);
			}
			String id = map.get("OBJ_ID");
			String name = map.get("GTBH");
			
			Pole p = new Pole();
			p.setId(id);
			p.setName(name);
			p.setX(poleAndPoleDistance*(i+1));
			p.setY(topDistSvgDistance);
			svgList.add(p);
			DistLine disLine = new DistLine();
			disLine.setId("");
			disLine.setName("");
			disLine.setX(poleAndPoleDistance*(i+1)+30);
			disLine.setY(topDistSvgDistance);
			disLine.setOrientation("H");
			disLine.setWidth(poleAndPoleDistance);
			svgList.add(disLine);
			
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
				if(branchTranId!=null && branchTranId.trim().length()!=0){
					DisTran disTran = new DisTran();
					disTran.setId(branchTranId);
					disTran.setName(branchTranName);
					disTran.setX(poleAndPoleDistance*(i+1));
					disTran.setY(topDistSvgDistance+poleAndPoleDistance*(j+1));
					svgList.add(disTran);
				}
				Pole bp = new Pole();
				bp.setId(branchPoleId);
				bp.setName(branchPoleName);
				bp.setX(poleAndPoleDistance*(i+1));
				bp.setY(topDistSvgDistance+poleAndPoleDistance*(j+1));
				svgList.add(bp);
				DistLine branchDisLine = new DistLine();
				branchDisLine.setId("");
				branchDisLine.setName("");
				branchDisLine.setX(poleAndPoleDistance*(i+1));
				branchDisLine.setY(topDistSvgDistance + poleAndPoleDistance*j+32);
				branchDisLine.setOrientation("V");
				branchDisLine.setWidth(poleAndPoleDistance);
				svgList.add(branchDisLine);
			}
		}
	}	
}
