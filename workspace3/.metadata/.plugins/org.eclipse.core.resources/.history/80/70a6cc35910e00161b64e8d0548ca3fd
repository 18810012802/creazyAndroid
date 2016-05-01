package com.jb.genemap.dist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jb.genemap.dist.model.BranchTranAndPoleCime;
import com.jb.genemap.dist.model.DisTran;
import com.jb.genemap.dist.model.DistDeviceInfo;
import com.jb.genemap.dist.model.DistLine;
import com.jb.genemap.dist.model.Pole;
import com.jb.genemap.dist.model.Substation;
import com.jb.genemap.dist.model.TranAndPoleCime;

/**
 * 从数据库获取svg图形拓扑信息类（一个站）
 * @author yjl
 *
 */
public class ParseDistCime {
	private String cimefilePath = "c:\\1.cime";
	private double topDistSvgDistance = 100;//svg图形据画布上方距离
	private double poleAndPoleDistance = 155;//杆塔和杆塔间距离
	List<Substation> substationList = new ArrayList<Substation>();//厂站集合
	
	List<TranAndPoleCime> tranAndPoleCimeList = new ArrayList<TranAndPoleCime>();//主干线配变和杆塔数据集合
	
	List<BranchTranAndPoleCime> branchTranAndPoleCimeList = new ArrayList<BranchTranAndPoleCime>();//分支线配变和杆塔数据集合
	//存储所有绘图设备的信息
	List<DistDeviceInfo> svgList=new ArrayList<DistDeviceInfo>();
	
	public ParseDistCime(String lineName){
		ReadDistCimeFile readDistCimeFile = new ReadDistCimeFile();
		Map<String,Object> map = readDistCimeFile.getDataMap(cimefilePath, lineName);
		substationList = (List<Substation>) map.get("substationList");//厂站集合
		
		tranAndPoleCimeList = (List<TranAndPoleCime>) map.get("tranAndPoleCimeList");//主干线配变和杆塔数据集合
		
		branchTranAndPoleCimeList = (List<BranchTranAndPoleCime>) map.get("branchTranAndPoleCimeList");//分支线配变和杆塔数据集合
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
		for(Substation substation : substationList){
			String id = substation.getId();
			String name = substation.getName();
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
		for(int i=0;i<tranAndPoleCimeList.size();i++){
			TranAndPoleCime tranAndPoleCime = tranAndPoleCimeList.get(i);
			String tranId = tranAndPoleCime.getTranId();
			String tranName = tranAndPoleCime.getTranName();
			if(tranId==null || tranId.trim().length()==0 || "null".equals(tranId.trim())){
				tranId = "";
			}
			if(tranName==null || tranName.trim().length()==0 || "null".equals(tranName.trim())){
				tranName = "";
			}
			if(tranId!=null && tranId.trim().length()!=0){
				DisTran disTran = new DisTran();
				disTran.setId(tranId);
				disTran.setName(tranName);
				disTran.setX(poleAndPoleDistance*(i));
				disTran.setY(topDistSvgDistance+32);
				svgList.add(disTran);
			}
			String id = tranAndPoleCime.getPoleId();
			String name = tranAndPoleCime.getPoleName();
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
			int brachFlag = -1;
			for(int j=0;j<branchTranAndPoleCimeList.size();j++){
				BranchTranAndPoleCime branchTranAndPoleCime = branchTranAndPoleCimeList.get(j);
				String startPoleId = branchTranAndPoleCime.getStartPole();
				String branchTranId = branchTranAndPoleCime.getTranId();
				String branchTranName = branchTranAndPoleCime.getTranName();
				String branchPoleId = branchTranAndPoleCime.getPoleId();
				String branchPoleName = branchTranAndPoleCime.getPoleName();
				if(!startPoleId.equals(id)){
					continue;
				}
				brachFlag++;
				if(branchTranId==null || branchTranId.trim().length()==0 || "null".equals(branchTranId.trim())){
					branchTranId = "";
				}
				if(branchTranName==null || branchTranName.trim().length()==0 || "null".equals(branchTranName.trim())){
					branchTranName = "";
				}
				if(branchTranId!=null && branchTranId.trim().length()!=0){
					DisTran disTran = new DisTran();
					disTran.setId(branchTranId);
					disTran.setName(branchTranName);
					disTran.setX(poleAndPoleDistance*(i+1));
					disTran.setY(topDistSvgDistance+poleAndPoleDistance*(brachFlag+1));
					svgList.add(disTran);
				}
				Pole bp = new Pole();
				bp.setId(branchPoleId);
				bp.setName(branchPoleName);
				bp.setX(poleAndPoleDistance*(i+1));
				bp.setY(topDistSvgDistance+poleAndPoleDistance*(brachFlag+1));
				svgList.add(bp);
				DistLine branchDisLine = new DistLine();
				branchDisLine.setId("");
				branchDisLine.setName("");
				branchDisLine.setX(poleAndPoleDistance*(i+1));
				branchDisLine.setY(topDistSvgDistance + poleAndPoleDistance*brachFlag+32);
				branchDisLine.setOrientation("V");
				branchDisLine.setWidth(poleAndPoleDistance);
				svgList.add(branchDisLine);
			}
		}
	}
}
