package com.jb.genemap.next.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import com.flowCal.device.model.main.ACLineDot;
import com.flowCal.device.model.main.Breaker;
import com.flowCal.device.model.main.BusbarSection;
import com.flowCal.device.model.main.Disconnector;
import com.flowCal.device.model.main.PowerTransformer;
import com.flowCal.device.model.main.TransformerWinding;
import com.jb.genemap.next.model.BusConnType;
import com.jb.genemap.next.model.DataSourceType;
import com.jb.genemap.next.model.DeviceType;
import com.jb.genemap.next.model.OrientationType;
import com.jb.genemap.next.model.deviceTypes.BreakerInfo;
import com.jb.genemap.next.model.deviceTypes.BusLineInfo;
import com.jb.genemap.next.model.deviceTypes.DeviceInfo;
import com.jb.genemap.next.model.deviceTypes.DisconnectorInfo;
import com.jb.genemap.next.model.deviceTypes.LineInfo;
import com.jb.genemap.next.model.deviceTypes.TransformerInfo;
import com.jb.genemap.next.service.utils.JdbcDao;
import com.jb.genemap.next.view.Main;

public class DeviceInfoList {
	public static double topTranDistance = 500;//变压器距离画布上方距离
	
	public static double leftTranDistance = 500;//变压器距离画布左方距离
	
	public static double topBusDistance = 500;//变压器距离画布上方距离
	
	public  static double busAndBusDistance = Main.adjustDistance;//母线与母线间的距离
	
	public static double tranAndTranDistance = Main.adjustDistance;;//变压器与变压器间的距离
	
	public static double leftBusDistance = 500;//母线距离画布左方距离
	
	public static double breakerToBreakerDistance = 50;//开关和开关间的距离
	
	public static double maxBreakerNumForAllHigh=2;//
	
	public static double maxBreakerNumForAllRealHigh=2;
	
	public static double maxBreakerNumForAllLow=5;//
	
	public static double busWidthForHigh=(maxBreakerNumForAllHigh+1)*breakerToBreakerDistance;
	
	public  static double busAndBusDistanceForLow=100;//
	
	public static double breakerToBreakerDisdanceForLow = 50;//低压母线上开关和开关间的距离
//	private List<DeviceInfo> paramList = new ArrayList<DeviceInfo>();//设备图形参数集合
	private List<PowerTransformer> tranList = new ArrayList<PowerTransformer>();//cime文件中的主变集合数据信息
	private List<Breaker> breakerList = new ArrayList<Breaker>();//cime文件中的开关集合数据信息
	private List<Disconnector> disconnectorList = new ArrayList<Disconnector>();//cime文件中的刀闸集合数据信息
	private List<BusbarSection> busbarSectionList = new ArrayList<BusbarSection>();//cime文件中的母线集合数据信息
	private List<ACLineDot> lineList = new ArrayList<ACLineDot>();//cime文件中的线路集合数据信息
//	private List<Breaker> tranBreakerList = new ArrayList<Breaker>();//主变绕组连接的开关集合（判断母线所连接的开关的绘图方向）
//	private List<Disconnector> tranDisconnectorList = new ArrayList<Disconnector>();//主变绕组连接的刀闸集合（判断母线所连接的刀闸的绘图方向）
	//存储所有绘图设备的信息
	List<DeviceInfo> list=new ArrayList<DeviceInfo>();
	//获取belongbreakerno和sortno之间的关联
	Map<String,String> breakernoToSortno=new HashMap<String,String>();
	//母联设备前后母线的mrid组合,用于和母联设备中的sortNo对比，以判断是否删除重复母联设备
	List<String> join=new ArrayList<String>();
	//在具有重复的MRID的deviceInfo集合中有哪些不同的belongbreakerNO
    List<String> differentBelongBreakerNo=new ArrayList<String>(); 
	//向下的belongbreakerno集合
	List<String> toTranBreakerNo=new ArrayList<String>(); 
	//按母联顺序排列的母线的MRID[1,2,0]
	List<String> sortBusMRID=new ArrayList<String>();
	//存放高压母线id对应的来源设备信息
	Map<String,DeviceInfo> busNoToBreakerInfo=new HashMap<String,DeviceInfo>();
	//存放高压母线id对应的高压母线信息
	List<BusLineInfo> busInfoForHigh=new ArrayList<BusLineInfo>();
	
	
	//暂时存储低压母线的集合
	List<BusLineInfo> tempBusLineLow1=new ArrayList<BusLineInfo>();
	//暂时存储低压母线的集合
	List<BusLineInfo> tempBusLineLow2=new ArrayList<BusLineInfo>();
	//存储低压母线的集合
	List<BusLineInfo> busLineLow=new ArrayList<BusLineInfo>();
	//已处理的低压母线数
	public int busLineCountLow=0;
	//根据第三次递归得到的breakerMRID在第四次递归时得到的对应的DeviceInfo集合
	List<DeviceInfo> breakerCircle=new ArrayList<DeviceInfo>();
	//最后的两个角
	List<DeviceInfo> corners=new ArrayList<DeviceInfo>();
	//低压母联设备
	List<String> breakerMRID=new ArrayList<String>();
	//低压母线(id,name)
	Map<String,DeviceInfo> busLineMRID=new TreeMap<String,DeviceInfo>();
	List<String> breakerAndBusLineMRID=new ArrayList<String>();
	//存储低压母线桥两端的设备信息(left,right)
	Map<String,Map<String,DeviceInfo>> bridges=new HashMap<String,Map<String,DeviceInfo>>();
	//第三次递归起始breaker,确保第四次次递归走的路线和第三次保持一致
	String sameCircleFlag=null;
	//存放变压器
	List<DeviceInfo> trans=new ArrayList<DeviceInfo>();
	//变压器数目
	public static int transCount=0;
	//存放低压SINGLE母线id对应的来源设备信息
	Map<String,DeviceInfo> busNoToBreakerInfoForLow=new HashMap<String,DeviceInfo>();
	//母联设备
	Map<String,List<String>> busIdToJoinBreakersForSingleLow=new HashMap<String,List<String>>();
	public int busConnType=BusConnType.DOUBLE;
	
	
	
	//混合中的双母接线
	List<String> doubleTranForFixedLow=new ArrayList<String>();
	//母线对应的接入设备
	Map<String,DeviceInfo> busIdToLastBreakerInfoForLow =new HashMap<String,DeviceInfo>();
	//低压母线分别对应的变压器connIds
	Map<String,String> busIdToConnIdLow =new HashMap<String,String>();
	//busIdOfLow分别对应的变压器两个起始分支
	Map<String,String> busIdTostartBreakerIdForLow =new HashMap<String,String>();
	//单母对应的双母接线
	Map<String,String> singleIdToDoubleIdForLow =new HashMap<String,String>();
	//母联设备
	Map<String,List<String>> busIdToJoinBreakersForFixedLow=new HashMap<String,List<String>>();	
	//有顺序的通向低压的conns
	List<String> conns=new ArrayList<String>();	
	//有顺序的初始分支id
	String[] sortedStartDoubleBreakers=new String[2];
	//变压器对应的通向低压的conn
	Map<String,String> tranIdToConnIdLow =new HashMap<String,String>();
	//有顺序的低压母线Id
	List<String> sortedBusIdLow=new ArrayList<String>();
	//有顺序的变压器Id
	List<String> sortedTranIdLow=new ArrayList<String>();
	//存放双母右段
	Map<BusLineInfo,DeviceInfo> doubleRightBusLineLow=new HashMap<BusLineInfo,DeviceInfo>();
	//高压
	List<BusbarSection> startBusList = null;
	//低压
	List<BusbarSection> endBusList = null;
	//中压
	List<BusbarSection> middleBusList = null;
	//双母分支后的所有分支breaker
	List<String> doubleBreakers=new ArrayList<String>();
	//低压双母是否连在一起
	boolean doubleBusIsConnected=false;
	
	
	
	//电压等级编号
	String[] volLevels=new String[3];
	//变压器conn对应的vol
	Map<String,String> connToVol=new HashMap<String,String>();
	//高压母线集合
	List<String> highBusIdList=new ArrayList<String>();
	//第一次递归分支时的发出breaker对应的之后经过的bus
	Map<String,List<String>> breakerToBusForSort=new HashMap<String,List<String>>();
	//第一次递归时发出的conn对应的先后两个分支breaker
	Map<String,String> connToLeftBreaker=new HashMap<String,String>();
	//变压器对应的高中低压conn 
	Map<String,Map<String,String>> tranIdToConnForSort=new HashMap<String,Map<String,String>>();
	//变压器对应的左分支
	Map<String,String> tranToLeftBreaker=new HashMap<String,String>();
	//变压器高压连接点对应的左分支
	Map<String,String> tranToLeftBreakerForHigh=new HashMap<String,String>();
	//数据来源类型
	public int dataSource;
	//存放原始变压器
	List<PowerTransformer> transformers=new ArrayList<PowerTransformer>();
	//另一个辅助高度
	public static double anotherAssisHeight=190;
	//高压母线集合第一次递归用
	List<String> busIdForHigh=new ArrayList<String>();
	//高压母线集合第二次递归用
	List<String> busIdForHigh2=new ArrayList<String>();
	//存放高压母线id对应的高压母线信息
	List<BusLineInfo> busInfoForRealHigh=new ArrayList<BusLineInfo>();
	//高压母线集合第一次递归用
	List<String> tempBusIdForHigh=new ArrayList<String>();
	public DeviceInfoList(int dataSource,String subName) throws Exception {
		this.dataSource=dataSource;
		IParse parseCime=null;
		if(dataSource==DataSourceType.CIME){
			parseCime= new ParseCime(subName);
		}else if(dataSource==DataSourceType.DATABASE){
			parseCime= new ParseDB(subName);
		}
		
			this.tranList = parseCime.getTranMap();
			this.breakerList = parseCime.getBreakerMap();
			this.disconnectorList = parseCime.getDisconnectorMap();
			this.busbarSectionList = parseCime.getBusMap();
			this.lineList = parseCime.getLineMap();
			
			if(tranList.size()==0&&breakerList.size()==0&&disconnectorList.size()==0&&busbarSectionList.size()==0&&lineList.size()==0){
				int w=4/0;
			}
			busAndBusDistance = Main.adjustDistance;
			tranAndTranDistance = Main.adjustDistance;
	}

	public List<DeviceInfo> getDeviceInfoList(){
		startBusList=getStartBusLineList();
		endBusList=getEndBusLineList();
		middleBusList=getMiddleBusLineList();
		busConnType=getBusLineType();
		List<DeviceInfo> allList=new ArrayList<DeviceInfo>();
		if(busConnType==BusConnType.SINGLE||busConnType==BusConnType.DOUBLE){
			try {
				allList = getDeviceInfoListForSingleOrDouble();
			} catch (Exception e) {
				busConnType=BusConnType.FIXED;
			}
		}if(busConnType==BusConnType.FIXED){
			try {
				allList =  getDeviceInfoListForFixed();
			} catch (Exception e) {
				busConnType=BusConnType.FIXEDWITHOUTTRAN;
			}
		}
		if(busConnType==BusConnType.FIXEDWITHOUTTRAN){
			allList =  getDeviceInfoListForFixedWithoutTran();
		}
		if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
			volLevels[0]="33";
			volLevels[1]="32";
			volLevels[2]="25";
			
			allList=getDeviceInfoListForDoubleWithMiddleBus();
		}else{
			String vol=null;
			boolean multiVol=false;
			Set<String> allVolCode=new HashSet<String>();
			for(int j=0;j<middleBusList.size();j++){
				BusbarSection bs=middleBusList.get(j);
				//一旦发现middleBusList多电压，要求用户指定高中低电压volLevels
				if(vol!=null && !vol.equals(bs.getVoltageLevel())){
					multiVol=true;
					System.out.println("高压母线集合(startBusList):");
					for(int i=0;i<startBusList.size();i++){
						BusbarSection bbs=startBusList.get(i);
						allVolCode.add(bbs.getVoltageLevel());
						System.out.println("名称="+bbs.getName()+",编号="+bbs.getVoltageLevel());
					}
					System.out.println("中压母线集合(middleBusList):");
					for(int i=0;i<middleBusList.size();i++){
						BusbarSection bbs=middleBusList.get(i);
						allVolCode.add(bbs.getVoltageLevel());
						System.out.println("名称="+bbs.getName()+",编号="+bbs.getVoltageLevel());
					}
					System.out.println("低压母线集合(endBusList):");
					for(int i=0;i<endBusList.size();i++){
						BusbarSection bbs=endBusList.get(i);
						allVolCode.add(bbs.getVoltageLevel());
						System.out.println("名称="+bbs.getName()+",编号="+bbs.getVoltageLevel());
					}
					
					System.out.println("请在以上母线集合中选择并分别输入高、中、低压母线的编号，以逗号间隔:");
					while(true){
						Scanner scanner=new Scanner(System.in);
						String line=scanner.nextLine();
						String[] tempLevels=line.split(",");
						if(tempLevels.length==3){
							try{
								for(int i=0;i<tempLevels.length;i++){
									boolean exist=false;
									for(String s:allVolCode){
										if(Integer.parseInt(tempLevels[i])==Integer.parseInt(s)){
											exist=true;
										}
									}
									if(!exist){
										//主动抛异常
										int w=5/0;
									}
								}
							}catch(Exception e){
								System.out.println("输入有误，请在以上母线集合中选择并分别输入高、中、低压母线的编号，以逗号间隔:");
								continue;
							}
							volLevels=tempLevels;
							allList=getDeviceInfoListForDoubleWithMiddleBus();
							break;
						
						}else{
							System.out.println("输入有误，请在以上母线集合中选择并分别输入高、中、低压母线的编号，以逗号间隔:");
						}
					}
					break;
				}
				vol=bs.getVoltageLevel().toString();
				
			}
			if(!multiVol){
				
			}
			
		}
		return allList;
	}
	
	public List<DeviceInfo> getDeviceInfoListForDoubleWithMiddleBus(){
		//getBusLineListByVol(32);
		list = getDeviceInfoListForSingleOrDouble();
		return list;
	}
	
	public List<DeviceInfo> getDeviceInfoListForFixedWithoutTran(){
		//已获得双母接线的变压器
		if(doubleTranForFixedLow.size()>0){
		//清空之前收集的可能有问题的数据
			list.removeAll(list);
			//母线对应的接入设备
			busIdToLastBreakerInfoForLow =new HashMap<String,DeviceInfo>();
			//低压母线分别对应的变压器connIds
			busIdToConnIdLow =new HashMap<String,String>();
			//busIdOfLow分别对应的变压器两个起始分支
			busIdTostartBreakerIdForLow =new HashMap<String,String>();
			//单母对应的双母接线
			singleIdToDoubleIdForLow =new HashMap<String,String>();
			//母联设备
			busIdToJoinBreakersForFixedLow=new HashMap<String,List<String>>();	
			//有顺序的通向低压的conns
			conns=new ArrayList<String>();	
			//有顺序的初始分支id
			sortedStartDoubleBreakers=new String[2];
			//变压器对应的通向低压的conn
			tranIdToConnIdLow =new HashMap<String,String>();
			//有顺序的低压母线Id
			sortedBusIdLow=new ArrayList<String>();
			//有顺序的变压器Id
			sortedTranIdLow=new ArrayList<String>();
			//存放双母右段
			doubleRightBusLineLow=new HashMap<BusLineInfo,DeviceInfo>();
			//高压
			//startBusList = null;
			//低压
			//endBusList = null;
			//双母分支后的所有分支breaker
			doubleBreakers=new ArrayList<String>();
			//低压双目是否连在一起
			doubleBusIsConnected=false;
			//母联设备
			busIdToJoinBreakersForSingleLow=new HashMap<String,List<String>>();
		//从双母变压器开始的第一次递归
			String startTranMRID=doubleTranForFixedLow.get(0);
			for(int i=0;i<tranList.size();i++){

				PowerTransformer ptf=tranList.get(i);
				if(startTranMRID.equals(ptf.getMRID())){
					TransformerInfo ti=new TransformerInfo();
					transCount++;
					ti.setId(ptf.getMRID());
					ti.setName(ptf.getName());
					ti.setOrientation(OrientationType.SOUTH);
					ti.setType(DeviceType.TRANSFORMER);
					ti.setX(leftTranDistance+i*tranAndTranDistance);
					ti.setY(topTranDistance);
					ti.setSvgStr(ti.svgStr());
					List<String> connIds=GetAnotherNodeStrForTran(ptf);
					for(int j=0;j<connIds.size();j++){
						String connIdNow=connIds.get(j);
						firstFindDeviceByConnIdForFixedWithoutTran(connIdNow,ti);
					}
					break;
				}
			}
			
			Iterator jId=busIdToJoinBreakersForSingleLow.keySet().iterator();
			while(jId.hasNext()){
				String key=jId.next().toString();
				ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
				Iterator sIt=singleIdToDoubleIdForLow.keySet().iterator();
				while(sIt.hasNext()){
					String k=sIt.next().toString();
					String v= singleIdToDoubleIdForLow.get(k);
					if(v.equals(key)){
						busIdToJoinBreakersForFixedLow.put(k, value);
						break;
					}
				}
			}
			
			System.out.println("新连接信息：");
			Iterator it7=busIdToJoinBreakersForFixedLow.keySet().iterator();
			while(it7.hasNext()){
				String key=it7.next().toString();
				ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForFixedLow.get(key);
				System.out.println("key="+key+"value="+value.toString());
			}
			System.out.println("单母对应的双母接线：");
			Iterator it5=singleIdToDoubleIdForLow.keySet().iterator();
			while(it5.hasNext()){
				String key=it5.next().toString();
				String value= singleIdToDoubleIdForLow.get(key);
				System.out.println("key="+key+"value="+value.toString());
			}
			System.out.println("连接信息：");
			Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
			while(it.hasNext()){
				String key=it.next().toString();
				ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
				System.out.println("key="+key+"value="+value.toString());
			}
			
			System.out.println("母线对应的接入设备:");
			Iterator it2=busIdToLastBreakerInfoForLow.keySet().iterator();
			while(it2.hasNext()){
				String key=it2.next().toString();
				DeviceInfo value=(DeviceInfo) busIdToLastBreakerInfoForLow.get(key);
				
				System.out.println("key="+key+"value="+value.getName());
			}
			
			System.out.println("busIdOfLow分别对应的变压器两个起始分支:");
			Iterator it3=busIdTostartBreakerIdForLow.keySet().iterator();
			while(it3.hasNext()){
				String key=it3.next().toString();
				String value=(String) busIdTostartBreakerIdForLow.get(key);
				System.out.println("key="+key+"value="+value);
			}
			System.out.println("低压母线分别对应的变压器connIds:");
			Iterator it4=busIdToConnIdLow.keySet().iterator();
			while(it4.hasNext()){
				String key=it4.next().toString();
				String value=(String) busIdToConnIdLow.get(key);
				System.out.println("key="+key+"value="+value);
			}
			Iterator joinIt=singleIdToDoubleIdForLow.keySet().iterator();
			if(singleIdToDoubleIdForLow.size()>=1){
				int count=0;
				while(joinIt.hasNext()){
					count++;
					String key=joinIt.next().toString();
					String value=singleIdToDoubleIdForLow.get(key);
					if(count==1){
						sortedBusIdLow.add(key);
						sortedBusIdLow.add(value);
						Iterator breakersIt=busIdTostartBreakerIdForLow.keySet().iterator();
						while(breakersIt.hasNext()){
							String k=breakersIt.next().toString();
							if(!k.equals(sortedBusIdLow.get(1))){
								sortedBusIdLow.add(k);
							}
							
						}
					}else if(count==2){
						sortedBusIdLow.add(key);
					}
				}
			}
			System.out.println("有顺序的低压母线Id：");	
			System.out.println(sortedBusIdLow.toString());
			sortedStartDoubleBreakers[0]=(busIdTostartBreakerIdForLow.get(sortedBusIdLow.get(1)));
			sortedStartDoubleBreakers[1]=(busIdTostartBreakerIdForLow.get(sortedBusIdLow.get(2)));
			System.out.println("有顺序的初始分支id：");
			System.out.println(sortedStartDoubleBreakers[0]+"//"+sortedStartDoubleBreakers[1]);
			tranIdToConnIdLow.put(doubleTranForFixedLow.get(0), busIdToConnIdLow.get(sortedBusIdLow.get(1)));
			System.out.println("变压器对应的通向低压的conn：");
			Iterator tIt=tranIdToConnIdLow.keySet().iterator();
			while(tIt.hasNext()){
				String key=tIt.next().toString();
				String value= tranIdToConnIdLow.get(key);
				System.out.println("key="+key+"value="+value.toString());
			}
			for(int i=0;i<tranList.size();i++){
				PowerTransformer ptf=tranList.get(i);
				if(startTranMRID.equals(ptf.getMRID())){
					TransformerInfo ti=new TransformerInfo();
					transCount++;
					ti.setId(ptf.getMRID());
					ti.setName(ptf.getName());
					ti.setOrientation(OrientationType.SOUTH);
					ti.setType(DeviceType.TRANSFORMER);
					ti.setX(leftTranDistance+1*tranAndTranDistance);
					ti.setY(topTranDistance);
					ti.setSvgStr(ti.svgStr());
					list.add(ti);
					List<String> connIds=GetAnotherNodeStrForTran(ptf);
					for(int j=0;j<connIds.size();j++){
						String connIdNow=connIds.get(j);
						doubleBusIsConnected=false;
						secondFindDeviceByConnIdForFixedWithoutTran(connIdNow,ti);
					}
					break;
				}
			}
		}
		if(doubleRightBusLineLow.size()>0){
			Iterator dr=doubleRightBusLineLow.keySet().iterator();
			BusLineInfo bli=(BusLineInfo)dr.next();
			if(busNoToBreakerInfoForLow.get(bli.getId())!=null){
				bli.setAssisHeight(busNoToBreakerInfoForLow.get(bli.getId()).getY());
				bli.setAssisWidth(busNoToBreakerInfoForLow.get(bli.getId()).getX());
				bli.setReverse(true);
				bli.setSvgStr(bli.svgStrLow());
				list.add(bli.withVLineStr(bli, doubleRightBusLineLow.get(bli)));
			}
		}
		return list;
	}
	
	public List<DeviceInfo> getDeviceInfoListForFixed(){
		if(startBusList.size()==endBusList.size()){
			ok:for(BusbarSection s:startBusList){
				for(BusbarSection e:startBusList){
					if(s.getMRID().equals(e.getMRID())){
						startBusList.removeAll(startBusList);
						break ok;
					}
				}
			}
		}
		for(int i=0;i<tranList.size();i++){
			PowerTransformer ptf=tranList.get(i);
			TransformerInfo ti=new TransformerInfo();
			transCount++;
			ti.setId(ptf.getMRID());
			ti.setName(ptf.getName());
			ti.setOrientation(OrientationType.SOUTH);
			ti.setType(DeviceType.TRANSFORMER);
			ti.setX(leftTranDistance+i*tranAndTranDistance);
			ti.setY(topTranDistance);
			ti.setSvgStr(ti.svgStr());
			List<String> connIds=GetAnotherNodeStrForTran(ptf);
			for(int j=0;j<connIds.size();j++){
				String connIdNow=connIds.get(j);
				thirdFindDeviceByConnId(connIdNow,ti,breakerMRID,busLineMRID,breakerAndBusLineMRID,false);
			
			}
		}
		Iterator joinIt=singleIdToDoubleIdForLow.keySet().iterator();
		if(singleIdToDoubleIdForLow.size()>=1){
			int count=0;
			while(joinIt.hasNext()){
				count++;
				String key=joinIt.next().toString();
				String value=singleIdToDoubleIdForLow.get(key);
				if(count==1){
					sortedBusIdLow.add(key);
					sortedBusIdLow.add(value);
					Iterator breakersIt=busIdTostartBreakerIdForLow.keySet().iterator();
					while(breakersIt.hasNext()){
						String k=breakersIt.next().toString();
						if(!k.equals(sortedBusIdLow.get(1))){
							sortedBusIdLow.add(k);
						}
						
					}
				}else if(count==2){
					sortedBusIdLow.add(key);
				}
			}
		}
		System.out.println("有顺序的低压母线Id：");	
		System.out.println(sortedBusIdLow.toString());
		System.out.println("低压母线分别对应的变压器connIds：");
		Iterator connIt=busIdToConnIdLow.keySet().iterator();
		while(connIt.hasNext()){
			String key=connIt.next().toString();
			String value=(String) busIdToConnIdLow.get(key);
			System.out.println("key="+key+"value="+value);
		}
		
		conns.add(busIdToConnIdLow.get(sortedBusIdLow.get(0)));
		conns.add(busIdToConnIdLow.get(sortedBusIdLow.get(1)));
		if(sortedBusIdLow.size()==4){
			conns.add(busIdToConnIdLow.get(sortedBusIdLow.get(3)));
		}
		for(int i=0;i<conns.size();i++){
			String conn=conns.get(i);
			for(int j=0;j<tranList.size();j++){
				PowerTransformer ptf=tranList.get(j);
				boolean match=false;
				List<String> connIds=GetAnotherNodeStrForTran(ptf);
				for(int k=0;k<connIds.size();k++){
					String connIdNow=connIds.get(k);
					if(connIdNow.equals(conn)){
						match=true;
						break;
					}
				}
				if(match){
					sortedTranIdLow.add(ptf.getMRID());
					break;
				}
				
			}
		}
		System.out.println("有顺序的变压器Id：");
		System.out.println(sortedTranIdLow.toString());
		
		for(int i=0;i<sortedTranIdLow.size();i++){
			//if(conns.get(i)!=null)
			tranIdToConnIdLow.put(sortedTranIdLow.get(i), conns.get(i));
		}
		System.out.println("变压器对应的通向低压的conn：");
		Iterator tIt=tranIdToConnIdLow.keySet().iterator();
		while(tIt.hasNext()){
			String key=tIt.next().toString();
			String value= tranIdToConnIdLow.get(key);
			System.out.println("key="+key+"value="+value.toString());
		}
		sortedStartDoubleBreakers[0]=(busIdTostartBreakerIdForLow.get(sortedBusIdLow.get(1)));
		sortedStartDoubleBreakers[1]=(busIdTostartBreakerIdForLow.get(sortedBusIdLow.get(2)));
		System.out.println("有顺序的初始分支id：");
		System.out.println(sortedStartDoubleBreakers[0]+"//"+sortedStartDoubleBreakers[1]);
		Iterator jId=busIdToJoinBreakersForSingleLow.keySet().iterator();
		while(jId.hasNext()){
			String key=jId.next().toString();
			ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
			Iterator sIt=singleIdToDoubleIdForLow.keySet().iterator();
			while(sIt.hasNext()){
				String k=sIt.next().toString();
				String v= singleIdToDoubleIdForLow.get(k);
				if(v.equals(key)){
					busIdToJoinBreakersForFixedLow.put(k, value);
					break;
				}
			}
		}
		
		for(int j=0;j<sortedTranIdLow.size();j++){
			for(int i=0;i<tranList.size();i++){
				PowerTransformer ptf=tranList.get(i);
				if(sortedTranIdLow.get(j).equals(ptf.getMRID())){
					TransformerInfo ti=new TransformerInfo();
					//transCount++;
					ti.setId(ptf.getMRID());
					ti.setName(ptf.getName());
					ti.setOrientation(OrientationType.SOUTH);
					ti.setType(DeviceType.TRANSFORMER);
					ti.setX(leftTranDistance+j*tranAndTranDistance);
					ti.setY(topTranDistance);
					ti.setSvgStr(ti.svgStr());
					list.add(ti);
					List<String> connIds=GetAnotherNodeStrForTran(ptf);
					for(int k=0;k<connIds.size();k++){
						String connIdNow=connIds.get(k);
						doubleBusIsConnected=false;
						fouthFindDeviceByConnId(connIdNow,ti);
					}
				}
				
			}
		}
		
		if(doubleRightBusLineLow.size()>0){
			Iterator dr=doubleRightBusLineLow.keySet().iterator();
			BusLineInfo bli=(BusLineInfo)dr.next();
			if(busNoToBreakerInfoForLow.get(bli.getId())!=null){
				bli.setAssisHeight(busNoToBreakerInfoForLow.get(bli.getId()).getY());
				bli.setAssisWidth(busNoToBreakerInfoForLow.get(bli.getId()).getX());
				bli.setReverse(true);
				bli.setSvgStr(bli.svgStrLow());
				list.add(bli.withVLineStr(bli, doubleRightBusLineLow.get(bli)));
			}
		}
		return list;
	}
	
	public void queryAndAddLine(DeviceInfo deviceInfo){
		
		if(dataSource==DataSourceType.DATABASE){
			String lastDeviceId=deviceInfo.getId();
			JdbcDao jd=new JdbcDao();
			String queryLine="SELECT LINE_ID,LINE_NAME FROM LOSS_ARCH_EQUIP_LINE T WHERE T.START_SWITCH_ID ='"+lastDeviceId+"' ";
			List<Map<String,String>> l=jd.queryBySql(queryLine);
			if(l.size()==1){
				Map<String,String> lineMap=l.get(0);
				LineInfo li=new LineInfo();
				li.setId(lineMap.get("LINE_ID"));
				li.setName(lineMap.get("LINE_NAME"));
				li.setOrientation(deviceInfo.getOrientation());
				li.setX(deviceInfo.getX());
				li.setY(deviceInfo.getY());
				li.setSvgStr(li.svgStr());
				list.add(li);
			}
		}
		
	}
	
	public List<DeviceInfo> getDeviceInfoListForSingleOrDouble(){
		transCount=0;
		//List<Map<String, String>> BreakerMapList = joinBreakerData(breakerList, disconnectorList);
	    List<BusbarSection> busList = new ArrayList<BusbarSection>();//获取起始高压侧母线数据集合（绘图以此为起点）
	   if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
		   busList=getBusLineListByVol(Integer.parseInt(volLevels[1]));
	   }else{
		   busList = getStartBusLineList(); 
	   }
	   /* setTranSwith();//设置主变所连接开关或刀闸集合
	    List<Map<String, String>> tranBreakerMapList = joinBreakerData(tranBreakerList);
	    List<Map<String, String>> tranDisconnectorMapList = joinDisconnectorData(tranDisconnectorList);*/
	    int busCount = busList.size();//获取母线高压侧根数
	    for(int i=0;i<busCount;i++){
	    	BusbarSection bs=busList.get(i);
    		BusLineInfo busLineInfo=addBusLine(bs,i,false);
	    	String connId = bs.getPhysicNodeBegin();
	    	firstFindDeviceByConnId(connId,busLineInfo,false);
	    }
	  //-------------------------通过第一次循环获得的数据计算出母线排列顺序，母联设备的信息等START---------------------------------
	  //-------------------------计算出母线排列顺序：list——>repeatlistByMRID——>allRepeatList——>randomList——>busPairList——>busNoList——>leftAndRightBusNoListt——>sortBusMRID---------------------------------
	  //-------------------------计算出重复设备中的母连开关：list——>repeatlistByMRID——>allRepeatList——>differentBelongBreakerNo---------------------------------
	  // List<DeviceInfo> allRepeatList=new ArrayList<DeviceInfo>();
	    List<String> unRepeatlistByMRID=new ArrayList<String>();//不重复的设备
	    List<String> repeatlistByMRID=new ArrayList<String>();//重复的设备
	    for(int i=0;i<list.size();i++){
	    	DeviceInfo df=list.get(i);
	    	String mrid=df.getId();
	    	boolean b=false;
	    	for(String id:unRepeatlistByMRID){
	    		if(id==mrid){
	    			b=true;//已存在该MRID
	    			break;
	    		}
	    	}
	    	if(!b){
	    		unRepeatlistByMRID.add(mrid);
	    	}else{
	    		repeatlistByMRID.add(mrid);
	    	}
	    }
	    if(repeatlistByMRID.size()>0){
	    //具有重复的MRID的deviceInfo集合
	    List<DeviceInfo> allRepeatList=new ArrayList<DeviceInfo>();
//	    Map<String,List<DeviceInfo>> map=new HashMap<String,List<DeviceInfo>>();
	    for(int j=0;j<repeatlistByMRID.size();j++){
	    	String id=repeatlistByMRID.get(j);
//	    	map.put(id, new ArrayList());
	    	for(int i=0;i<list.size();i++){
	    		DeviceInfo df=list.get(i);
		    	String mrid=df.getId();
		    	if(id.equals(mrid)){
		    		allRepeatList.add(df);
//		    			List temp=map.get(id);
//		    			temp.add(df);
//		    			map.put(id, temp);
		    	}
	    	}
	    }
	    
	    //在重复设备中和母线相连的起始刀闸
	    List<DeviceInfo> randomList=new ArrayList<DeviceInfo>();
	    List<String> randomString=new ArrayList<String>();
	    for(DeviceInfo df:allRepeatList){
	    	if(df.getBelongBreakerNo().equals(df.getId())){
	    		randomList.add(df);
	    		randomString.add(df.getId());
	    	}
	    }
	    //相连母线的母线号码对[[1, 2], [0, 2]]
	    List<List<String>> busPairList=new ArrayList<List<String>>();
	    	for(String rs:randomString){
    		List<String> busPair=new ArrayList<String>();
    		 for(DeviceInfo df:allRepeatList){
	    		if(df.getId().equals(rs)){
	    			busPair.add(df.getBelongBusNo());
	    		}
	    	}
    		 boolean b=false;
    		 for(List<String> bp:busPairList){
    			 if((busPair.get(0).equals(bp.get(0))&&busPair.get(1).equals(bp.get(1)))||(busPair.get(1).equals(bp.get(0))&&busPair.get(0).equals(bp.get(1)))){
    				 b=true;
    			 }
    		 }
    		 if(!b){
        		 busPairList.add(busPair); 
    		 }
	    }
	    
	    	 //和母线相连的起始刀闸对应母线的mrid列表[1, 2, 0, 2]
	    	List<String> busNoList=new ArrayList<String>();
	    	 for(List<String> bp:busPairList){
	    		 busNoList.add(bp.get(0));
	    		 busNoList.add(bp.get(1));
	    	 }
	    	//位于起始和结尾德母线id[1, 0]
			List<String> leftAndRightBusNoList=new ArrayList<String>();
			for(String p:busNoList){
				int count=0;
				for(String s:busNoList){
					if(s.equals(p)){
						count++;
					}
				}
				if(count==1){
					leftAndRightBusNoList.add(p);
				}
			}
			//按母联顺序排列的母线的MRID[1,2,0]
			//List<String> sortBusMRID=new ArrayList<String>();
			addSortBusMRID(sortBusMRID,busPairList,leftAndRightBusNoList.get(0));
			
			 //在具有重复的MRID的deviceInfo集合中有哪些不同的belongbreakerNO
		   // List<String> differentBelongBreakerNo=new ArrayList<String>(); 
		    for(DeviceInfo df:allRepeatList){
		    	//DeviceInfo df=allRepeatList.get(i);
		    	String bn=df.getBelongBreakerNo();
		    	boolean b=false;
		    	for(String rf:differentBelongBreakerNo){
		    		if(bn.equals(rf)){
		    			b=true;
		    		}
		    	}
		    	if(!b){
		    		differentBelongBreakerNo.add(bn);
		    	}
		    }
			list.removeAll(list);
			//为所有存在的前后母联mrid集合join添加数据
			for(int i=0;i<sortBusMRID.size()-1;i++)
			{
				join.add(sortBusMRID.get(i)+sortBusMRID.get(i+1));
			}
			for(int j=0;j<sortBusMRID.size();j++)
			{
			 String MRID=sortBusMRID.get(j);
			 for(int i=0;i<busCount;i++){
			    	BusbarSection bs=busList.get(i);
			    	if(bs.getMRID().equals(MRID)){
			    		
			    		BusLineInfo busLineInfo=addBusLine(bs,j,false);
				    	String connId = bs.getPhysicNodeBegin();
				    	busInfoForHigh.add(busLineInfo);
				    	secondFindDeviceByConnId(connId,busLineInfo);
				    	//addBusLine(bs,j,true);
				    	
			    	}
			    	
			    }
			
			}
			//校正assisHeight并添加高压母线
			 for(int i=0;i<busInfoForHigh.size();i++){
					 BusLineInfo busLineInfo=busInfoForHigh.get(i);
					 DeviceInfo di=busNoToBreakerInfo.get(busLineInfo.getId());
					 if(i>0){
						 busLineInfo.setAssisHeight(di.getY()); 
						 busLineInfo.setSvgStr(busLineInfo.svgStr());
					 }
		    		list.add(busLineInfo);
			  }
	    }else{
	    	list.removeAll(list);
	    	 for(int i=0;i<busCount;i++){
			    	BusbarSection bs=busList.get(i);
		    		BusLineInfo busLineInfo=addBusLine(bs,i,true);
			    	String connId = bs.getPhysicNodeBegin();
			    	secondFindDeviceByConnId(connId,busLineInfo);
		    	}
	    }
			  //-------------------------通过第一次循环获得的数据计算出母线排列顺序，母联设备的信息等END---------------------------------
			//添加低压母线
			 for(int i=0;i<busLineLow.size();i++){
				BusLineInfo bi=busLineLow.get(i);
				String sortNo=bi.getSortNo();
				 if(!sortNo.equals("0") && !sortNo.equals((busLineLow.size()-1)+"")){
						list.add(bi.withBridgeLine(bi,bridges.get(bi.getId()).get("left"),bridges.get(bi.getId()).get("right")));
				 }else{
						list.add(bi);
				}
			 }
	    	return list;
	}
	
	//添加母线后递归查询并添加
	public BusLineInfo addBusLine(BusbarSection bs,int i,boolean add){
		BusLineInfo busLineInfo=new BusLineInfo();
    	busLineInfo.setId(bs.getMRID());
    	busLineInfo.setName(bs.getName());
    	
    	double x=i*busWidthForHigh+leftBusDistance+i*busAndBusDistance;
    	busLineInfo.setX(x);
    	busLineInfo.setY(topBusDistance);
    	//busLineInfo.setSortNo(count);
    	busLineInfo.setBelongBusNo(bs.getMRID());
    	//busLineInfo.setBelongBusNo(i+"");
    	busLineInfo.setType(DeviceType.BUSLINE);
    	if(i>0){
    		busLineInfo.setNotFirst(true);
    	}
    	busWidthForHigh=(maxBreakerNumForAllHigh+1)*breakerToBreakerDistance;
    	busLineInfo.setWidth(busWidthForHigh);
    	busLineInfo.setSvgStr(busLineInfo.svgStr());
    	if(add){
    		//DeviceInfo di=busNoToBreakerInfo.get(busLineInfo.getId());
    		//busLineInfo.setAssisHeight(di.getY());
    		list.add(busLineInfo);
    	}
    	
    	//母线只有一个连接点
    	return busLineInfo;
    	
	}
	//递归添加sortBusMRID中母线排序后的mrid
	public void addSortBusMRID(List<String> sortBusMRID,List<List<String>> busPairList,String addId){
		sortBusMRID.add(addId);
		String next=null;
        for(int i=0;i<busPairList.size();i++){
        	List<String> pair=busPairList.get(i);
			boolean b=false;
			for(String busNo:pair){
				if(busNo.equals(addId)){
					b=true;
				}
			}
			if(b){
				for(String busNo:pair){
					if(!busNo.equals(addId)){
						next=busNo;
					}
				}
				busPairList.remove(pair);
				addSortBusMRID(sortBusMRID,busPairList,next);
			}
		}
	}
	
	public BusLineInfo addBusLineLow(BusLineInfo busLineInfo){
		double transDistance=trans.get(1).getX()-trans.get(0).getX();
		double busToBusDistanceForLow=(transDistance-busLineInfo.getWidth()*(busLineLow.size()/2))/(busLineLow.size()/2-1);
		String pos=busLineInfo.getPosition();
		String orien=busLineInfo.getOrientation();
		busLineInfo.setColor("yellow");
		busLineInfo.setWidth(breakerToBreakerDistance*(maxBreakerNumForAllLow+1));
		double x=0;
		double y=0;
		DeviceInfo parent=busLineInfo.getParentDeviceInfo();
		String sortNo=busLineInfo.getSortNo();
		if(pos!=null){
			if(pos.equals(OrientationType.leftUp)){
				x=parent.getX();
				y=parent.getY()+180;
				
			}else if(pos.equals(OrientationType.rightUp)){
				x=parent.getX()-busLineInfo.getWidth()+parent.getWidth()/2;
				y=parent.getY()+180;
			}else if(pos.equals(OrientationType.leftDown)){
				x=parent.getX();
				y=parent.getY()+210;
			}else if(pos.equals(OrientationType.rightDown)){
				x=parent.getX()-busLineInfo.getWidth()+parent.getWidth()/2;
				y=parent.getY()+210;
			}
		}else{
			if(orien.equals(OrientationType.NORTH)){
				x=parent.getX()-busLineInfo.getWidth()+parent.getWidth()/2-(Integer.parseInt(sortNo) * (busToBusDistanceForLow+busLineInfo.getWidth()));
				y=parent.getY()+180;
			}else if(orien.equals(OrientationType.SOUTH)){
				x=parent.getX()-busLineInfo.getWidth()+parent.getWidth()/2-(Integer.parseInt(sortNo) * (busToBusDistanceForLow+busLineInfo.getWidth()));
				y=parent.getY()+210;
			}
		}
		busLineInfo.setX(x);
		busLineInfo.setY(y);
		busLineInfo.setSvgStr(busLineInfo.svgStrLow());
		return busLineInfo;
	}
	
	public void findJoinBreakers(BusLineInfo presetBli,String connId,String lastId,List<String> joinBreakers,int index,int limitCount){
		limitCount++;
		if(limitCount>10){
			//死循环主动抛异常
			int wrong=3/0;
			return;
		}
		boolean t=false;
		//起始母线
		boolean origion=false;
        List<BusbarSection> busbarSectionList = getBusByConnId(connId);
		
		List<Breaker> breakerList = getBreakerByConnId(connId);
		
		List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);
		
		List<Map<String, String>> breakerMapList = joinBreakerData(breakerList, disconnectorList);
		/*String connIdNew=GetAnotherNodeStr(breakerMap,connId);*/
		
		if(busbarSectionList != null && busbarSectionList.size()!=0){
			if((busbarSectionList.get(0).getMRID()).equals(presetBli.getId())||(busbarSectionList.size()==2&&(busbarSectionList.get(1).getMRID()).equals(presetBli.getId()))){
				origion=true;
			}else{
				t=true;
				//该母联是否已记录
				boolean existed=false;
				Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
				while(it.hasNext()){
					String key=it.next().toString();
					ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
					for(String word:value){
						for(String w:joinBreakers){
							if(w.equals(word)){
								existed=true;
								break;
							}
						}
					}
				}
				if(!existed){
					busIdToJoinBreakersForSingleLow.put(busbarSectionList.get(index).getMRID(),joinBreakers);
				}
				else{
					return;
				}
			}
			
			
		}if(breakerMapList != null && breakerMapList.size()!=0 ){
			if(!t){
				if(origion){
					for(int i=0;i<breakerMapList.size();i++){
						Map breakerMap=breakerMapList.get(i);
						if(breakerMap.get("id").equals(lastId)){
							joinBreakers.add(breakerMap.get("id").toString());
							String connIdNew=GetAnotherNodeStr(breakerMap,connId);
							findJoinBreakers(presetBli,connIdNew,breakerMap.get("id").toString(),joinBreakers,index,limitCount);
							break;
						}
					}
				}else{
					for(int i=0;i<breakerMapList.size();i++){
						
						Map breakerMap=breakerMapList.get(i);
						if(breakerMap.get("id").equals(lastId)){
							continue;
						}else{
							joinBreakers.add(breakerMap.get("id").toString());
							String connIdNew=GetAnotherNodeStr(breakerMap,connId);
							findJoinBreakers(presetBli,connIdNew,breakerMap.get("id").toString(),joinBreakers,index,limitCount);
						}
					}
				}
				
			}
			
		}
	}
	//第一次递归
	
	public void firstFindDeviceByConnId(String connId,DeviceInfo deviceInfo,boolean knowFirst){
//		String type= deviceInfo.getType();
//		Map<String,Object> result = new HashMap<String, Object>();
		List<BusbarSection> busbarSectionList = getBusByConnId(connId);
		
		List<Breaker> breakerList = getBreakerByConnId(connId);
		
		List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);
		
		List<Map<String, String>> breakerMapList = joinBreakerData(breakerList, disconnectorList);
		
//		List<ACLineDot> lineList = getLineByConnId(connId);
		
		List<PowerTransformer> powerTransformerList = getTranByConnId(connId);
		
		
		if(busbarSectionList!=null && busbarSectionList.size()!=0){
			if(knowFirst){
				//l
				BusbarSection bs=busbarSectionList.get(0);
				if(bs.getMRID().equals(deviceInfo.getId())){
				}else{
					//BusbarSection bs=busbarSectionList.get(0);
					BusLineInfo bli=new BusLineInfo();
					bli.setId(bs.getMRID());
					bli.setType(DeviceType.BUSLINE);
					bli.setName(bs.getName());
					bli.setParentDeviceInfo(deviceInfo);
				}
				
			}else{
				if(deviceInfo.getBelongBreakerNo()!=null&&!deviceInfo.getBelongBreakerNo().equals("null")){
					breakernoToSortno.put(deviceInfo.getBelongBreakerNo(), deviceInfo.getBelongBusNo()+busbarSectionList.get(0).getMRID());
				}
				if(!deviceInfo.getType().equals(DeviceType.BUSLINE)){
					return;	
				}
			}
		}
		if(powerTransformerList != null && powerTransformerList.size()!=0 &&  !DeviceType.TRANSFORMER.equals(deviceInfo.getType())){
			if(knowFirst){
				
			}else{
					if(deviceInfo.getBelongBreakerNo()!=null && !"null".equals(deviceInfo.getBelongBreakerNo())&&!deviceInfo.getType().equals(DeviceType.BUSLINE)){
						toTranBreakerNo.add(deviceInfo.getBelongBreakerNo());
				}
			}
		}
		if (breakerMapList!=null  && breakerMapList.size()!=0){
			if(knowFirst){
				
			}else{
				for(int i=0;i<breakerMapList.size();i++){
					
					Map breakerMap=breakerMapList.get(i);
					if(breakerMap.get("id").equals(deviceInfo.getId())){
						continue;
					}
					
					String deviceType=breakerMap.get("type").toString();
					DeviceInfo df=null;
					if(deviceType.equals(DeviceType.DISCONNECTOR)){
						df=new DisconnectorInfo();
					}else{
						df=new BreakerInfo();
					}
					df.setId(breakerMap.get("id").toString());
					df.setName(breakerMap.get("name").toString());
					df.setBelongBusNo((deviceInfo).getBelongBusNo());
					if(deviceInfo.getType().equals(DeviceType.BUSLINE)){
						if(breakerMapList.size()>maxBreakerNumForAllHigh){
							maxBreakerNumForAllHigh=breakerMapList.size();
						}
						df.setBelongBreakerNo(df.getId());
					}else{
						df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
					}
					
					df.setType(deviceType);
					double pDeviceX=deviceInfo.getX();
					double pDeviceY=deviceInfo.getY();
					double x=pDeviceX;
					if(deviceInfo.getType().equals(DeviceType.BUSLINE)){
						x=pDeviceX+(i+1)*breakerToBreakerDistance;
					}
					double y=pDeviceY;
					df.setY(y);
					df.setX(x);
					df.setOrientation(OrientationType.NORTH);
					list.add(df.withVLineStr(df,deviceInfo));
					String connIdNew=GetAnotherNodeStr(breakerMap,connId);
					if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
						continue;
					}
					firstFindDeviceByConnId(connIdNew,df,false);
				}
			}
		}
	}
	//第二次递归
	public void secondFindDeviceByConnId(String connId,DeviceInfo deviceInfo){
		String type= deviceInfo.getType();
		Map<String,Object> result = new HashMap<String, Object>();
		List<BusbarSection> busbarSectionList = getBusByConnId(connId);
		
		List<Breaker> breakerList = getBreakerByConnId(connId);
		
		List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);
		
		List<Map<String, String>> breakerMapList = joinBreakerData(breakerList, disconnectorList);
		
		List<ACLineDot> lineList = getLineByConnId(connId);
		
		List<PowerTransformer> powerTransformerList = getTranByConnId(connId);
		if(busbarSectionList!=null && busbarSectionList.size()!=0){
			String busId=busbarSectionList.get(0).getMRID();
			for(int i=0;i<sortBusMRID.size();i++){
				String s=sortBusMRID.get(i);
				if(s.equals(busId)){
					if(i>0&&sortBusMRID.get(i-1).equals(deviceInfo.getBelongBusNo())){
						busNoToBreakerInfo.put(busId,deviceInfo);
					}
				}
			}
		}
		if(lineList != null && lineList.size()!=0  ){
				ACLineDot ad=lineList.get(0);
				LineInfo li=new LineInfo();
				li.setId(ad.getMRID());
				li.setName(ad.getName());
				li.setOrientation(deviceInfo.getOrientation());
				li.setX(deviceInfo.getX());
				li.setY(deviceInfo.getY());
				li.setSvgStr(li.svgStr());
				list.add(li);
		}
		if(powerTransformerList != null && powerTransformerList.size()!=0 &&  !DeviceType.TRANSFORMER.equals(deviceInfo.getType())){
			if(busbarSectionList.size()>0&&!deviceInfo.getType().equals(DeviceType.BUSLINE)){
				return;
			}
			PowerTransformer ptf=powerTransformerList.get(0);
			TransformerInfo ti=new TransformerInfo();
			transCount++;
			ti.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
			ti.setBelongBusNo(deviceInfo.getBelongBusNo());
			ti.setId(ptf.getMRID());
			ti.setName(ptf.getName());
			ti.setOrientation(OrientationType.SOUTH);
			ti.setType(DeviceType.TRANSFORMER);
			ti.setX(deviceInfo.getX());
			ti.setY(deviceInfo.getY());
			ti.setSvgStr(ti.svgStr());
			if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
				//if(transCount==1){
				    ti.setConnNum(3);
				    ti.setSvgStr(ti.svgStr());
					list.add(ti.withVLineStr(ti,deviceInfo));
					ti.setX(ti.getX()+6);
					List<String> connIds=GetAnotherNodeStrForTran(ptf);
					for(int j=0;j<connIds.size();j++){
						String connIdNow=connIds.get(j);
						//if(!connIdNow.equals(connId)){
							firstFindDeviceByConnIdForFixedWithoutTran(connIdNow,ti);
						//}
					}
					if(transCount==1){
						Iterator btb=breakerToBusForSort.keySet().iterator();
						//第一条母线
						while(btb.hasNext()){
							String key=btb.next().toString();
							List<String> value=breakerToBusForSort.get(key);
							if(value.size()==2){
								sortedBusIdLow.add(value.get(0));
							}
						}
						Iterator btb1=breakerToBusForSort.keySet().iterator();
						//中间的母线
						while(btb1.hasNext()){
							String key=btb1.next().toString();
							List<String> value=breakerToBusForSort.get(key);
							if(value.size()!=2){
								sortedBusIdLow.addAll(value);
							}
						}
						Iterator btb2=breakerToBusForSort.keySet().iterator();
						//最后一条母线
						while(btb2.hasNext()){
							String key=btb2.next().toString();
							List<String> value=breakerToBusForSort.get(key);
							if(value.size()==2){
								sortedBusIdLow.add(value.get(1));
							}
						}
					}
					if(transCount==1){
						tranToLeftBreakerForHigh.put(ti.getId(),busIdTostartBreakerIdForLow.get(busIdForHigh.get(0)));
						tranToLeftBreaker.put(ti.getId(),busIdTostartBreakerIdForLow.get(sortedBusIdLow.get(0)));
					}else{
						tranToLeftBreaker.put(ti.getId(),busIdTostartBreakerIdForLow.get(sortedBusIdLow.get(sortedBusIdLow.size()-2)));
					}
					trans.add(ti);
					transformers.add(ptf);
					if(trans.size()==2){
						busAndBusDistanceForLow=(trans.get(1).getX()-trans.get(0).getX()-(sortedBusIdLow.size()-2)*breakerToBreakerDistance*maxBreakerNumForAllLow)/(sortedBusIdLow.size()-3);
						for(int i=0;i<trans.size();i++){
							TransformerInfo t=(TransformerInfo)trans.get(i);
							List<String> conns=GetAnotherNodeStrForTran(transformers.get(i));
							transCount=i+1;
							for(int j=0;j<conns.size();j++){
								String connIdNow=conns.get(j);
								if(!connIdNow.equals(tranIdToConnForSort.get(t.getId()).get("m"))){
									secondFindDeviceByConnIdForFixedWithoutTran(connIdNow,t);
								}
							}
						
						}
					}
					System.out.println("两条路线：");
					Iterator it1=breakerToBusForSort.keySet().iterator();
					while(it1.hasNext()){
						//System.out.println(it1.next());
						String key=it1.next().toString();
						List<String> value=breakerToBusForSort.get(key);
						System.out.println("key="+key+",value="+value.toString());
					}
					System.out.println("变压器对应的连接点：");
					Iterator it2=tranIdToConnForSort.keySet().iterator();
					while(it2.hasNext()){
						String key=it2.next().toString();
						Map<String,String> value=tranIdToConnForSort.get(key);
						System.out.println("key="+key+",value="+value.toString());
					}
					System.out.println("连接信息：");
					Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
					while(it.hasNext()){
						String key=it.next().toString();
						ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
						System.out.println("key="+key+"value="+value.toString());
					}
					System.out.println("bus对应分支breaker信息：");
					Iterator it3=busIdTostartBreakerIdForLow.keySet().iterator();
					while(it3.hasNext()){
						String key=it3.next().toString();
						String value=(String) busIdTostartBreakerIdForLow.get(key);
						System.out.println("key="+key+"value="+value.toString());
					}
					System.out.println("有顺序的bus：");
					System.out.println(sortedBusIdLow.toString());
					System.out.println("变压器对应的左分支：");
					Iterator it4=tranToLeftBreaker.keySet().iterator();
					while(it4.hasNext()){
						String key=it4.next().toString();
						String value=(String) tranToLeftBreaker.get(key);
						System.out.println("key="+key+"value="+value.toString());
					}
					if(transCount==2){
						for(BusLineInfo bli:tempBusLineLow2){
							if(busNoToBreakerInfoForLow.get(bli.getId())!=null&&!bli.getId().equals(sortedBusIdLow.get(0))&&!bli.getId().equals(sortedBusIdLow.get(1))){
								bli.setAssisHeight(busNoToBreakerInfoForLow.get(bli.getId()).getY());
								bli.setAssisWidth(busNoToBreakerInfoForLow.get(bli.getId()).getX());
								if(bli.getId().equals(sortedBusIdLow.get(sortedBusIdLow.size()-1))){
									bli.setReverse(true);
									bli.setAnotherAssisHeight(anotherAssisHeight);
								}
								bli.setNotFirst(true);
								bli.setSvgStr(bli.svgStrLow());
								list.add(bli.withVLineStr(bli, busNoToBreakerInfoForLow.get(bli.getId())));
							}
						}
						
					}
					return;
				//}
			}
			String connIdNow=GetAnotherNodeStrForTran(ptf,connId);
			thirdFindDeviceByConnId(connIdNow,ti,breakerMRID,busLineMRID,breakerAndBusLineMRID,false);
			//Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
			/*while(it.hasNext()){
				String key=it.next().toString();
				ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
			}*/
			if(busConnType==BusConnType.SINGLE){
				list.add(ti.withVLineStr(ti,deviceInfo));
				fouthFindDeviceByConnId(connIdNow,ti);
			}
			if(busConnType==BusConnType.DOUBLE){
				
				list.add(ti.withALineStr(ti.withVLineStr(ti,deviceInfo)));
				BusLineInfo bliUp=new BusLineInfo();
				BusLineInfo bliDown=new BusLineInfo();
				
				if(transCount==1){
					bliUp.setPosition(OrientationType.leftUp);
					bliDown.setPosition(OrientationType.leftDown);
				}else if(transCount==2){
					bliUp.setPosition(OrientationType.rightUp);
					bliDown.setPosition(OrientationType.rightDown);
				}
				bliUp.setOrientation(OrientationType.NORTH);
				bliDown.setOrientation(OrientationType.SOUTH);
				bliUp.setParentDeviceInfo(ti);
				bliDown.setParentDeviceInfo(ti);
				tempBusLineLow1.add(bliDown);
				tempBusLineLow1.add(bliUp);
				trans.add(ti);
				//为对应低压母线设置mrid
				if(transCount==2){
					String connIdNew=GetAnotherNodeStrForTran(ptf,connId);
					
					thirdFindDeviceByConnId(connIdNew,ti,breakerMRID,busLineMRID,breakerAndBusLineMRID,true);

					int busLen=busLineMRID.size();
					for(int i=0;i<tempBusLineLow1.size();i++){
						BusLineInfo bi=tempBusLineLow1.get(i);
						String orien=bi.getPosition();
						if(orien.equals(OrientationType.rightUp)){
							bi.setSortNo("0");
						}else if(orien.equals(OrientationType.leftUp)){
							bi.setSortNo((busLen/2-1)+"");
						}else if(orien.equals(OrientationType.leftDown)){
							bi.setSortNo(busLen/2+"");
						}else if(orien.equals(OrientationType.rightDown)){
							bi.setSortNo(busLen-1+"");
						}
						tempBusLineLow2.add(bi);
					}
					
					
					for(int i=0;i<busLineMRID.size()/2-2;i++){
						BusLineInfo newBliUp=new BusLineInfo();
						BusLineInfo newBliDown=new BusLineInfo();
						bliUp.setOrientation(OrientationType.NORTH);
						bliUp.setSortNo((busLineMRID.size()/2-2-i)+"");
						bliDown.setOrientation(OrientationType.SOUTH);
						bliDown.setSortNo((busLineMRID.size()/2+i)+"");
						bliUp.setParentDeviceInfo(ti);
						bliDown.setParentDeviceInfo(ti);
						tempBusLineLow2.add(bliDown);
						tempBusLineLow2.add(bliUp);
					}
					for(int i=0;i<busLineMRID.size();i++){
						DeviceInfo b=busLineMRID.get(i+"");
						BusLineInfo bli=null;
						for(BusLineInfo bi:tempBusLineLow2){
							String no=bi.getSortNo();
							if(no.equals(i+"")){
								bli=bi;
								bli.setId(b.getId());
								bli.setName(b.getName());
								busLineLow.add(addBusLineLow(bli));
								continue;
							}
						}
						
					}
					
					fouthFindDeviceByConnId(connIdNew,ti);
					
					for(int i=0;i<breakerMRID.size();i++){
						String id=breakerMRID.get(i);
						DeviceInfo br=null;
						BusLineInfo bli=null;
						ok:for(DeviceInfo bi:breakerCircle){
							if(id.equals(bi.getId())){
								br=bi;
								break ok;
							}
						}
						
						double xx=0;
						double yy=0;
						if(i==0){
							ok2:for(DeviceInfo d:breakerCircle){
								if(d.getId().equals(breakerMRID.get(1))){
									br.setX(d.getX());
									br.setY(d.getY()-16);
									br.setOrientation(OrientationType.NORTH);
									list.add(br.withVLineStr(br, d));
									break ok2;
								}
							}
						}
						else if(i==1){
							
						}
						
						else if(i==2){
							System.out.println("------------22-------------");
							for(BusLineInfo b:busLineLow){
								if(b.getPosition().equals(OrientationType.rightUp)){
									bli=b;
								}
							}
							xx=bli.getX();
							yy=bli.getY();
							br.setX(xx);
							br.setY(yy);
							DeviceInfo rightUp=br.withVLineStr(br, bli);
							list.add(rightUp);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(busLineLow.get(1).getId())!=null){
								bridge=bridges.get(busLineLow.get(1).getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("right", br);
							bridges.put(busLineLow.get(1).getId(),bridge);
						}
						//3
						else if(i>2 &&i <(busLineLow.size()-1) && i%2==1){
							System.out.println("------------3-------------");

							bli=busLineLow.get(i/2);
							//xx=bli.getX();
							xx=bli.getX()+bli.getWidth()-breakerToBreakerDistance;
							yy=bli.getY();
							bli.setOrientation(OrientationType.NORTH);
							br.setX(xx);
							br.setY(yy);
							DeviceInfo b=br.withVLineStr(br, bli);
							list.add(b);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(bli.getId())!=null){
								bridge=bridges.get(bli.getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("left", br);
							bridges.put(bli.getId(),bridge);
						}
						//4
						else if(i>2 &&i <(busLineLow.size()-1) && i%2==0){
							System.out.println("------------4-------------");

							bli=busLineLow.get(i/2-1);
							xx=bli.getX();
							//xx=bli.getX()+bli.getWidth()-breakerToBreakerDistance;
							yy=bli.getY();
							bli.setOrientation(OrientationType.NORTH);
							br.setX(xx);
							br.setY(yy);
							DeviceInfo b=br.withVLineStr(br, bli);
							list.add(b);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(busLineLow.get(i/2).getId())!=null){
								bridge=bridges.get(busLineLow.get(i/2).getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("right", br);
							bridges.put(busLineLow.get(i/2).getId(),bridge);
						}
						
						
						//5
						else if(i==(busLineLow.size()-1)){
							System.out.println("------------33-------------");
							for(BusLineInfo b:busLineLow){
								if(b.getPosition().equals(OrientationType.leftUp)){
									bli=b;
								}
							}
							xx=bli.getX()+bli.getWidth()-breakerToBreakerDistance;
							yy=bli.getY();
							br.setX(xx);
							br.setY(yy);
							
							DeviceInfo leftUp=br.withVLineStr(br, bli);
							list.add(leftUp);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(bli.getId())!=null){
								bridge=bridges.get(bli.getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("left", br);
							bridges.put(bli.getId(),bridge);
						}
						//6
						else if(i==busLineLow.size()){
							OK4:for(DeviceInfo d:breakerCircle){
								if(d.getId().equals(breakerMRID.get(5))){
									br.setX(d.getX());
									br.setY(d.getY()+16);
									br.setOrientation(OrientationType.SOUTH);
									list.add(br.withVLineStr(br, d));
									break OK4;
								}
							}
						}
						
						
						
						//8
						else if(i==(busLineLow.size()+2)){
							System.out.println("------------66-------------");
							for(BusLineInfo b:busLineLow){
								if(b.getPosition().equals(OrientationType.leftDown)){
									bli=b;
								}
							}
							xx=bli.getX()+bli.getWidth()-breakerToBreakerDistance;
							yy=bli.getY();
							br.setX(xx);
							br.setY(yy);
							DeviceInfo leftDown=br.withVLineStr(br, bli);
							list.add(leftDown);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(bli.getId())!=null){
								bridge=bridges.get(bli.getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("right", br);
							bridges.put(bli.getId(),bridge);
						}
						
						//9
						else if(i>(busLineLow.size()+2) &&i <(busLineLow.size()*2-1) && i%2==1){
							System.out.println("------------9-------------");

							bli=busLineLow.get(i/2);
							//xx=bli.getX();
							xx=bli.getX()+bli.getWidth()-breakerToBreakerDistance;
							yy=bli.getY();
							bli.setOrientation(OrientationType.SOUTH);
							br.setX(xx);
							br.setY(yy);
							DeviceInfo b=br.withVLineStr(br, bli);
							list.add(b);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(busLineLow.get(i/2-1).getId())!=null){
								bridge=bridges.get(busLineLow.get(i/2-1).getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("right", br);
							bridges.put(busLineLow.get(i/2-1).getId(),bridge);
						}
						//10
						else if(i>(busLineLow.size()+2) &&i <(busLineLow.size()*2-1) && i%2==0){
							System.out.println("------------10-------------");
							bli=busLineLow.get(i/2-1);
							xx=bli.getX();
							//xx=bli.getX()+bli.getWidth()-breakerToBreakerDistance;
							yy=bli.getY();
							bli.setOrientation(OrientationType.SOUTH);
							br.setX(xx);
							br.setY(yy);
							DeviceInfo b=br.withVLineStr(br, bli);
							list.add(b);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(bli.getId())!=null){
								bridge=bridges.get(bli.getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("left", br);
							bridges.put(bli.getId(),bridge);
						}
						
						
						
						//11
						else if(i==(busLineLow.size()*2-1)){
							System.out.println("------------77-------------");
							for(BusLineInfo b:busLineLow){
								if(b.getPosition().equals(OrientationType.rightDown)){
									bli=b;
								}
							}
							xx=bli.getX();
							yy=bli.getY();
							br.setX(xx);
							br.setY(yy);
							DeviceInfo rightDown=br.withVLineStr(br, bli);
							list.add(rightDown);
							Map<String,DeviceInfo> bridge=null;
							if(bridges.get(busLineLow.get(busLineLow.size()-2).getId())!=null){
								bridge=bridges.get(busLineLow.get(busLineLow.size()-2).getId());
							}else{
								bridge=new HashMap<String,DeviceInfo>();
							}
							bridge.put("left", br);
							bridges.put(busLineLow.get(busLineLow.size()-2).getId(),bridge);
						}
					}
					
				}
			}

		}if (breakerMapList!=null  && breakerMapList.size()!=0){
			if(busbarSectionList.size()>0&&!deviceInfo.getType().equals(DeviceType.BUSLINE)){
				return;
			}
			int count=0;
			for(int i=0;i<breakerMapList.size();i++){
				count++;
				Map breakerMap=breakerMapList.get(i);
				if(breakerMap.get("id").equals(deviceInfo.getId())){
					continue;
				}
				String deviceType=breakerMap.get("type").toString();
				DeviceInfo df=null;
				if(deviceType.equals(DeviceType.DISCONNECTOR)){
					df=new DisconnectorInfo();
				}else{
					df=new BreakerInfo();
				}
				df.setId(breakerMap.get("id").toString());
				df.setName(breakerMap.get("name").toString());
				df.setBelongBusNo((deviceInfo).getBelongBusNo());
				if(deviceInfo.getType().equals(DeviceType.BUSLINE)){
					df.setBelongBreakerNo(df.getId());
				}else{
					df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
				}
				df.setType(deviceType);
				double pDeviceX=deviceInfo.getX();
				double pDeviceY=deviceInfo.getY();
				double x=pDeviceX;
				if(deviceInfo.getType().equals(DeviceType.BUSLINE)){
					
					x=pDeviceX+(count)*breakerToBreakerDistance;
				}
				double y=pDeviceY;
				df.setY(y);
				df.setX(x);
				//判断OrientationType
				if(deviceInfo.getType().equals(DeviceType.BUSLINE)){
					boolean toTran=false;
					for(String t:toTranBreakerNo){
						if(t.equals(df.getId())){
							toTran=true;
						}
					}
					if(toTran){
						df.setOrientation(OrientationType.SOUTH);
					}else{
						df.setOrientation(OrientationType.NORTH);
					}
				}else{
					df.setOrientation(deviceInfo.getOrientation());
				}
				//判断是否是重复的breaker
				boolean isRepeat=false;
				for(String s:differentBelongBreakerNo){
					if(df.getBelongBreakerNo().equals(s)){
						isRepeat=true;
					}
				}
				//判断从母线出来的breaker在breakernoToSortno中对应的sortno在join中是否存在
				boolean b=false;
				if(deviceInfo.getType().equals(DeviceType.BUSLINE)&&isRepeat){
					//count--;
					df.setX(pDeviceX+deviceInfo.getWidth()-breakerToBreakerDistance);
					for(String s:join){
						if(s.equals(breakernoToSortno.get(df.getBelongBreakerNo()))){
							b=true;
							count--;
							break;
						}
					}
					if(b){
						list.add(df.withVLineStr(df,deviceInfo));
						String connIdNew=GetAnotherNodeStr(breakerMap,connId);
						if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
							//continue;
						}else{
							secondFindDeviceByConnId(connIdNew,df);
						}
					}
				}else{
					list.add(df.withVLineStr(df,deviceInfo));
					String connIdNew=GetAnotherNodeStr(breakerMap,connId);
					if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
						//continue;
					}else{
						secondFindDeviceByConnId(connIdNew,df);
					}
				}
			}
		}
		if(disconnectorList!=null  && disconnectorList.size()!=0){
		}
	}
	//第三次递归
		public void thirdFindDeviceByConnId(String connId,DeviceInfo deviceInfo,List<String> breakerMRID,Map<String,DeviceInfo> busLineMRID,List<String> breakerAndBusLineMRID,boolean circle){
			List<BusbarSection> busbarSectionList = getBusByConnId(connId);
			
			List<Breaker> breakerList = getBreakerByConnId(connId);
			
			List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);
			
			List<Map<String, String>> breakerMapList = joinBreakerData(breakerList, disconnectorList);
			
			List<ACLineDot> lineList = getLineByConnId(connId);
			
			List<PowerTransformer> powerTransformerList = getTranByConnId(connId);
			
			if(powerTransformerList.size()!=0 && powerTransformerList != null && !DeviceType.TRANSFORMER.equals(deviceInfo.getType())){
				
			
			}if(busbarSectionList!=null && busbarSectionList.size()!=0){
				
					BusbarSection bs=busbarSectionList.get(0);
					if(doubleBusIsConnected && busbarSectionList.size()==2){
						bs=busbarSectionList.get(1);
					}
					if(busbarSectionList.size()==2 && doubleBusIsConnected==false){
						doubleBusIsConnected=true;
					}
					if(bs.getMRID().equals(deviceInfo.getId())){
					}else{
						BusLineInfo bli=new BusLineInfo();
						bli.setId(bs.getMRID());
						bli.setType(DeviceType.BUSLINE);
						bli.setName(bs.getName());
						bli.setParentDeviceInfo(deviceInfo);
						if(busConnType==BusConnType.SINGLE){
							List<String> joinBreakers=new ArrayList<String>();
							int limitCount=0;
							findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,0,limitCount);
							Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
							boolean ContactAnotherBus=false;
							while(it.hasNext()){
								String key=it.next().toString();
								ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
									if(deviceInfo.getId().equals(value.get(0))){
										
										ContactAnotherBus=true;
										break;
									}
							}
							if(ContactAnotherBus){
								return;	
							}
							
						}else if(busConnType==BusConnType.DOUBLE && circle){
							boolean b=false;
							for(String s:breakerAndBusLineMRID){
								if(s.equals(bli.getId())){
									b=true;
								}
							}
							if(!b){
								if(busLineMRID.size()==2){
									corners.add(deviceInfo.getParentDeviceInfo().getParentDeviceInfo().getParentDeviceInfo());
									corners.add(deviceInfo.getParentDeviceInfo().getParentDeviceInfo());
								}
								
								breakerMRID.add(deviceInfo.getParentDeviceInfo().getId());
								breakerMRID.add(deviceInfo.getId());
								
								busLineMRID.put(busLineMRID.size()+"",bli);
								breakerAndBusLineMRID.add(bli.getId());
								breakerAndBusLineMRID.add(deviceInfo.getId());
								breakerAndBusLineMRID.add(deviceInfo.getParentDeviceInfo().getId());
								//thirdFindDeviceByConnId(connIdNew,bli,breakerMRID,busLineMRID,breakerAndBusLineMRID);
							}else{
								corners.add(deviceInfo.getParentDeviceInfo().getParentDeviceInfo().getParentDeviceInfo());
								corners.add(deviceInfo.getParentDeviceInfo().getParentDeviceInfo());
								return;
							}
						}else if(busConnType==BusConnType.FIXED){
							    String orien=deviceInfo.getOrientation();
							    if(orien==null){
							    	
							    	for(BusbarSection buse:startBusList){
							    		if(buse.getMRID().equals(bli.getId())){
							    			orien=OrientationType.NORTH;
							    			break;
							    		}
							    	}
							    	for(BusbarSection buse:endBusList){
							    		if(buse.getMRID().equals(bli.getId())){
							    			orien=OrientationType.SOUTH;
							    			break;
							    		}
							    	}
							    	
							    }
							    if(orien.equals(OrientationType.SOUTH)){
							    	//如果是双母接线
							    	if(doubleTranForFixedLow.size()>=1 && doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){
										//第一次碰到母线
										if(deviceInfo.getBelongTranConnId()!=null){
											System.out.println(bli.getId()+"bli.getName="+bli.getName());
											busIdTostartBreakerIdForLow.put(bli.getId(), deviceInfo.getBelongBreakerNo());
											busIdToLastBreakerInfoForLow.put(bli.getId().toString(),deviceInfo);
											busIdToConnIdLow.put(bli.getId(),deviceInfo.getBelongTranConnId());
										//第二次
										}else{
											System.out.println("fffffffffffffff第二次进入双母");
											System.out.println(bli.getId()+"bli.getName="+bli.getName());
											List<String> joinBreakers=new ArrayList<String>();
											singleIdToDoubleIdForLow.put(bli.getId(),deviceInfo.getBelongBusNo());
											int limitCount=0;
											findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,0,limitCount);
											return;
										}
									}
									else{
								    	if(deviceInfo.getBelongTranConnId()!=null){
								    		busIdToConnIdLow.put(bli.getId(),deviceInfo.getBelongTranConnId());
									    	busIdToLastBreakerInfoForLow.put(bli.getId().toString(),deviceInfo);
								    	}else{
								    		return;
								    	}
									}	
							    }else if(orien.equals(OrientationType.NORTH)){
							    	if(deviceInfo.getBelongTranConnId()!=null){
							    		/*busIdToConnIdLow.put(bli.getId(),deviceInfo.getBelongTranConnId());
								    	busIdToLastBreakerInfoForLow.put(bli.getId().toString(),deviceInfo);*/
							    		return;
							    	}else{
							    		List<String> joinBreakers=new ArrayList<String>();
							    		int limitCount=0;
										findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,0,limitCount);
							    		return;
							    	}
							    }
						}
					}
			}if (breakerMapList!=null  && breakerMapList.size()!=0){
				
				//从TRANSFORMER发出
				if((deviceInfo.getType().equals(DeviceType.TRANSFORMER))){
					
					if(breakerMapList.size()==1){
						if(busConnType==BusConnType.DOUBLE){
							busConnType=BusConnType.SINGLE;
						}
						/*Map breakerMap=breakerMapList.get(0);
						String connIdNew=GetAnotherNodeStr(breakerMap,connId);
						thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID);*/
					}
					
					if(busConnType==BusConnType.SINGLE||((busConnType==BusConnType.DOUBLE) && circle)){
						Map breakerMap=breakerMapList.get(0);
						if(busConnType==BusConnType.DOUBLE){
							//和第四次递归走同一条路线
							sameCircleFlag=breakerMap.get("id").toString();
						}
						if(breakerMap.get("id").equals(deviceInfo.getId())){
						}else{
							String connIdNew=GetAnotherNodeStr(breakerMap,connId);
							DeviceInfo df=null;
							String deviceType=breakerMap.get("type").toString();
							if(deviceType.equals(DeviceType.DISCONNECTOR)){
								df=new DisconnectorInfo();
							}else{
								df=new BreakerInfo();
							}
							df.setId(breakerMap.get("id").toString());
							df.setType(deviceType);
							df.setName(breakerMap.get("name").toString());
							df.setParentDeviceInfo(deviceInfo);
							
							if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
								return;
							}
							//breakerAndBusLineMRID中是否将出现重复
							boolean b=false;
							for(String s:breakerAndBusLineMRID){
								if(s.equals(df.getId())){
									b=true;
								}
							}
							if(!b){
							thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID,circle);
							}
						
					}
				}else if(busConnType==BusConnType.FIXED){
					if(breakerMapList.size()==1){
						Map breakerMap=breakerMapList.get(0);
						if(breakerMap.get("id").equals(deviceInfo.getId())){
						}else{
							System.out.println("11  "+deviceInfo.getId()+"11来自tran一个设备 deviceInfo.getName()="+deviceInfo.getName());
							String connIdNew=GetAnotherNodeStr(breakerMap,connId);
							DeviceInfo df=null;
							String deviceType=breakerMap.get("type").toString();
							if(deviceType.equals(DeviceType.DISCONNECTOR)){
								df=new DisconnectorInfo();
							}else{
								df=new BreakerInfo();
							}
							df.setId(breakerMap.get("id").toString());
							df.setType(deviceType);
							df.setName(breakerMap.get("name").toString());
							df.setParentDeviceInfo(deviceInfo);
							df.setBelongTranNo(deviceInfo.getId());
							df.setBelongTranConnId(connId);
							df.setOrientation(deviceInfo.getOrientation());
							System.out.println("11来自tran df.getName()="+df.getName());
							//df.setBelongBreakerNo(df.getId());
							//System.out.println("11 deviceInfo.getBelongBreakerNo()="+deviceInfo.getBelongBreakerNo());
							if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
								//return;
							}else{
								thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID,circle);
							}
						}
						//从tran出来后直接分支
					}else if(breakerMapList.size()==2){
						
						doubleTranForFixedLow.add(deviceInfo.getId());
						//为防止回溯的doubleBreakers填充数据
						for(int i=0;i<breakerMapList.size();i++){
							Map breakerMap=breakerMapList.get(i);
							if(breakerMap.get("id").equals(deviceInfo.getId())){
							}else{
								doubleBreakers.add(breakerMap.get("id").toString());
							}
						}
						for(int i=0;i<breakerMapList.size();i++){
							Map breakerMap=breakerMapList.get(i);
							if(breakerMap.get("id").equals(deviceInfo.getId())){
							}else{
								String connIdNew=GetAnotherNodeStr(breakerMap,connId);
								DeviceInfo df=null;
								String deviceType=breakerMap.get("type").toString();
								if(deviceType.equals(DeviceType.DISCONNECTOR)){
									df=new DisconnectorInfo();
								}else{
									df=new BreakerInfo();
								}
								df.setId(breakerMap.get("id").toString());
								df.setType(deviceType);
								df.setName(breakerMap.get("name").toString());
								df.setParentDeviceInfo(deviceInfo);
								df.setBelongTranNo(deviceInfo.getId());
								System.out.println("33  deviceInfo.getId()="+deviceInfo.getId()+"33来自tran两个设备  deviceInfo.getName()="+deviceInfo.getName());
								System.out.println("33 df.getName()="+df.getName());
								df.setBelongTranConnId(connId);
								df.setBelongBreakerNo(df.getId());
								System.out.println("33 df.getBelongBreakerNo()="+df.getBelongBreakerNo());
								if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
									//return;
								}else{
									thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID,circle);
								}
							}
						}
					}
				}
					//从起始breaker发出
				}else if(breakerMapList.size()>=1 && !deviceInfo.getType().equals(DeviceType.TRANSFORMER)){
					if(busConnType==BusConnType.SINGLE||((busConnType==BusConnType.DOUBLE) && circle)){
							for(Map breakerMap:breakerMapList){
								if(breakerMap.get("id").equals(deviceInfo.getId())){
									continue;
								}else{
									DeviceInfo df=null;
									String deviceType=breakerMap.get("type").toString();
									if(deviceType.equals(DeviceType.DISCONNECTOR)){
										df=new DisconnectorInfo();
									}else{
										df=new BreakerInfo();
									}
									df.setId(breakerMap.get("id").toString());
									df.setType(deviceType);
									df.setName(breakerMap.get("name").toString());
									df.setParentDeviceInfo(deviceInfo);
									df.setOrientation(deviceInfo.getOrientation());
									String connIdNew=GetAnotherNodeStr(breakerMap,connId);
									if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
									}else{
										thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID,circle);
									}
								}
							}
						}else if(busConnType==BusConnType.FIXED){
							//经过几个breaker后出现分支
							if(breakerMapList.size()==3 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
								System.out.println("3 deviceInfo.getName()3="+deviceInfo.getName());
								doubleTranForFixedLow.add(deviceInfo.getBelongTranNo());
								//为防止回溯的doubleBreakers填充数据
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										doubleBreakers.add(breakerMap.get("id").toString());
									}
								}
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setBelongTranNo(deviceInfo.getBelongTranNo());
										df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
										df.setOrientation(deviceInfo.getOrientation());
										df.setBelongBreakerNo(df.getId());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}else{
											thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID,circle);
										}
									}
								}
								//第一次碰到母线
							}else if(busbarSectionList.size()>0 && deviceInfo.getBelongTranNo()!=null){
								//只有双母才会走这段代码
								if(doubleTranForFixedLow.size()==1){
									if(doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){
										System.out.println("breakerMapList.size()="+breakerMapList.size());
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											boolean repeatEver=false;
											for(String breakerId:doubleBreakers){
												if(breakerMap.get("id").equals(breakerId)){
													repeatEver=true;
												}
											}
											if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setBelongBusNo(busbarSectionList.get(0).getMRID());
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setOrientation(deviceInfo.getOrientation());
												//df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setBelongBreakerNo(df.getBelongBreakerNo());
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID,circle);
												}
											}
										}
									}
								}
								//变压器之后或母线之后碰到母线之前有多个设备
							}else if(breakerMapList.size()==2 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
								System.out.println("22一个设备 deviceInfo.getName()="+deviceInfo.getName());
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									boolean repeatEver=false;
									if(deviceInfo.getBelongTranConnId()==null){
										for(String breakerId:doubleBreakers){
											if(breakerMap.get("id").equals(breakerId)){
												repeatEver=true;
											}
										}
									}
									if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
										queryAndAddLine(deviceInfo);
									}else{
										
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setBelongTranNo(deviceInfo.getBelongTranNo());
										df.setOrientation(deviceInfo.getOrientation());
										if(deviceInfo.getBelongTranConnId()!=null){
											//为防止回溯的doubleBreakers填充数据
											doubleBreakers.add(breakerMap.get("id").toString());
										}
										if(deviceInfo.getBelongTranConnId()!=null)
										System.out.println("22  deviceInfo.getBelongTranConnId()="+deviceInfo.getBelongTranConnId());	
										df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
										System.out.println("22 df.getName()="+df.getName());
										if(deviceInfo.getBelongBreakerNo()!=null){
											df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
										}
										if(deviceInfo.getBelongBusNo()!=null){
											df.setBelongBusNo(deviceInfo.getBelongBusNo());
										}
										//df.setBelongBreakerNo(df.getBelongBreakerNo());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}else{
											thirdFindDeviceByConnId(connIdNew,df,breakerMRID,busLineMRID,breakerAndBusLineMRID,circle);
										}
									}
								}
							}
						}
					}
				
					if(busbarSectionList.size()>0){
						String mrid=busbarSectionList.get(0).getMRID();
						for(BusbarSection s:startBusList){
							if(s.getMRID().equals(mrid)){
								if(breakerMapList.size()>maxBreakerNumForAllHigh)
								maxBreakerNumForAllHigh=breakerMapList.size();
								break;
							}
						}
						
						for(BusbarSection s:endBusList){
							if(s.getMRID().equals(mrid)){
								if(breakerMapList.size()>maxBreakerNumForAllLow)
								maxBreakerNumForAllLow=breakerMapList.size();
								break;
							}
						}
						
					}
				}
								
			}
		
		//第四次递归
				public void fouthFindDeviceByConnId(String connId,DeviceInfo deviceInfo){
					String type= deviceInfo.getType();
					String busNo=null;
					Map<String,Object> result = new HashMap<String, Object>();
					List<BusbarSection> busbarSectionList = getBusByConnId(connId);
					
					List<Breaker> breakerList1 = getBreakerByConnId(connId);
					
					List<Disconnector> disconnectorList1 = getDisconnectorByConnId(connId);
					
					List<Map<String, String>> breakerMapList = joinBreakerData(breakerList1, disconnectorList1);
					
					List<ACLineDot> lineList = getLineByConnId(connId);
					
					List<PowerTransformer> powerTransformerList = getTranByConnId(connId);
					if(lineList != null && lineList.size()!=0 ){
						ACLineDot ad=lineList.get(0);
						LineInfo li=new LineInfo();
						li.setId(ad.getMRID());
						li.setName(ad.getName());
						li.setOrientation(deviceInfo.getOrientation());
						li.setX(deviceInfo.getX());
						li.setY(deviceInfo.getY());
						li.setSvgStr(li.svgStr());
						list.add(li);
					}
					if( powerTransformerList != null && powerTransformerList.size()!=0 && !DeviceType.TRANSFORMER.equals(deviceInfo.getType())){
					/*	PowerTransformer ptf=powerTransformerList.get(0);
						TransformerInfo ti=new TransformerInfo();
						ti.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
						ti.setBelongBusNo(deviceInfo.getBelongBusNo());
						ti.setId(ptf.getMRID());
						ti.setName(ptf.getName());
						ti.setOrientation(deviceInfo.getOrientation());
						ti.setType(DeviceType.TRANSFORMER);
						double pDeviceX=deviceInfo.getX();
						double pDeviceY=deviceInfo.getY();
						ti.setX(pDeviceX);
						ti.setY(pDeviceY);
						ti.setSvgStr(ti.svgStr());
						//ti.withVLineStr(ti,deviceInfo);
						//ti.withALineStr(deviceInfo);
						
						list.add(ti.withVLineStr(ti,deviceInfo));*/
						
					
					}if(busbarSectionList!=null && busbarSectionList.size()!=0){
						if(!deviceInfo.getType().equals(DeviceType.BUSLINE)){
							BusbarSection bs=busbarSectionList.get(0);
							
							if(doubleBusIsConnected && busbarSectionList.size()==2){
								bs=busbarSectionList.get(1);
							}
							
							BusLineInfo bli=new BusLineInfo();
							bli.setId(bs.getMRID());
							bli.setName(bs.getName());
							bli.setType(DeviceType.BUSLINE);
							bli.setParentDeviceInfo(deviceInfo);
							if(busConnType==BusConnType.SINGLE){
								if(!bs.getMRID().equals(deviceInfo.getId())){
									Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
									boolean ContactAnotherBus=false;
									while(it.hasNext()){
										String key=it.next().toString();
										ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
										//for(String v:value){
											if(deviceInfo.getId().equals(value.get(0))){
												ContactAnotherBus=true;
												break;
											}
										//}
									}
									if(ContactAnotherBus){
										busNoToBreakerInfoForLow.put(bs.getMRID(), deviceInfo);
										return;	
									}
									bli.setWidth(maxBreakerNumForAllLow*breakerToBreakerDistance);
									bli.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance/2+deviceInfo.getWidth()/2);
									bli.setY(deviceInfo.getY()+deviceInfo.getHeight());
									bli.setOrientation(OrientationType.SOUTH);
									bli.setColor("yellow");
									busLineCountLow++;
									if(busLineCountLow!=1){
										bli.setNotFirst(true);
										bli.setAssisHeight(busNoToBreakerInfoForLow.get(bli.getId()).getY());
										bli.setAssisWidth(busNoToBreakerInfoForLow.get(bli.getId()).getX());
									}
									bli.setSvgStr(bli.svgStrLow());
									list.add(bli.withVLineStr(bli, deviceInfo));
								}
							}else if(busConnType==BusConnType.DOUBLE){
								if(!bs.getMRID().equals(deviceInfo.getId())){
									//为所属母线id赋值
									busNo=bs.getMRID();
									busLineCountLow++;
									//thirdFindDeviceByConnId(connIdNew,bli,breakerMRID,busLineMRID,breakerAndBusLineMRID);
								}
							}else if(busConnType==BusConnType.FIXED){
								String orien=deviceInfo.getOrientation();
								if(orien.equals(OrientationType.SOUTH)){
									//如果是双母接线
									if(doubleTranForFixedLow.size()>=1 && doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){
										//第一次碰到母线
										if(deviceInfo.getBelongTranConnId()!=null){
											
											System.out.println("4fffffffffffffff第一次进入双母");
											
											bli.setWidth(maxBreakerNumForAllLow*breakerToBreakerDistance);
											if(bli.getId().equals(sortedBusIdLow.get(1))){
												bli.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance+deviceInfo.getWidth()/2);

											}else if(bli.getId().equals(sortedBusIdLow.get(2))){
												bli.setDoubleConnDistance(true);
												bli.setX(deviceInfo.getX()+deviceInfo.getWidth()/2);
											}
											bli.setY(deviceInfo.getY()+deviceInfo.getHeight());
											bli.setOrientation(OrientationType.SOUTH);
											bli.setColor("yellow");
											busLineCountLow++;
											bli.setNotFirst(true);
											
											if(busNoToBreakerInfoForLow.get(bli.getId())!=null){
												bli.setAssisHeight(busNoToBreakerInfoForLow.get(bli.getId()).getY());
												bli.setAssisWidth(busNoToBreakerInfoForLow.get(bli.getId()).getX());
												bli.setSvgStr(bli.svgStrLow());
												list.add(bli.withVLineStr(bli, deviceInfo));
											}else{
												if(doubleBusIsConnected && busbarSectionList.size()==2){
													bli.setSvgStr(bli.svgStrLow());
													list.add(bli.withVLineStr(bli, deviceInfo));
												}else{
													doubleRightBusLineLow.put(bli,deviceInfo);
												}
											}
										
											
											/*busIdTostartBreakerIdForLow.put(bli.getId(), deviceInfo.getBelongBreakerNo());
											busIdToLastBreakerInfoForLow.put(bli.getId().toString(),deviceInfo);
											busIdToConnIdLow.put(bli.getId(),deviceInfo.getBelongTranConnId());*/
										//第二次
										}else{
											System.out.println("4fffffffffffffff第二次进入双母");
											/*List<String> joinBreakers=new ArrayList<String>();
											singleIdToDoubleIdForLow.put(bli.getId(),deviceInfo.getBelongBusNo());
											findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers);*/
											return;
										}
									}
									else{
								    	if(deviceInfo.getBelongTranConnId()!=null){
								    		//busIdToConnIdLow.put(bli.getId(),deviceInfo.getBelongTranConnId());
									    	//busIdToLastBreakerInfoForLow.put(bli.getId().toString(),deviceInfo);
									    	bli.setWidth(maxBreakerNumForAllLow*breakerToBreakerDistance);
											bli.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance/2+deviceInfo.getWidth()/2);
											bli.setY(deviceInfo.getY()+deviceInfo.getHeight());
											bli.setOrientation(OrientationType.SOUTH);
											bli.setColor("yellow");
											busLineCountLow++;
											//System.out.println("busLineCountLow="+busLineCountLow);
											//if(busLineCountLow!=1){
												//bli.setNotFirst(true);
												//bli.setAssisHeight(busNoToBreakerInfoForLow.get(bli.getId()).getY());
												//bli.setAssisWidth(busNoToBreakerInfoForLow.get(bli.getId()).getX());
											//}
											bli.setSvgStr(bli.svgStrLow());
											list.add(bli.withVLineStr(bli, deviceInfo));
								    	}else{
								    		busNoToBreakerInfoForLow.put(bs.getMRID(), deviceInfo);
								    		return;
								    	}
							    	    
									}
									
								}else if(orien.equals(OrientationType.NORTH)){
									bli.setWidth(maxBreakerNumForAllHigh*breakerToBreakerDistance);
									bli.setX(deviceInfo.getX()-maxBreakerNumForAllHigh*breakerToBreakerDistance/2+deviceInfo.getWidth()/2);
									bli.setY(deviceInfo.getY()-deviceInfo.getHeight());
									bli.setOrientation(OrientationType.NORTH);
									bli.setColor("red");
									bli.setSvgStr(bli.svgStrLow());
									list.add(bli.withVLineStr(bli, deviceInfo));
								}
							}
								
							
						}
					}if (breakerMapList!=null  && breakerMapList.size()!=0){
						if(busConnType==BusConnType.SINGLE){
							
							if(breakerMapList.size()==1 &&!breakerMapList.get(0).get("id").equals(deviceInfo.getId()) &&deviceInfo.getType().equals(DeviceType.TRANSFORMER)){
								Map breakerMap=breakerMapList.get(0);
								String connIdNew=GetAnotherNodeStr(breakerMap,connId);
								DeviceInfo df=null;
								String deviceType=breakerMap.get("type").toString();
								if(deviceType.equals(DeviceType.DISCONNECTOR)){
									df=new DisconnectorInfo();
								}else{
									df=new BreakerInfo();
								}
								df.setId(breakerMap.get("id").toString());
								df.setType(deviceType);
								df.setName(breakerMap.get("name").toString());
								df.setParentDeviceInfo(deviceInfo);
								df.setX(deviceInfo.getX());
								df.setY(deviceInfo.getY());
								df.setOrientation(deviceInfo.getOrientation());
								if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
									//return;
								}
								list.add(df.withVLineStr(df, deviceInfo));
								fouthFindDeviceByConnId(connIdNew,df);
								
							}
							else if(breakerMapList.size()==2){
								
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}
									else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setX(deviceInfo.getX());
										df.setY(deviceInfo.getY());
										df.setOrientation(deviceInfo.getOrientation());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}
										list.add(df.withVLineStr(df, deviceInfo));
										fouthFindDeviceByConnId(connIdNew,df);
									}
								}
							}else if(breakerMapList.size()==3){
								int count=0;
								for(int i=0;i<breakerMapList.size();i++){
									count++;
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
										count--;
									}else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										
										df.setOrientation(deviceInfo.getOrientation());
										if(count==2&&!deviceInfo.getType().equals(DeviceType.BUSLINE)){
											df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
											df.setAssistBottomLine(true);
										}else {
											df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
										}
										df.setY(deviceInfo.getY());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}
										list.add(df.withVLineStr(df, deviceInfo));
										fouthFindDeviceByConnId(connIdNew,df);
									}
								
								}
							}
							 else if(breakerMapList.size()>3){
								int count=0;
								for(int i=0;i<breakerMapList.size();i++){
									count++;
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
										count--;
									}else{
										String startJoinId=null;
											if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID())!=null){
												startJoinId=busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1);
											}
										Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
										boolean otherBusStartJoinId=false;
										while(it.hasNext()){
											String key=it.next().toString();
											ArrayList<String> value=(ArrayList<String>) busIdToJoinBreakersForSingleLow.get(key);
											//for(String v:value){
												if(breakerMap.get("id").equals(value.get(0))){
													
													otherBusStartJoinId=true;
												}
											//}
										}
										if(otherBusStartJoinId){
											count--;
											continue;
										}
										
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										
										df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance/2+count*breakerToBreakerDistance);
										if(startJoinId!=null && breakerMap.get("id").equals(startJoinId)){
											df.setX(deviceInfo.getX()+maxBreakerNumForAllLow*breakerToBreakerDistance/2);
											count--;
										}
										df.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
										df.setOrientation(deviceInfo.getOrientation());
										df.setBelongBusNo(deviceInfo.getBelongBusNo());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}
										list.add(df.withVLineStr(df, deviceInfo));
										fouthFindDeviceByConnId(connIdNew,df);
									}
							}
						}
					}else if(busConnType==BusConnType.DOUBLE && busLineCountLow<=busLineMRID.size()){
						//从TRANSFORMER发出
						if((deviceInfo.getType().equals(DeviceType.TRANSFORMER))){
							Map breakerMap=breakerMapList.get(0);
							for(Map m:breakerMapList){
								if(sameCircleFlag.equals(m.get("id"))){
									breakerMap=m;
								}
							}
							if(breakerMap.get("id").equals(deviceInfo.getId())){
							}else{
								String connIdNew=GetAnotherNodeStr(breakerMap,connId);
								DeviceInfo df=null;
								String deviceType=breakerMap.get("type").toString();
								if(deviceType.equals(DeviceType.DISCONNECTOR)){
									df=new DisconnectorInfo();
								}else{
									df=new BreakerInfo();
								}
								df.setId(breakerMap.get("id").toString());
								df.setType(deviceType);
								df.setParentDeviceInfo(deviceInfo);
								
								if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
									//return;
								}
								fouthFindDeviceByConnId(connIdNew,df);
								}
							}
							//从起始breaker发出
						else if( !deviceInfo.getType().equals(DeviceType.TRANSFORMER)){
							BusLineInfo bli=null;
							boolean c=false;
							if(busNo!=null){
								//breakerMRID
								for(BusLineInfo b:busLineLow){
									if(busNo==b.getId()){
										bli=b;
										c=true;
									}
								}
							}
							
							int count=0;
							for(int i=0;i<breakerMapList.size();i++){
								count++;
								
								Map breakerMap=breakerMapList.get(i);
								if(breakerMap.get("id").equals(deviceInfo.getId())){
									queryAndAddLine(deviceInfo);
									count--;
									continue;
								}else{
									DeviceInfo df=null;
									String deviceType=breakerMap.get("type").toString();
									if(deviceType.equals(DeviceType.DISCONNECTOR)){
										df=new DisconnectorInfo();
									}else{
										df=new BreakerInfo();
									}
									df.setId(breakerMap.get("id").toString());
									df.setType(deviceType);
									df.setName(breakerMap.get("name").toString());
									df.setParentDeviceInfo(deviceInfo);
									//corners中是否存在，判断是否需要剔除
									boolean inCorners=false;
									for(DeviceInfo s:corners){
										String id=s.getId();
										if(df.getId().equals(id)){
											inCorners=true;
											break;
										}
									}
									//breakerMRID中是否存在,判断是否需要剔除
									boolean inBreakerMRID=false;
									for(String s:breakerMRID){
										if(s.equals(df.getId())){
											inBreakerMRID=true;
										}
									}
									if(inCorners){
										BusLineInfo bus=new BusLineInfo();
										if(df.getId().equals(corners.get(0).getId()))
										{
											
											for(BusLineInfo bb:busLineLow){
												if(bb.getPosition().equals(OrientationType.leftUp)){
													bus=bb;
													break;
												}
											}
											df.setX(bus.getX());
											df.setY(bus.getY());
											df.setOrientation(OrientationType.NORTH);
											list.add(df.withVLineStr(df, bus));
											count--;
											
										}
										if(df.getId().equals(corners.get(1).getId())){
											df.setX(deviceInfo.getX());
											df.setY(deviceInfo.getY());
											df.setOrientation(OrientationType.NORTH);
											//if(deviceInfo.getX()>1300){
												list.add(df.withVLineStr(df, deviceInfo));
											//}
										}
										if(df.getId().equals(corners.get(2).getId()))
										{
											
											for(BusLineInfo bb:busLineLow){
												if(bb.getPosition().equals(OrientationType.rightDown)){
													bus=bb;
													break;
												}
											}
											df.setX(bus.getX()+((maxBreakerNumForAllLow+1)*breakerToBreakerDistance)-bli.getHeight()/2);

											df.setY(bus.getY());
											df.setOrientation(OrientationType.SOUTH);
											list.add(df.withVLineStr(df, bus));
											count--;
										}
										if(df.getId().equals(corners.get(3).getId())){
											df.setX(deviceInfo.getX());
											df.setY(deviceInfo.getY());
											df.setOrientation(OrientationType.SOUTH);
											list.add(df.withVLineStr(df, deviceInfo));
										}
									}
									
									if(busNo!=null){
										deviceInfo.setType(DeviceType.BUSLINE);
										df.setBelongBusNo(busNo);
										double x=bli.getX()+(count)*breakerToBreakerDistance;
										if(!inCorners){
										df.setX(x);
										df.setY(bli.getY());
										}
										df.setOrientation(bli.getOrientation());
									}else{
										if(!inCorners){
										df.setX(deviceInfo.getX());
										df.setY(deviceInfo.getY());
										}
										df.setOrientation(deviceInfo.getOrientation());
										df.setBelongBusNo(deviceInfo.getBelongBusNo());
									}
									
									if(!inBreakerMRID&&!inCorners){
										list.add(df.withVLineStr(df, deviceInfo));
									}
									if(inBreakerMRID){
										boolean existed=false;
										for(DeviceInfo s:breakerCircle){
											if(s.getId().equals(df.getId())){
												existed=true;
											}
										}
										if(!existed){
											if(df.getId().equals(breakerMRID.get(1))){
												for(BusLineInfo bb:busLineLow){
													if(bb.getPosition().equals(OrientationType.rightUp)){
														bli=bb;
													}
												}
												df.setX(bli.getX()+(maxBreakerNumForAllLow+1)*breakerToBreakerDistance-bli.getHeight()/2);
												df.setY(bli.getY());
												df.setOrientation(OrientationType.NORTH);
												list.add(df.withVLineStr(df, bli));
												breakerCircle.add(df);
												bli=null;
												count--;
											}
											
											else if(df.getId().equals(breakerMRID.get(busLineLow.size()))){
												breakerCircle.add(df);
												df.setOrientation(OrientationType.SOUTH);
											}
											else if(df.getId().equals(breakerMRID.get(busLineLow.size()+1))){
												for(BusLineInfo bb:busLineLow){
													if(bb.getPosition().equals(OrientationType.leftDown)){
														bli=bb;
													}
												}
												df.setX(bli.getX());
												df.setY(bli.getY());
												df.setOrientation(OrientationType.SOUTH);
												breakerCircle.add(df);
												list.add(df.withVLineStr(df, bli));
												bli=null;
												count--;
											}else{
												breakerCircle.add(df);
											}
										}
									}
									if(breakerMapList.size()==3 && count==1){
										df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
										df.setAssistBottomLine(true);
									}else if(breakerMapList.size()==3){
										df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
									}
									String connIdNew=GetAnotherNodeStr(breakerMap,connId);
									boolean inBreakerAndBusLineMRID=false;
									if("".equals(connIdNew)||inBreakerAndBusLineMRID){//如果下一个连接点为空，当前循环终止
									}else{
										fouthFindDeviceByConnId(connIdNew,df);
									}
								
								}
							}
							if(busNo!=null){
							}
						}
					}else if(busConnType==BusConnType.FIXED){
						if((deviceInfo.getType().equals(DeviceType.TRANSFORMER))){
							String southConn=tranIdToConnIdLow.get(deviceInfo.getId());
							if(breakerMapList.size()==1){
								Map breakerMap=breakerMapList.get(0);
								if(breakerMap.get("id").equals(deviceInfo.getId())){
								}else{
									System.out.println("11  "+deviceInfo.getId()+"11来自tran一个设备 deviceInfo.getName()="+deviceInfo.getName());
									String connIdNew=GetAnotherNodeStr(breakerMap,connId);
									DeviceInfo df=null;
									String deviceType=breakerMap.get("type").toString();
									if(deviceType.equals(DeviceType.DISCONNECTOR)){
										df=new DisconnectorInfo();
									}else{
										df=new BreakerInfo();
									}
									df.setId(breakerMap.get("id").toString());
									df.setType(deviceType);
									df.setName(breakerMap.get("name").toString());
									df.setParentDeviceInfo(deviceInfo);
									df.setBelongTranNo(deviceInfo.getId());
									df.setBelongTranConnId(connId);
									df.setX(deviceInfo.getX());
									df.setY(deviceInfo.getY());
									if(southConn!=null && southConn.equals(connId)){
										df.setOrientation(OrientationType.SOUTH);
									}else{
										df.setOrientation(OrientationType.NORTH);
									}
									list.add(df.withVLineStr(df, deviceInfo));
									System.out.println("11来自tran df.getName()="+df.getName());
									//df.setBelongBreakerNo(df.getId());
									//System.out.println("11 deviceInfo.getBelongBreakerNo()="+deviceInfo.getBelongBreakerNo());
									if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
										//return;
									}else{
										fouthFindDeviceByConnId(connIdNew,df);
									}
								}
								//从tran出来后直接分支
							}else if(breakerMapList.size()==2){
								
								doubleTranForFixedLow.add(deviceInfo.getId());
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setBelongTranNo(deviceInfo.getId());
										System.out.println("33  deviceInfo.getId()="+deviceInfo.getId()+"33来自tran两个设备  deviceInfo.getName()="+deviceInfo.getName());
										System.out.println("33 df.getName()="+df.getName());
										df.setBelongTranConnId(connId);
										df.setBelongBreakerNo(df.getId());
										if(df.getId().equals(sortedStartDoubleBreakers[1])){
											df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
											df.setAssistBottomLine(true);
										}else if(df.getId().equals(sortedStartDoubleBreakers[0])){
											df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
										}else{
											df.setX(deviceInfo.getX());
										}
										df.setY(deviceInfo.getY());
										if(southConn.equals(connId)){
											df.setOrientation(OrientationType.SOUTH);
										}else{
											df.setOrientation(OrientationType.NORTH);
										}
										list.add(df.withVLineStr(df, deviceInfo));
										System.out.println("33 df.getBelongBreakerNo()="+df.getBelongBreakerNo());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}else{
											fouthFindDeviceByConnId(connIdNew,df);
										}
									}
								}
							}
						}else if(breakerMapList.size()>=1 && !deviceInfo.getType().equals(DeviceType.TRANSFORMER)){
							//经过几个breaker后出现分支
							if(breakerMapList.size()==3 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
								System.out.println("3 deviceInfo.getName()3="+deviceInfo.getName());
								doubleTranForFixedLow.add(deviceInfo.getBelongTranNo());
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setBelongTranNo(deviceInfo.getBelongTranNo());
										df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
										df.setBelongBreakerNo(df.getId());
										if(df.getId().equals(sortedStartDoubleBreakers[1])){
											df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
											df.setAssistBottomLine(true);
										}else if(df.getId().equals(sortedStartDoubleBreakers[0])){
											df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
										}else{
											df.setX(deviceInfo.getX());
										}
										df.setY(deviceInfo.getY());
										df.setOrientation(deviceInfo.getOrientation());
										list.add(df.withVLineStr(df, deviceInfo));
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}else{
											fouthFindDeviceByConnId(connIdNew,df);
										}
									}
								}
								//第一次碰到母线
							}else if(busbarSectionList.size()>0 && deviceInfo.getBelongTranNo()!=null){
								//只有双母才会走这段代码
								/*if(doubleTranForFixedLow.size()==1){
									if(doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){*/
										int count=0;
										for(int i=0;i<breakerMapList.size();i++){
											count++;
											Map breakerMap=breakerMapList.get(i);
											boolean repeatEver=false;
												for(String breakerId:doubleBreakers){
													if(breakerMap.get("id").equals(breakerId)){
														repeatEver=true;
													}
												}
											if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
												count--;
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setBelongBusNo(busbarSectionList.get(0).getMRID());
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												//df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setBelongBreakerNo(df.getBelongBreakerNo());
												
												boolean continueForth=true;
												if(deviceInfo.getOrientation().equals(OrientationType.SOUTH)){
													
													//双母左段
													if(!doubleBusIsConnected && busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(1))){
														df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance+deviceInfo.getWidth()/2+count*breakerToBreakerDistance);
														if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1).equals(df.getId())){
															continueForth=false;
															//count--;
														}
													//双母右段
													}else if((busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(2))&&!doubleBusIsConnected)||(doubleBusIsConnected && busbarSectionList.size()==2 && busbarSectionList.get(1).getMRID().equals(sortedBusIdLow.get(2)))){
														if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1).equals(df.getId())){
															continueForth=false;
															//count--;
														}
														df.setX(deviceInfo.getX()+deviceInfo.getWidth()/2+count*breakerToBreakerDistance);
													
													}else{
													
														df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance/2+count*breakerToBreakerDistance);
														if(busIdToJoinBreakersForFixedLow.get(busbarSectionList.get(0).getMRID()).get(0).equals(df.getId())){
															//起始单母
															if(busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(0))){
																df.setX(deviceInfo.getX()+maxBreakerNumForAllLow*breakerToBreakerDistance/2);
															//结束单母
															}else{
																df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance/2);
															}
															//count--;
																												
														}
													}
													df.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
												}else if(deviceInfo.getOrientation().equals(OrientationType.NORTH)){
													String startJoinId=null;
													if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID())!=null){
														startJoinId=busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1);
													}
													df.setY(deviceInfo.getY()-deviceInfo.getHeight()/2);
													df.setX(deviceInfo.getX()-maxBreakerNumForAllHigh*breakerToBreakerDistance/2+count*breakerToBreakerDistance);
													if(startJoinId!=null && breakerMap.get("id").equals(startJoinId)){
														df.setX(deviceInfo.getX()+maxBreakerNumForAllLow*breakerToBreakerDistance/2);
														count--;
													}
												}
												df.setOrientation(deviceInfo.getOrientation());
												list.add(df.withVLineStr(df, deviceInfo));

												if("".equals(connIdNew)||!continueForth){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													fouthFindDeviceByConnId(connIdNew,df);
												}
											}
										/*}
									}*/
								}
								if(busbarSectionList.size()==2 && !doubleBusIsConnected){
									doubleBusIsConnected=true;
								}		
								//变压器之后或母线之后碰到母线之前有多个设备
							}else if(breakerMapList.size()==2 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
								System.out.println("22一个设备 deviceInfo.getName()="+deviceInfo.getName());
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									boolean repeatEver=false;
									if(deviceInfo.getBelongTranConnId()==null){
										for(String breakerId:doubleBreakers){
											if(breakerMap.get("id").equals(breakerId)){
												repeatEver=true;
											}
										}
									}
									if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
									}else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setBelongTranNo(deviceInfo.getBelongTranNo());
										if(deviceInfo.getBelongTranConnId()!=null)
										System.out.println("22  deviceInfo.getBelongTranConnId()="+deviceInfo.getBelongTranConnId());	
										df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
										System.out.println("22 df.getName()="+df.getName());
										if(deviceInfo.getBelongBreakerNo()!=null){
											df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
										}
										if(deviceInfo.getBelongBusNo()!=null){
											df.setBelongBusNo(deviceInfo.getBelongBusNo());
										}
										df.setX(deviceInfo.getX());
										df.setY(deviceInfo.getY());
										df.setOrientation(deviceInfo.getOrientation());
										
										list.add(df.withVLineStr(df, deviceInfo));
										//df.setBelongBreakerNo(df.getBelongBreakerNo());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}else{
											fouthFindDeviceByConnId(connIdNew,df);
										}
									}
								}
							}
						}
					}
				}
				busNo=null;
				}
				
				
				
				//FixedWithoutTran等第1次递归
				public void firstFindDeviceByConnIdForFixedWithoutTran(String connId,DeviceInfo deviceInfo){
					List<BusbarSection> busbarSectionList = getBusByConnId(connId);
					
					List<Breaker> breakerList = getBreakerByConnId(connId);
					
					List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);
					
					List<Map<String, String>> breakerMapList = joinBreakerData(breakerList, disconnectorList);
					
					List<ACLineDot> lineList = getLineByConnId(connId);
					
					List<PowerTransformer> powerTransformerList = getTranByConnId(connId);
					
					if(powerTransformerList.size()!=0 && powerTransformerList != null && !DeviceType.TRANSFORMER.equals(deviceInfo.getType())){
						if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
							System.out.println("powerTransformerList.size()!="+powerTransformerList.size()+"pppppppppppppppppppppppppppppppppppppppppppppppppppp");
							return;
						}else{ 
							PowerTransformer ptf=powerTransformerList.get(0);
							TransformerInfo ti=new TransformerInfo();
							toTranBreakerNo.removeAll(toTranBreakerNo);
							toTranBreakerNo.add(deviceInfo.getBelongBreakerNo());
							ti.setBelongBusNo(deviceInfo.getBelongBusNo());
							ti.setId(ptf.getMRID());
							ti.setName(ptf.getName());
							ti.setOrientation(deviceInfo.getOrientation());
							ti.setType(DeviceType.TRANSFORMER);
							String connIdNew=GetAnotherNodeStrForTran(ptf,connId);
							firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,ti);
						}
					}if(busbarSectionList!=null && busbarSectionList.size()!=0){
							BusbarSection bs=busbarSectionList.get(0);
							if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
								if(bs.getMRID().equals(deviceInfo.getId())){
								}else{
									BusLineInfo bli=new BusLineInfo();
									bli.setId(bs.getMRID());
									bli.setType(DeviceType.BUSLINE);
									bli.setName(bs.getName());
									bli.setParentDeviceInfo(deviceInfo);
									connToVol.put(deviceInfo.getBelongTranConnId(),bs.getVoltageLevel());
									Map<String,String> hml=tranIdToConnForSort.get(deviceInfo.getBelongTranNo());
									//判断高中低压母线
									if(bs.getVoltageLevel().equals(volLevels[0])){
										if(hml==null){
											hml=new HashMap();
											hml.put("h",deviceInfo.getBelongTranConnId());
										}else{
											String conn=hml.get("h");
											if(conn==null){
												hml.put("h",deviceInfo.getBelongTranConnId());
											}
										}
										tranIdToConnForSort.put(deviceInfo.getBelongTranNo(),hml);
										//再次遇到母线
										if(deviceInfo.getBelongTranConnId()==null){

											System.out.println("再次遇到母线=================================bs.getName()="+bs.getName());
											
											if(busIdForHigh.size()>=2){
												boolean existed=false;
												for(String s:busIdForHigh){
													if(s.equals(bli.getId())){
														existed=true;
													}
												}
												if(!existed){
													if(busIdToJoinBreakersForSingleLow.get(busIdForHigh.get(1))==null){
														List<String> joinBreakers=new ArrayList<String>();
														int index=0;
														int limitCount=0;
														findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,index,limitCount);
													}
													busIdForHigh.add(bli.getId());
												}
											}else if(busIdForHigh.size()==1){

												boolean existed=false;
												for(String s:tempBusIdForHigh){
													if(s.equals(bli.getId())){
														existed=true;
													}
												}
												if(!existed){
													tempBusIdForHigh.add(bli.getId());
													if(busIdToJoinBreakersForSingleLow.get(busIdForHigh.get(0))!=null){
														busIdToJoinBreakersForSingleLow.remove(busIdToJoinBreakersForSingleLow.get(busIdForHigh.get(0)));
													}
													
													//if(busIdToJoinBreakersForSingleLow.get(busIdForHigh.get(0))==null){
														System.out.println(bli.getName()+"=======busIdForHigh.get(0)="+busIdForHigh.get(0)+"======deviceInfo.getBelongBusNo()="+deviceInfo.getBelongBusNo()+"=================ddddddddddddddddddddddddddddddddddddddddddddddddddddddddeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeecccccccccccccccccccccccccccccccccccccccccccccccccccccccccc=====================================");
														List<String> joinBreakers=new ArrayList<String>();
														int index=0;
														int limitCount=0;
														findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,index,limitCount);
													//}
												}
												
											}
											
											/*if(busIdToJoinBreakersForSingleLow.get(bli.getId())==null){
												List<String> joinBreakers=new ArrayList<String>();
												int index=0;
												int limitCount=0;
												findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,index,limitCount);
											}*/
											
											if(busIdForHigh.size()!=3){
												return;
											}
										}
										//第一次遇到母线
										else{
											busIdTostartBreakerIdForLow.put(bli.getId(), deviceInfo.getBelongBreakerNo());
											boolean existed=false;
											for(String s:busIdForHigh){
												if(s.equals(bli.getId())){
													existed=true;
												}
											}
											if(!existed){
												busIdForHigh.add(bli.getId());
											}
											System.out.println("第一次遇到母线9999999999999999999999999999999999999999999999999"+bli.getId());
											
										}
									
									}else if(bs.getVoltageLevel().equals(volLevels[1])){
										if(hml==null){
											hml=new HashMap();
											hml.put("m",deviceInfo.getBelongTranConnId());
										}else{
											String conn=hml.get("m");
											if(conn==null){
												hml.put("m",deviceInfo.getBelongTranConnId());
											}
										}
										tranIdToConnForSort.put(deviceInfo.getBelongTranNo(),hml);
										return;
									}else if(bs.getVoltageLevel().equals(volLevels[2])){
										//再次遇到母线
										if(deviceInfo.getBelongTranConnId()==null){
											List<String> joinBreakers=new ArrayList<String>();
											int index=0;
											int limitCount=0;
											findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,index,limitCount);
										}
										//第一次遇到母线
										else{
											busIdTostartBreakerIdForLow.put(bli.getId(), deviceInfo.getBelongBreakerNo());
										}
										if(hml==null){
											hml=new HashMap();
											hml.put("l",deviceInfo.getBelongTranConnId());
										}else{
											String conn=hml.get("l");
											if(conn==null){
												hml.put("l",deviceInfo.getBelongTranConnId());
											}
										}
										tranIdToConnForSort.put(deviceInfo.getBelongTranNo(),hml);
										
										if(transCount==1){
											List<String> busListLowForSort=breakerToBusForSort.get(deviceInfo.getBelongBreakerNo());
											if(busListLowForSort==null){
												busListLowForSort=new ArrayList<String>();
											}
											busListLowForSort.add(bli.getId());
											if(deviceInfo.getBelongBreakerNo()!=null)
											breakerToBusForSort.put(deviceInfo.getBelongBreakerNo(),busListLowForSort);
										}
										
									}else{
										return;
									}
									if(transCount!=1){
										return;
									}
									
								}
							}else{
								if(doubleBusIsConnected && busbarSectionList.size()==2){
									bs=busbarSectionList.get(1);
								}
								
								if(bs.getMRID().equals(deviceInfo.getId())){
								}else{
									BusLineInfo bli=new BusLineInfo();
									bli.setId(bs.getMRID());
									bli.setType(DeviceType.BUSLINE);
									bli.setName(bs.getName());
									bli.setParentDeviceInfo(deviceInfo);
									if(busConnType==BusConnType.FIXEDWITHOUTTRAN){
										    String orien=deviceInfo.getOrientation();
										   //判断高、低压母线
										   // if(orien==null){
										    	for(BusbarSection buse:startBusList){
										    		if(buse.getMRID().equals(bli.getId())){
										    			orien=OrientationType.NORTH;
										    			//break;
										    		}
										    	}
										    	for(BusbarSection buse:endBusList){
										    		if(buse.getMRID().equals(bli.getId())){
										    			orien=OrientationType.SOUTH;
										    			//break;
										    		}
										    	}
										    //}
										    if(orien.equals(OrientationType.SOUTH)){
	
										    	//如果是双母接线
										    	//if(doubleTranForFixedLow.size()>=1 && doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){
													//第一次碰到母线
													if(deviceInfo.getBelongTranConnId()!=null){
														System.out.println("fffffffffffffff第1次进入双母");
	
														busIdTostartBreakerIdForLow.put(bli.getId(), deviceInfo.getBelongBreakerNo());
														busIdToLastBreakerInfoForLow.put(bli.getId().toString(),deviceInfo);
														busIdToConnIdLow.put(bli.getId(),deviceInfo.getBelongTranConnId());
													//第二次
													}else{
														System.out.println("fffffffffffffff第二次进入双母");
														List<String> joinBreakers=new ArrayList<String>();
														boolean hasExisted=false;
														//双母ID唯一
														Iterator joinIt=singleIdToDoubleIdForLow.keySet().iterator();
														
														while(joinIt.hasNext()){
															String key=joinIt.next().toString();
															String value=singleIdToDoubleIdForLow.get(key);
															if(value.equals(deviceInfo.getBelongBusNo())||key.equals(bli.getId())){
																hasExisted=true;
																break;
															}
														}
														if(hasExisted){
															return;
														}else{
															System.out.println(doubleBusIsConnected+""+endBusList.size()+bli.getId()+"---------------------找到单母------------------------JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ"+bli.getId()+bli.getName());
															singleIdToDoubleIdForLow.put(bli.getId(),deviceInfo.getBelongBusNo());
															int index=0;
															if(doubleBusIsConnected){
																index=1;
															}
															int limitCount=0;
															findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers,index,limitCount);
														}
														
								 						//return;
													}
										    }else if(orien.equals(OrientationType.NORTH)){
	
										    	if(deviceInfo.getBelongTranConnId()!=null){
										    		/*busIdToConnIdLow.put(bli.getId(),deviceInfo.getBelongTranConnId());
											    	busIdToLastBreakerInfoForLow.put(bli.getId().toString(),deviceInfo);*/
										    		//return;
	
										    	}else{
													
										    		List<String> joinBreakers=new ArrayList<String>();
													//findJoinBreakers(bli,connId,deviceInfo.getId(),joinBreakers);
										    		//return;
										    	}
										    }
									}
								}
							}
					}if (breakerMapList!=null  && breakerMapList.size()!=0){
						System.out.println("--------------------------breakerMapList.size()---------------------------="+breakerMapList.size());
						//从TRANSFORMER发出
						if((deviceInfo.getType().equals(DeviceType.TRANSFORMER))){
						if(busConnType==BusConnType.FIXEDWITHOUTTRAN){
							if(breakerMapList.size()==1){
								Map breakerMap=breakerMapList.get(0);
								if(breakerMap.get("id").equals(deviceInfo.getId())){
								}else{
									System.out.println("11  "+deviceInfo.getId()+"11来自tran一个设备 deviceInfo.getName()="+deviceInfo.getName());
									String connIdNew=GetAnotherNodeStr(breakerMap,connId);
									DeviceInfo df=null;
									String deviceType=breakerMap.get("type").toString();
									if(deviceType.equals(DeviceType.DISCONNECTOR)){
										df=new DisconnectorInfo();
									}else{
										df=new BreakerInfo();
									}
									df.setId(breakerMap.get("id").toString());
									df.setType(deviceType);
									df.setName(breakerMap.get("name").toString());
									df.setParentDeviceInfo(deviceInfo);
									df.setBelongTranNo(deviceInfo.getId());
									df.setBelongTranConnId(connId);
									df.setOrientation(deviceInfo.getOrientation());
									System.out.println("11来自tran df.getName()="+df.getName());
									//df.setBelongBreakerNo(df.getId());
									df.setBelongBreakerNo(df.getBelongBreakerNo());
									//System.out.println("11 deviceInfo.getBelongBreakerNo()="+deviceInfo.getBelongBreakerNo());
									if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
										//return;
									}else{
										firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
									}
								}
								//从tran出来后直接分支
							}else if(breakerMapList.size()==2){
								
								doubleTranForFixedLow.add(deviceInfo.getId());
								//为防止回溯的doubleBreakers填充数据
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										doubleBreakers.add(breakerMap.get("id").toString());
									}
								}
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setBelongTranNo(deviceInfo.getId());
										System.out.println("33  deviceInfo.getId()="+deviceInfo.getId()+"33来自tran两个设备  deviceInfo.getName()="+deviceInfo.getName());
										System.out.println("33 df.getName()="+df.getName());
										df.setBelongTranConnId(connId);
										df.setBelongBreakerNo(df.getId());
										//df.setBelongBreakerNo(df.getBelongBreakerNo());
										System.out.println("33 df.getBelongBreakerNo()="+df.getBelongBreakerNo());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}else{
											firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
										}
									}
								}
							}
						}else if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
							if(breakerMapList.size()==1){
								Map breakerMap=breakerMapList.get(0);
								if(breakerMap.get("id").equals(deviceInfo.getId())){
								}else{
									System.out.println("11  "+deviceInfo.getId()+"11来自tran一个设备 deviceInfo.getName()="+deviceInfo.getName());
									String connIdNew=GetAnotherNodeStr(breakerMap,connId);
									DeviceInfo df=null;
									String deviceType=breakerMap.get("type").toString();
									if(deviceType.equals(DeviceType.DISCONNECTOR)){
										df=new DisconnectorInfo();
									}else{
										df=new BreakerInfo();
									}
									df.setId(breakerMap.get("id").toString());
									df.setType(deviceType);
									df.setName(breakerMap.get("name").toString());
									df.setParentDeviceInfo(deviceInfo);
									df.setBelongTranNo(deviceInfo.getId());
									df.setBelongTranConnId(connId);
									df.setOrientation(deviceInfo.getOrientation());
									System.out.println("11来自tran df.getName()="+df.getName());
									//df.setBelongBreakerNo(df.getId());
									//df.setBelongBreakerNo(df.getBelongBreakerNo());
									if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
										//return;
									}else{
										firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
									}
								}
							}else if(breakerMapList.size()==2){
								
								doubleTranForFixedLow.add(deviceInfo.getId());
								//为防止回溯的doubleBreakers填充数据
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										doubleBreakers.add(breakerMap.get("id").toString());
									}
								}
								for(int i=0;i<breakerMapList.size();i++){
									Map breakerMap=breakerMapList.get(i);
									if(breakerMap.get("id").equals(deviceInfo.getId())){
									}else{
										String connIdNew=GetAnotherNodeStr(breakerMap,connId);
										DeviceInfo df=null;
										String deviceType=breakerMap.get("type").toString();
										if(deviceType.equals(DeviceType.DISCONNECTOR)){
											df=new DisconnectorInfo();
										}else{
											df=new BreakerInfo();
										}
										df.setId(breakerMap.get("id").toString());
										df.setType(deviceType);
										df.setName(breakerMap.get("name").toString());
										df.setParentDeviceInfo(deviceInfo);
										df.setBelongTranNo(deviceInfo.getId());
										System.out.println("33  deviceInfo.getId()="+deviceInfo.getId()+"33来自tran两个设备  deviceInfo.getName()="+deviceInfo.getName());
										System.out.println("33 df.getName()="+df.getName());
										df.setBelongTranConnId(connId);
										df.setBelongBreakerNo(df.getId());
										//df.setBelongBreakerNo(df.getBelongBreakerNo());
										System.out.println("33 df.getBelongBreakerNo()="+df.getBelongBreakerNo());
										if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
											//return;
										}else{
											firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
										}
									}
								}
							}
						}
							//从起始breaker发出
						}else if(breakerMapList.size()>=1 && !deviceInfo.getType().equals(DeviceType.TRANSFORMER)){
							if(busConnType==BusConnType.FIXEDWITHOUTTRAN){
									//经过几个breaker后出现分支
									if(breakerMapList.size()==3 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
										System.out.println("3 deviceInfo.getName()3="+deviceInfo.getName());
										doubleTranForFixedLow.add(deviceInfo.getBelongTranNo());
										//为防止回溯的doubleBreakers填充数据
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												doubleBreakers.add(breakerMap.get("id").toString());
											}
										}
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setOrientation(deviceInfo.getOrientation());
												df.setBelongBreakerNo(df.getId());
												//df.setBelongBreakerNo(df.getBelongBreakerNo());
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}else if(busbarSectionList.size()>0 && deviceInfo.getBelongTranNo()!=null){
										//碰到母线了
										/*if(doubleTranForFixedLow.size()==1){
											if(doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){*/
												for(int i=0;i<breakerMapList.size();i++){
													Map breakerMap=breakerMapList.get(i);
													boolean repeatEver=false;
													for(String breakerId:doubleBreakers){
														if(breakerMap.get("id").equals(breakerId)){
															repeatEver=true;
														}
													}
													if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
													}else{
														String connIdNew=GetAnotherNodeStr(breakerMap,connId);
														DeviceInfo df=null;
														String deviceType=breakerMap.get("type").toString();
														if(deviceType.equals(DeviceType.DISCONNECTOR)){
															df=new DisconnectorInfo();
														}else{
															df=new BreakerInfo();
														}
														if(doubleBusIsConnected && busbarSectionList.size()==2){
															df.setBelongBusNo(busbarSectionList.get(0).getMRID());
														}else if(!doubleBusIsConnected && busbarSectionList.size()==2){
															df.setBelongBusNo(busbarSectionList.get(1).getMRID());
														}
														df.setId(breakerMap.get("id").toString());
														df.setType(deviceType);
														df.setName(breakerMap.get("name").toString());
														df.setParentDeviceInfo(deviceInfo);
														df.setBelongTranNo(deviceInfo.getBelongTranNo());
														df.setOrientation(deviceInfo.getOrientation());
														//df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
														//df.setBelongBreakerNo(df.getBelongBreakerNo());
														df.setBelongBreakerNo(df.getId());
														if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
															//return;
														}else{
															firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
														}
													}
												/*}
											}*/
										}
										if(busbarSectionList.size()==2 && doubleBusIsConnected==false){
											doubleBusIsConnected=true;
										}
										//变压器之后或母线之后碰到母线之前有多个设备
									}else if(breakerMapList.size()==2 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
										System.out.println("22一个设备 deviceInfo.getName()="+deviceInfo.getName());
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											boolean repeatEver=false;
											if(deviceInfo.getBelongTranConnId()==null){
												for(String breakerId:doubleBreakers){
													if(breakerMap.get("id").equals(breakerId)){
														repeatEver=true;
													}
												}
											}
											if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
											}else{
												
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setOrientation(deviceInfo.getOrientation());
												if(deviceInfo.getBelongTranConnId()!=null){
													//为防止回溯的doubleBreakers填充数据
													doubleBreakers.add(breakerMap.get("id").toString());
												}
												if(deviceInfo.getBelongTranConnId()!=null)
												System.out.println("22  deviceInfo.getBelongTranConnId()="+deviceInfo.getBelongTranConnId());	
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												System.out.println("22 df.getName()="+df.getName());
												if(deviceInfo.getBelongBreakerNo()!=null){
													df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
												}
												if(deviceInfo.getBelongBusNo()!=null){
													df.setBelongBusNo(deviceInfo.getBelongBusNo());
												}
												//df.setBelongBreakerNo(df.getId());
												df.setBelongBreakerNo(df.getBelongBreakerNo());
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}
								}else if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){

									//变压器之后或母线之后碰到母线之前有多个设备
									if(breakerMapList.size()<=2 && busbarSectionList.size()==0){
										System.out.println("22静安一个设备 deviceInfo.getName()="+deviceInfo.getName());
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setOrientation(deviceInfo.getOrientation());
												
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
												df.setBelongBusNo(deviceInfo.getBelongBusNo());
												//df.setBelongBreakerNo(df.getId());
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}else if(busbarSectionList.size()>0){
										//碰到母线了
										/*if(doubleTranForFixedLow.size()==1){
											if(doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){*/
												for(int i=0;i<breakerMapList.size();i++){
													Map breakerMap=breakerMapList.get(i);
													boolean repeatEver=false;
													for(String breakerId:doubleBreakers){
														if(breakerMap.get("id").equals(breakerId)){
															repeatEver=true;
														}
													}
													if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
													}else{
														String connIdNew=GetAnotherNodeStr(breakerMap,connId);
														DeviceInfo df=null;
														String deviceType=breakerMap.get("type").toString();
														if(deviceType.equals(DeviceType.DISCONNECTOR)){
															df=new DisconnectorInfo();
														}else{
															df=new BreakerInfo();
														}
														
														df.setBelongBusNo(busbarSectionList.get(0).getMRID());
														df.setId(breakerMap.get("id").toString());
														df.setType(deviceType);
														df.setName(breakerMap.get("name").toString());
														df.setParentDeviceInfo(deviceInfo);
														df.setBelongTranNo(deviceInfo.getBelongTranNo());
														df.setOrientation(deviceInfo.getOrientation());
														//df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
														df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
														//df.setBelongBreakerNo(df.getId());
														if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
															//return;
														}else{
															firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
														}
													}
												/*}
											}*/
										}
										if(busbarSectionList.size()==2 && doubleBusIsConnected==false){
											doubleBusIsConnected=true;
										}
										
									}//经过几个breaker后出现分支
									else if(breakerMapList.size()>=3  && busbarSectionList.size()==0){
										System.out.println("3 ffffffffffffffffffffffffffzzzzzzzzzzzzzzzzzzzzzzzz deviceInfo.getName()3="+deviceInfo.getName());
										//doubleTranForFixedLow.add(deviceInfo.getBelongTranNo());
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setBelongBusNo(deviceInfo.getBelongBusNo());
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setOrientation(deviceInfo.getOrientation());
												df.setBelongBreakerNo(df.getId());
												//df.setBelongBreakerNo(df.getBelongBreakerNo());
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													firstFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}
								}
							}
						
							if(busbarSectionList.size()>0){
								String mrid=busbarSectionList.get(0).getMRID();
								if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
										if(volLevels[2].equals(busbarSectionList.get(0).getVoltageLevel())){
											if(breakerMapList.size()>maxBreakerNumForAllLow)
											maxBreakerNumForAllLow=breakerMapList.size();
										}
										else if(volLevels[0].equals(busbarSectionList.get(0).getVoltageLevel())){
											if(breakerMapList.size()>maxBreakerNumForAllHigh)
											maxBreakerNumForAllRealHigh=breakerMapList.size();
										}
								}else{
									
									for(BusbarSection s:startBusList){
										if(s.getMRID().equals(mrid)){
											if(breakerMapList.size()>maxBreakerNumForAllHigh)
											maxBreakerNumForAllRealHigh=breakerMapList.size();
											break;
										}
									}
									
									for(BusbarSection s:endBusList){
										if(s.getMRID().equals(mrid)){
											if(breakerMapList.size()>maxBreakerNumForAllLow)
											maxBreakerNumForAllLow=breakerMapList.size();
											break;
										}
									}
								}
								
								
							}
						}
										
					}
				
				//FixedWithoutTran等第二次递归
						public void secondFindDeviceByConnIdForFixedWithoutTran(String connId,DeviceInfo deviceInfo){
							String type= deviceInfo.getType();
							String busNo=null;
							Map<String,Object> result = new HashMap<String, Object>();
							List<BusbarSection> busbarSectionList = getBusByConnId(connId);
							
							List<Breaker> breakerList1 = getBreakerByConnId(connId);
							
							List<Disconnector> disconnectorList1 = getDisconnectorByConnId(connId);
							
							List<Map<String, String>> breakerMapList = joinBreakerData(breakerList1, disconnectorList1);
							
							List<ACLineDot> lineList = getLineByConnId(connId);
							
							List<PowerTransformer> powerTransformerList = getTranByConnId(connId);
							if(lineList != null && lineList.size()!=0 ){
								ACLineDot ad=lineList.get(0);
								LineInfo li=new LineInfo();
								li.setId(ad.getMRID());
								li.setName(ad.getName());
								li.setOrientation(deviceInfo.getOrientation());
								li.setX(deviceInfo.getX());
								li.setY(deviceInfo.getY());
								li.setSvgStr(li.svgStr());
								list.add(li);
							}
							if(powerTransformerList != null && powerTransformerList.size()!=0 && !DeviceType.TRANSFORMER.equals(deviceInfo.getType())){
								if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
									System.out.println("powerTransformerList.size()!="+powerTransformerList.size()+"pppppppppppppppppppppppppppppppppppppppppppppppppppp");
									return;
								}else{
									PowerTransformer ptf=powerTransformerList.get(0);
									TransformerInfo ti=new TransformerInfo();
									ti.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
									ti.setBelongBusNo(deviceInfo.getBelongBusNo());
									ti.setId(ptf.getMRID());
									ti.setName(ptf.getName());
									ti.setType(DeviceType.TRANSFORMER);
									double pDeviceX=deviceInfo.getX();
									double pDeviceY=deviceInfo.getY();
									ti.setX(pDeviceX);
									ti.setY(pDeviceY);
									ti.setSvgStr(ti.svgStr());
									//ti.withVLineStr(ti,deviceInfo);
									//ti.withALineStr(deviceInfo);
									ti.setOrientation(deviceInfo.getOrientation());
									ti.setType(DeviceType.TRANSFORMER);
									String connIdNew=GetAnotherNodeStrForTran(ptf,connId);
									list.add(ti.withVLineStr(ti,deviceInfo));
									System.out.println(breakerMapList.size()+"connId="+connId+"connIdNew="+connIdNew+"powerTransformerList1111111111111111111111111111111111111111111111111111111111111111powerTransformerList");

									secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,ti);
									return;
								}
							}if(busbarSectionList!=null && busbarSectionList.size()!=0){
								if(!deviceInfo.getType().equals(DeviceType.BUSLINE)){
									BusbarSection bs=busbarSectionList.get(0);
									
									if(doubleBusIsConnected && busbarSectionList.size()==2){
										bs=busbarSectionList.get(1);
									}
									
									BusLineInfo bli=new BusLineInfo();
									bli.setId(bs.getMRID());
									bli.setName(bs.getName());
									bli.setType(DeviceType.BUSLINE);
									bli.setParentDeviceInfo(deviceInfo);
								    if(busConnType==BusConnType.FIXEDWITHOUTTRAN){
										String orien=deviceInfo.getOrientation();
										if(orien.equals(OrientationType.SOUTH)){
											//如果是双母接线
											if(doubleTranForFixedLow.size()>=1 && doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){
												//第一次碰到母线
												if(deviceInfo.getBelongTranConnId()!=null){
													System.out.println("4fffffffffffffff第一次进入双母");
													bli.setWidth(maxBreakerNumForAllLow*breakerToBreakerDistance);
													if(bli.getId().equals(sortedBusIdLow.get(1))){
														bli.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance+deviceInfo.getWidth()/2);
													}else if(bli.getId().equals(sortedBusIdLow.get(2))){
														bli.setDoubleConnDistance(true);
														bli.setX(deviceInfo.getX()+deviceInfo.getWidth()/2);
													}
													bli.setY(deviceInfo.getY()+deviceInfo.getHeight());
													bli.setOrientation(OrientationType.SOUTH);
													bli.setColor("yellow");
													busLineCountLow++;
													bli.setNotFirst(true);
													
													if(busNoToBreakerInfoForLow.get(bli.getId())!=null){
													    //bli.setAssisHeight(busNoToBreakerInfoForLow.get(bli.getId()).getY());
														//bli.setAssisWidth(busNoToBreakerInfoForLow.get(bli.getId()).getX());
														bli.setSvgStr(bli.svgStrLow());
														list.add(bli.withVLineStr(bli, deviceInfo));
													}else{
														if(busbarSectionList.size()==2){
															bli.setSvgStr(bli.svgStrLow());
															list.add(bli.withVLineStr(bli, deviceInfo));
														}else{
															doubleRightBusLineLow.put(bli,deviceInfo);
														}
													}
												//第二次
												}else{
													System.out.println("4fffffffffffffff第二次进入双母");
											    	bli.setWidth(maxBreakerNumForAllLow*breakerToBreakerDistance);
													if(bli.getId().equals(sortedBusIdLow.get(0))){
												    	bli.setX(leftTranDistance+tranAndTranDistance-2*maxBreakerNumForAllLow*breakerToBreakerDistance);
													}else if(sortedBusIdLow.size()>3 && bli.getId().equals(sortedBusIdLow.get(3))){
												    	bli.setX(leftTranDistance+tranAndTranDistance+maxBreakerNumForAllLow*breakerToBreakerDistance);
													}
													
											    	bli.setY(deviceInfo.getY()+deviceInfo.getHeight());
													bli.setOrientation(OrientationType.SOUTH);
													bli.setColor("yellow");
													busLineCountLow++;
													bli.setSvgStr(bli.svgStrLow());
													list.add(bli.withVLineStr(bli, deviceInfo));
												}
											}
											
										}else if(orien.equals(OrientationType.NORTH)){
											bli.setWidth(maxBreakerNumForAllHigh*breakerToBreakerDistance);
											bli.setX(deviceInfo.getX()-maxBreakerNumForAllHigh*breakerToBreakerDistance/2+deviceInfo.getWidth()/2);
											bli.setY(deviceInfo.getY()-deviceInfo.getHeight());
											bli.setOrientation(OrientationType.NORTH);
											bli.setColor("red");
											bli.setSvgStr(bli.svgStrLow());
											list.add(bli.withVLineStr(bli, deviceInfo));
										}
									}else if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
										
										//第二个变压器再次碰到母线
										if(transCount==2&&deviceInfo.getBelongTranConnId()==null){
											return;
										}else if(transCount==1&&!bs.getVoltageLevel().equals(volLevels[0])){
											DeviceInfo newDf=new BreakerInfo();
											newDf.setX(deviceInfo.getX());
											newDf.setY(deviceInfo.getY());
											newDf.setOrientation(deviceInfo.getOrientation());
											if(bli.getId().equals(sortedBusIdLow.get(sortedBusIdLow.size()-1))){
												newDf.setY(newDf.getY()+anotherAssisHeight);
											}
											busNoToBreakerInfoForLow.put(bs.getMRID(), newDf);
										
										}
										
										//判断高中低压母线
										if(bs.getVoltageLevel().equals(volLevels[0])){
											bli.setColor("blue");
											//再次遇到母线
											if(deviceInfo.getBelongTranConnId()==null){
												
												if(busIdForHigh2.size()>=2){
													boolean existed=false;
													for(String s:busIdForHigh2){
														if(s.equals(bli.getId())){
															existed=true;
														}
													}
													if(!existed){
														busIdForHigh2.add(bli.getId());
														//四段母线
														if(busIdForHigh.get(2).equals(bli.getId())){
															double x=busInfoForRealHigh.get(1).getX();
															double y=busInfoForRealHigh.get(1).getY();
															bli.setX(x+busAndBusDistanceForLow+breakerToBreakerDistance*maxBreakerNumForAllRealHigh);
															bli.setY(y);
															bli.setWidth(maxBreakerNumForAllRealHigh*breakerToBreakerDistance);
															//bli.setUpOrDownForLeftAssisLine("up");
															bli.setSvgStr(bli.svgStrLow());
															bli.setOrientation(OrientationType.NORTH);
															//list.add(bli);
															busInfoForRealHigh.add(bli);
															tempBusLineLow2.add(bli);
															busNoToBreakerInfoForLow.put(bs.getMRID(), deviceInfo);
														//三段母线
														}else if(busIdForHigh.get(3).equals(bli.getId())){
															double x=busInfoForRealHigh.get(0).getX();
															double y=busInfoForRealHigh.get(0).getY();
															bli.setX(x+busAndBusDistanceForLow+breakerToBreakerDistance*maxBreakerNumForAllRealHigh);
															bli.setY(y);
															bli.setWidth(maxBreakerNumForAllRealHigh*breakerToBreakerDistance);
															bli.setOrientation(OrientationType.SOUTH);
															//bli.setUpOrDownForLeftAssisLine("down");
															bli.setSvgStr(bli.svgStrLow());
															//list.add(bli);
															//busInfoForRealHigh.add(bli);
															//tempBusLineLow2.add(bli);
															//doubleRightBusLineLow.put(bli,deviceInfo);
														}
													}
												}else{
													if(busIdForHigh.get(3).equals(bli.getId())){
														double x=busInfoForRealHigh.get(0).getX();
														double y=busInfoForRealHigh.get(0).getY();
														bli.setX(x+busAndBusDistanceForLow+breakerToBreakerDistance*maxBreakerNumForAllRealHigh);
														bli.setY(y);
														bli.setWidth(maxBreakerNumForAllRealHigh*breakerToBreakerDistance);
														bli.setOrientation(OrientationType.SOUTH);
														//bli.setUpOrDownForLeftAssisLine("down");
														bli.setSvgStr(bli.svgStrLow());
														//list.add(bli);
														//busInfoForRealHigh.add(bli);
														tempBusLineLow2.add(bli);
														DeviceInfo newDf=new BreakerInfo();
														newDf.setX(deviceInfo.getX());
														newDf.setY(deviceInfo.getY()-deviceInfo.getStandardDistance());
														newDf.setOrientation(deviceInfo.getOrientation());
														if(bli.getId().equals(sortedBusIdLow.get(sortedBusIdLow.size()-1))){
															newDf.setY(newDf.getY()+anotherAssisHeight);
														}
														busNoToBreakerInfoForLow.put(bs.getMRID(), newDf);												
													}
												}
												if(busIdForHigh2.size()!=3){
													return;
												}
											}
											//第一次遇到母线
											else{
												boolean existed=false;
												for(String s:busIdForHigh2){
													if(s.equals(bli.getId())){
														existed=true;
													}
												}
												if(!existed){
													busIdForHigh2.add(bli.getId());
												}
												if(deviceInfo.getId().equals(tranToLeftBreakerForHigh.get(deviceInfo.getBelongTranNo()))){
													bli.setX(deviceInfo.getX()+deviceInfo.getWidth()*breakerToBreakerDistance/50+deviceInfo.getWidth()/2);
													bli.setY(deviceInfo.getY()-anotherAssisHeight);
													bli.setWidth(maxBreakerNumForAllRealHigh*breakerToBreakerDistance);
													bli.setUpOrDownForLeftAssisLine("up");
													bli.setSvgStr(bli.svgStrLow());
													bli.setOrientation(OrientationType.SOUTH);
													list.add(bli);
													busInfoForRealHigh.add(bli);
												}else{
													bli.setX(deviceInfo.getX()+deviceInfo.getWidth()/2);
													bli.setY(deviceInfo.getY()-anotherAssisHeight/4);
													bli.setWidth(maxBreakerNumForAllRealHigh*breakerToBreakerDistance);
													bli.setOrientation(OrientationType.NORTH);
													bli.setUpOrDownForLeftAssisLine("down");
													bli.setSvgStr(bli.svgStrLow());
													list.add(bli);
													busInfoForRealHigh.add(bli);
													
												}
												
											}
										
											
										}else if(bs.getVoltageLevel().equals(volLevels[1])){
											return;
										}else if(bs.getVoltageLevel().equals(volLevels[2])){
											//再次遇到母线
											if(deviceInfo.getBelongTranConnId()==null){
												//如果碰到最后俩母线
												if(bli.getId().equals(sortedBusIdLow.get(sortedBusIdLow.size()-1))||bli.getId().equals(sortedBusIdLow.get(sortedBusIdLow.size()-2))){
													return;
												}
												
												bli.setWidth(maxBreakerNumForAllLow*breakerToBreakerDistance);
												double lastX=tempBusLineLow1.get(tempBusLineLow1.size()-1).getX();
												double lastY=tempBusLineLow1.get(tempBusLineLow1.size()-1).getY();
												bli.setX(lastX+maxBreakerNumForAllLow*breakerToBreakerDistance+busAndBusDistanceForLow);
												bli.setY(lastY);
												bli.setOrientation(OrientationType.SOUTH);
												bli.setColor("yellow");
												bli.setSvgStr(bli.svgStrLow());
												//list.add(bli.withVLineStr(bli, deviceInfo));
												tempBusLineLow1.add(bli);
												tempBusLineLow2.add(bli);
												doubleRightBusLineLow.put(bli,deviceInfo);
												
											
											}
											//第一次遇到母线
											else{
												
												bli.setWidth(maxBreakerNumForAllLow*breakerToBreakerDistance);
												//左侧
												if(bli.getId().equals(sortedBusIdLow.get(0))||bli.getId().equals(sortedBusIdLow.get(sortedBusIdLow.size()-2))){
													bli.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance+deviceInfo.getWidth()/2);
												}
												//右侧
												else if(bli.getId().equals(sortedBusIdLow.get(1))||bli.getId().equals(sortedBusIdLow.get(sortedBusIdLow.size()-1))){
													//bli.setDoubleConnDistance(true);
													bli.setX(deviceInfo.getX()+deviceInfo.getWidth()/2);
												}
												bli.setY(deviceInfo.getY()+deviceInfo.getHeight());
												bli.setOrientation(OrientationType.SOUTH);
												bli.setColor("yellow");
												bli.setSvgStr(bli.svgStrLow());
												if(bli.getId().equals(sortedBusIdLow.get(0))||bli.getId().equals(sortedBusIdLow.get(1))){
													list.add(bli.withVLineStr(bli, deviceInfo));
												}
												
												if(bli.getId().equals(sortedBusIdLow.get(1))){
													tempBusLineLow1.add(bli);
												}
												tempBusLineLow2.add(bli);
												doubleRightBusLineLow.put(bli,deviceInfo);
											}
											
										}else{
											return;
										}
									}
								}
							}if (breakerMapList!=null  && breakerMapList.size()!=0){
								if(busConnType==BusConnType.FIXEDWITHOUTTRAN){
								if((deviceInfo.getType().equals(DeviceType.TRANSFORMER))){
									String southConn=tranIdToConnIdLow.get(deviceInfo.getId());
									if(breakerMapList.size()==1){
										Map breakerMap=breakerMapList.get(0);
										if(breakerMap.get("id").equals(deviceInfo.getId())){
										}else{
											System.out.println("111  "+deviceInfo.getId()+"11来自tran一个设备 deviceInfo.getName()="+deviceInfo.getName());
											String connIdNew=GetAnotherNodeStr(breakerMap,connId);
											DeviceInfo df=null;
											String deviceType=breakerMap.get("type").toString();
											if(deviceType.equals(DeviceType.DISCONNECTOR)){
												df=new DisconnectorInfo();
											}else{
												df=new BreakerInfo();
											}
											df.setId(breakerMap.get("id").toString());
											df.setType(deviceType);
											df.setName(breakerMap.get("name").toString());
											df.setParentDeviceInfo(deviceInfo);
											df.setBelongTranNo(deviceInfo.getId());
											df.setBelongTranConnId(connId);
											df.setX(deviceInfo.getX());
											df.setY(deviceInfo.getY());
											if(southConn!=null && southConn.equals(connId)){
												df.setOrientation(OrientationType.SOUTH);
											}else{
												df.setOrientation(OrientationType.NORTH);
											}
											list.add(df.withVLineStr(df, deviceInfo));
											System.out.println("11来自tran df.getName()="+df.getName());
											//df.setBelongBreakerNo(df.getId());
											//System.out.println("11 deviceInfo.getBelongBreakerNo()="+deviceInfo.getBelongBreakerNo());
											if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
												//return;
											}else{
												secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
											}
										}
										//从tran出来后直接分支
									}else if(breakerMapList.size()==2){
										doubleTranForFixedLow.add(deviceInfo.getId());
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getId());
												System.out.println("33  deviceInfo.getId()="+deviceInfo.getId()+"33来自tran两个设备  deviceInfo.getName()="+deviceInfo.getName());
												System.out.println("33 df.getName()="+df.getName());
												df.setBelongTranConnId(connId);
												df.setBelongBreakerNo(df.getId());
												if(df.getId().equals(sortedStartDoubleBreakers[1])){
													df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
													df.setAssistBottomLine(true);
												}else if(df.getId().equals(sortedStartDoubleBreakers[0])){
													df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
												}else{
													df.setX(deviceInfo.getX());
												}
												df.setY(deviceInfo.getY());
												if(southConn.equals(connId)){
													df.setOrientation(OrientationType.SOUTH);
												}else{
													df.setOrientation(OrientationType.NORTH);
												}
												list.add(df.withVLineStr(df, deviceInfo));
												System.out.println("33 df.getBelongBreakerNo()="+df.getBelongBreakerNo());
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}
								}else if(breakerMapList.size()>=1 && !deviceInfo.getType().equals(DeviceType.TRANSFORMER)){
									//经过几个breaker后出现分支
									if(breakerMapList.size()==3 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
										System.out.println("3 deviceInfo.getName()3="+deviceInfo.getName());
										//doubleTranForFixedLow.add(deviceInfo.getBelongTranNo());
										int count=0;
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												count++;
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setBelongBreakerNo(df.getId());
												if(df.getId().equals(sortedStartDoubleBreakers[1])){
													df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
													df.setAssistBottomLine(true);
												}else if(df.getId().equals(sortedStartDoubleBreakers[0])){
													df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
												}else{
													if(count==1){
														df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
														df.setAssistBottomLine(true);
													}else{
														df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
													}
												}
												df.setY(deviceInfo.getY());
												df.setOrientation(deviceInfo.getOrientation());
												list.add(df.withVLineStr(df, deviceInfo));
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}else if(busbarSectionList.size()>0 && deviceInfo.getBelongTranNo()!=null){
										//碰到母线
										/*if(doubleTranForFixedLow.size()==1){
											if(doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){*/
												int count=0;
												for(int i=0;i<breakerMapList.size();i++){
													count++;
													Map breakerMap=breakerMapList.get(i);
													boolean repeatEver=false;
														for(String breakerId:doubleBreakers){
															if(breakerMap.get("id").equals(breakerId)){
																repeatEver=true;
															}
														}
													if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
														count--;
													}else{
														String connIdNew=GetAnotherNodeStr(breakerMap,connId);
														DeviceInfo df=null;
														String deviceType=breakerMap.get("type").toString();
														if(deviceType.equals(DeviceType.DISCONNECTOR)){
															df=new DisconnectorInfo();
														}else{
															df=new BreakerInfo();
														}
														df.setBelongBusNo(busbarSectionList.get(0).getMRID());
														df.setId(breakerMap.get("id").toString());
														df.setType(deviceType);
														df.setName(breakerMap.get("name").toString());
														df.setParentDeviceInfo(deviceInfo);
														df.setBelongTranNo(deviceInfo.getBelongTranNo());
														//df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
														df.setBelongBreakerNo(df.getId());
														
														boolean continueForth=true;
														if(deviceInfo.getOrientation().equals(OrientationType.SOUTH)){
															
															//双母左段
															if(!doubleBusIsConnected && busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(1))){
																df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance+deviceInfo.getWidth()/2+count*breakerToBreakerDistance);
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(1).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1).equals(df.getId())){
																	continueForth=false;
																	//count--;
																}
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1).equals(df.getId())){
																	//continueForth=false;
																	//count--;
																	df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance);
																}
																df.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
															//双母右段
															}else if((busbarSectionList.size()==1 && busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(2)) && doubleBusIsConnected)||(doubleBusIsConnected && busbarSectionList.size()==2 && busbarSectionList.get(1).getMRID().equals(sortedBusIdLow.get(2)))){
																df.setX(deviceInfo.getX()+deviceInfo.getWidth()/2+count*breakerToBreakerDistance);
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1).equals(df.getId())){
																	continueForth=false;
																	//count--;
																}
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(1).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1).equals(df.getId())){
																	//continueForth=false;
																	//count--;
																	df.setX(deviceInfo.getX()+maxBreakerNumForAllLow*breakerToBreakerDistance);
																}
																
																df.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
															}else{
															
																df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance/2+count*breakerToBreakerDistance);
																//if(busIdToJoinBreakersForFixedLow.get(busbarSectionList.get(0).getMRID()).get(0).equals(df.getId())){
																	//起始单母
																	if(busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(0))){
																		//df.setX(deviceInfo.getX()+maxBreakerNumForAllLow*breakerToBreakerDistance/2);
																		
																		df.setX(leftTranDistance+tranAndTranDistance-2*maxBreakerNumForAllLow*breakerToBreakerDistance+count*breakerToBreakerDistance);
																		
																	//结束单母
																	}else{
																		//df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance/2);
																		df.setX(leftTranDistance+tranAndTranDistance+maxBreakerNumForAllLow*breakerToBreakerDistance+count*breakerToBreakerDistance);

																	}
																	df.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
																	//count--;
																														
																//}
															}
															
														}else if(deviceInfo.getOrientation().equals(OrientationType.NORTH)){
															String startJoinId=null;
															if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID())!=null){
																startJoinId=busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1);
															}
															df.setY(deviceInfo.getY()-deviceInfo.getHeight()/2);
															df.setX(deviceInfo.getX()-maxBreakerNumForAllHigh*breakerToBreakerDistance/2+count*breakerToBreakerDistance);
															if(startJoinId!=null && breakerMap.get("id").equals(startJoinId)){
																df.setX(deviceInfo.getX()+maxBreakerNumForAllLow*breakerToBreakerDistance/2);
																count--;
															}
														}
														df.setOrientation(deviceInfo.getOrientation());
														if(toTranBreakerNo.get(0).equals(df.getId())){
															df.setOrientation(OrientationType.NORTH);
															df.setY(deviceInfo.getY()+deviceInfo.getHeight()+deviceInfo.getHeight()/2);
														}
														list.add(df.withVLineStr(df, deviceInfo));

														if("".equals(connIdNew)||!continueForth){//如果下一个连接点为空，当前循环终止
															//return;
														}else{
															secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
														}
													}
												/*}
											}*/
										}
										if(busbarSectionList.size()==2 && !doubleBusIsConnected){
											doubleBusIsConnected=true;
										}		
										//变压器之后或母线之后碰到母线之前有多个设备
									}else if(breakerMapList.size()>=1 && busbarSectionList.size()==0 && deviceInfo.getBelongTranNo()!=null){
										System.out.println("22一个设备 deviceInfo.getName()="+deviceInfo.getName());
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											boolean repeatEver=false;
											if(deviceInfo.getBelongTranConnId()==null){
												for(String breakerId:doubleBreakers){
													if(breakerMap.get("id").equals(breakerId)){
														repeatEver=true;
													}
												}
											}
											if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
												queryAndAddLine(deviceInfo);
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												if(deviceInfo.getBelongTranConnId()!=null)
												System.out.println("22  deviceInfo.getBelongTranConnId()="+deviceInfo.getBelongTranConnId());	
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												System.out.println("22 df.getName()="+df.getName());
												if(deviceInfo.getBelongBreakerNo()!=null){
													df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
												}
												if(deviceInfo.getBelongBusNo()!=null){
													df.setBelongBusNo(deviceInfo.getBelongBusNo());
												}
												df.setX(deviceInfo.getX());
												df.setY(deviceInfo.getY());
												df.setOrientation(deviceInfo.getOrientation());
												
												list.add(df.withVLineStr(df, deviceInfo));
												//df.setBelongBreakerNo(df.getBelongBreakerNo());
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}
								}
							}else if(busConnType==BusConnType.DOUBLEWITHMIDDLEBUS){
								//从TRANSFORMER发出
								if((deviceInfo.getType().equals(DeviceType.TRANSFORMER))){
									if(breakerMapList.size()==1){
										Map breakerMap=breakerMapList.get(0);
										if(breakerMap.get("id").equals(deviceInfo.getId())){
										}else{
											String connIdNew=GetAnotherNodeStr(breakerMap,connId);
											DeviceInfo df=null;
											String deviceType=breakerMap.get("type").toString();
											if(deviceType.equals(DeviceType.DISCONNECTOR)){
												df=new DisconnectorInfo();
											}else{
												df=new BreakerInfo();
											}
											df.setId(breakerMap.get("id").toString());
											df.setType(deviceType);
											df.setName(breakerMap.get("name").toString());
											df.setParentDeviceInfo(deviceInfo);
											df.setBelongTranNo(deviceInfo.getId());
											df.setBelongTranConnId(connId);
											df.setOrientation(deviceInfo.getOrientation());
											df.setX(deviceInfo.getX());
											if(!connId.equals(tranIdToConnForSort.get(deviceInfo.getId()).get("l"))){
												df.setX(deviceInfo.getX()+DeviceInfoList.maxBreakerNumForAllHigh*DeviceInfoList.breakerToBreakerDistance);
												df.setOrientation(OrientationType.NORTH);
											}
											
											df.setY(deviceInfo.getY());
											list.add(df.withVLineStr(df, deviceInfo));
											//df.setBelongBreakerNo(df.getId());
											//df.setBelongBreakerNo(df.getBelongBreakerNo());
											//System.out.println("11 deviceInfo.getBelongBreakerNo()="+deviceInfo.getBelongBreakerNo());
											if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
												//return;
											}else{
												secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
											}
										}
									}else if(breakerMapList.size()==2){
										
										
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getId());
												df.setBelongTranConnId(connId);
												df.setBelongBreakerNo(df.getId());
												df.setOrientation(deviceInfo.getOrientation());
												//df.setBelongBreakerNo(df.getBelongBreakerNo());
												if(!df.getId().equals(tranToLeftBreaker.get(df.getBelongTranNo()))){
													df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
													df.setAssistBottomLine(true);
												}else{
													df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
												}
												df.setY(deviceInfo.getY());
												list.add(df.withVLineStr(df, deviceInfo));
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}
								
									//从起始breaker发出
								}else if(breakerMapList.size()>=1 && !deviceInfo.getType().equals(DeviceType.TRANSFORMER)){
								

									//变压器之后或母线之后碰到母线之前有多个设备
									if(breakerMapList.size()<=2 && busbarSectionList.size()==0){
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
												queryAndAddLine(deviceInfo);
											}else{
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setBelongBusNo(deviceInfo.getBelongBusNo());
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setOrientation(deviceInfo.getOrientation());
												
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
												df.setBelongBusNo(deviceInfo.getBelongBusNo());
												//df.setBelongBreakerNo(df.getId());
												df.setX(deviceInfo.getX());
												df.setY(deviceInfo.getY());
												if(!busIdForHigh.get(0).equals(df.getBelongBusNo())){
													list.add(df.withVLineStr(df, deviceInfo));
												}
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}else if(busbarSectionList.size()>0){
										
										//碰到母线了
										/*if(doubleTranForFixedLow.size()==1){
											if(doubleTranForFixedLow.get(0).equals(deviceInfo.getBelongTranNo())){*/
										int count=0;
												for(int i=0;i<breakerMapList.size();i++){
													Map breakerMap=breakerMapList.get(i);
													boolean repeatEver=false;
													for(String breakerId:doubleBreakers){
														if(breakerMap.get("id").equals(breakerId)){
															repeatEver=true;
														}
													}
													if(breakerMap.get("id").equals(deviceInfo.getId())||repeatEver){
													}else{
														count++;
														String connIdNew=GetAnotherNodeStr(breakerMap,connId);
														DeviceInfo df=null;
														String deviceType=breakerMap.get("type").toString();
														if(deviceType.equals(DeviceType.DISCONNECTOR)){
															df=new DisconnectorInfo();
														}else{
															df=new BreakerInfo();
														}
														
														df.setBelongBusNo(busbarSectionList.get(0).getMRID());
														df.setId(breakerMap.get("id").toString());
														df.setType(deviceType);
														df.setName(breakerMap.get("name").toString());
														df.setParentDeviceInfo(deviceInfo);
														df.setBelongTranNo(deviceInfo.getBelongTranNo());
														df.setOrientation(deviceInfo.getOrientation());
														//df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
														df.setBelongBreakerNo(deviceInfo.getBelongBreakerNo());
														//df.setBelongBreakerNo(df.getId());
														//高压
														if(busbarSectionList.get(0).getVoltageLevel().equals(volLevels[0])){
															//二段母线
															if(busIdForHigh.get(1).equals(busbarSectionList.get(0).getMRID())){
																df.setX(deviceInfo.getX()+count*breakerToBreakerDistance);
																df.setY(deviceInfo.getY()-anotherAssisHeight/4+deviceInfo.getStandardDistance()/2);
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID())!=null&&df.getId().equals(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1))){
																	//continueForth=false;
																	count--;
																	df.setX(deviceInfo.getX()+maxBreakerNumForAllRealHigh*breakerToBreakerDistance);
																	df.setOrientation(OrientationType.SOUTH);
																	df.setY(deviceInfo.getY()-anotherAssisHeight/4-deviceInfo.getStandardDistance()/2);
																}
																if(df.getName().contains("母联")){
																	df.setX(deviceInfo.getX());
																	count--;
																}
																list.add(df.withVLineStr(df, deviceInfo));
															//一段母线
															}else if(busIdForHigh.get(0).equals(busbarSectionList.get(0).getMRID())){
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID())!=null&&df.getId().equals(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1))){
																	//continueForth=false;
																	double x=busInfoForRealHigh.get(0).getX();
																	double y=busInfoForRealHigh.get(0).getY();
																	count--;
																	df.setX(x+maxBreakerNumForAllRealHigh*breakerToBreakerDistance-deviceInfo.getStandardDistance()/2);
																	df.setOrientation(OrientationType.NORTH);
																	df.setY(y+deviceInfo.getStandardDistance()/2);
																	list.add(df.withVLineStr(df, deviceInfo));
																}
															//四段母线	
															}else if(busIdForHigh.get(2).equals(busbarSectionList.get(0).getMRID())){
																double x=busInfoForRealHigh.get(1).getX();
																double y=busInfoForRealHigh.get(1).getY();
																df.setX(x+busAndBusDistanceForLow+breakerToBreakerDistance*maxBreakerNumForAllRealHigh+count*breakerToBreakerDistance);
															    df.setY(y+deviceInfo.getStandardDistance()/2);
															    df.setOrientation(OrientationType.NORTH);
															    if(df.getName().contains("母联")){
															    	count--;
																	df.setX(x+busAndBusDistanceForLow+breakerToBreakerDistance*maxBreakerNumForAllRealHigh);
																}
															    list.add(df.withVLineStr(df, deviceInfo));
															
															}
														}else{
															Iterator it=busIdToJoinBreakersForSingleLow.keySet().iterator();
															boolean pass=false;
															if(busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(sortedBusIdLow.size()-2))||busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(sortedBusIdLow.size()-1))){
																while(it.hasNext()){
																	String key=it.next().toString();
																	List<String> value=busIdToJoinBreakersForSingleLow.get(key);
																	for(String s:value){
																		if(df.getId().equals(s)){
																			pass=true;
																			break;
																		}
																	}
																	
																}
															}
															
															//双母左段
															if(busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(0))||busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(sortedBusIdLow.size()-2))){
																df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance+deviceInfo.getWidth()/2+count*breakerToBreakerDistance);
																//有母联设备
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID())!=null&&df.getId().equals(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1))){
																	//continueForth=false;
																	count--;
																	df.setX(deviceInfo.getX()-maxBreakerNumForAllLow*breakerToBreakerDistance);
																}
																df.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
															//双母右段
															}else if(busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(1))||busbarSectionList.get(0).getMRID().equals(sortedBusIdLow.get(sortedBusIdLow.size()-1))){
																df.setX(deviceInfo.getX()+deviceInfo.getWidth()/2+count*breakerToBreakerDistance);
																//有母联设备
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID())!=null&&df.getId().equals(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1))){
																	//continueForth=false;
																	count--;
																	df.setX(deviceInfo.getX()+maxBreakerNumForAllLow*breakerToBreakerDistance);
																}
																
																df.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
															}else{
																double lastX=tempBusLineLow1.get(tempBusLineLow1.size()-1).getX();
																double lastY=tempBusLineLow1.get(tempBusLineLow1.size()-1).getY();
																df.setX(lastX+count*breakerToBreakerDistance);
																if(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).get(busIdToJoinBreakersForSingleLow.get(busbarSectionList.get(0).getMRID()).size()-1).equals(df.getId())){
																	//continueForth=false;
																	count--;
																	df.setX(lastX+maxBreakerNumForAllLow*breakerToBreakerDistance-df.getWidth()/2);
																}
																df.setY(lastY-deviceInfo.getHeight()/2);
															}
															if(!pass){
																list.add(df.withVLineStr(df, deviceInfo));
															}else{
																// count--;
															}
														}
														
														if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
															//return;
														}else{
															secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
														}
													}
												/*}
											}*/
										}
										/*if(busbarSectionList.size()==2 && doubleBusIsConnected==false){
											doubleBusIsConnected=true;
										}*/
										
									}//经过几个breaker后出现分支
									if(breakerMapList.size()==3 && busbarSectionList.size()==0){
										//doubleTranForFixedLow.add(deviceInfo.getBelongTranNo());
										int count=0; 
										for(int i=0;i<breakerMapList.size();i++){
											Map breakerMap=breakerMapList.get(i);
											if(breakerMap.get("id").equals(deviceInfo.getId())){
											}else{
												count++;
												String connIdNew=GetAnotherNodeStr(breakerMap,connId);
												DeviceInfo df=null;
												String deviceType=breakerMap.get("type").toString();
												if(deviceType.equals(DeviceType.DISCONNECTOR)){
													df=new DisconnectorInfo();
												}else{
													df=new BreakerInfo();
												}
												df.setBelongBusNo(deviceInfo.getBelongBusNo());
												df.setId(breakerMap.get("id").toString());
												df.setType(deviceType);
												df.setName(breakerMap.get("name").toString());
												df.setParentDeviceInfo(deviceInfo);
												df.setBelongTranNo(deviceInfo.getBelongTranNo());
												df.setBelongTranConnId(deviceInfo.getBelongTranConnId());
												df.setOrientation(deviceInfo.getOrientation());
												df.setBelongBreakerNo(df.getId());
												//df.setBelongBreakerNo(df.getBelongBreakerNo());
												//如果不是左分支
												if((!df.getId().equals(tranToLeftBreaker.get(df.getBelongTranNo()))&&!df.getId().equals(tranToLeftBreakerForHigh.get(df.getBelongTranNo())))){
													df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
													df.setAssistBottomLine(true);
													if(df.getBelongBusNo()!=null){
														if(count==1){
															df.setX(deviceInfo.getX()+df.getWidth()/2*breakerToBreakerDistance/50);
															df.setAssistBottomLine(true);
														}else{
															df.setAssistBottomLine(false);
															df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
														}
													}
													
												}else{
													df.setX(deviceInfo.getX()-df.getWidth()/2*breakerToBreakerDistance/50);
												}
												df.setY(deviceInfo.getY());
												if(!busIdForHigh.get(0).equals(df.getBelongBusNo())){
													list.add(df.withVLineStr(df, deviceInfo));
												}
												if("".equals(connIdNew)){//如果下一个连接点为空，当前循环终止
													//return;
												}else{
													secondFindDeviceByConnIdForFixedWithoutTran(connIdNew,df);
												}
											}
										}
									}
								}
							}
								
						}
						busNo=null;
						}					

	/**
	 * 获取当前设备的另一个连接点
	 * @param breakerMap 设备
	 * @param connId  当前设备一侧的连接点
	 * @return
	 */
	public String GetAnotherNodeStr(Map<String,String> breakerMap,String connId){
		String breakerConnId ="";
	
		String sId = breakerMap.get("physicNodeBegin");
		String eId = breakerMap.get("physicNodeEnd");
		if("".equals(connId) || connId ==null){
			return breakerConnId;
		} 
		if(!sId.equals(connId)){//查找开关所连的另一个端点信息
			breakerConnId = sId;
		}else if(eId!=null&&!eId.equals(connId)){
			breakerConnId = eId;
		}
		return breakerConnId;
	}
	
	/**
	 * 获取当前设备的另一个连接点
	 * @param breakerMap 设备
	 * @param connId  当前设备一侧的连接点
	 * @return
	 */
	public String GetAnotherNodeStrForTran(PowerTransformer powerTransformer,String connId){
		String tranConnId ="";
		if("".equals(connId) || connId ==null){
			return tranConnId;
		} 
		Map<String,TransformerWinding> twMap = powerTransformer.getTransformerWindingMap();//获取主变绕组
		for(String twType:twMap.keySet()){
			TransformerWinding transformerWinding = twMap.get(twType);
			String connectId = transformerWinding.getPhysicNodeBegin();//获取变压器绕组的连接点信系
			if(!connectId.equals(connId)){
				tranConnId=connectId;
			}
		}
		return tranConnId;
	}
	
	/**
	 * 获取当前设备连接点
	 * @param breakerMap 设备
	 * @param connId  当前设备一侧的连接点
	 * @return
	 */
	public List<String> GetAnotherNodeStrForTran(PowerTransformer powerTransformer){
		List<String> tranConnIds =new ArrayList<String>();
		Map<String,TransformerWinding> twMap = powerTransformer.getTransformerWindingMap();//获取主变绕组
		for(String twType:twMap.keySet()){
			TransformerWinding transformerWinding = twMap.get(twType);
			String connectId = transformerWinding.getPhysicNodeBegin();//获取变压器绕组的连接点信系
			tranConnIds.add(connectId);
		}
		return tranConnIds;
	}
	
	//根据电压等级获取母线
	private List<BusbarSection> getBusLineListByVol(int vol){
		List<BusbarSection> result = new ArrayList<BusbarSection>();
		
		List<BusbarSection> busList = new ArrayList<BusbarSection>();
		for(BusbarSection busbarSection  : busbarSectionList){
			int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
			if(vol==voltLevel) {
				result.add(busbarSection);
			}
		}
		return result;
	}
	
	//获取高压母线
	private List<BusbarSection> getStartBusLineList(){
		List<BusbarSection> result = new ArrayList<BusbarSection>();
		int max = 0;
		for(BusbarSection busbarSection  : busbarSectionList){
			int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
			if(max<voltLevel){
				max = voltLevel;
			}
		}
		List<BusbarSection> busList = new ArrayList<BusbarSection>();
		for(BusbarSection busbarSection  : busbarSectionList){
			int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
			if(max==voltLevel) {
				result.add(busbarSection);
			}
		}
		return result;
	}
	//获取中压母线
		private List<BusbarSection> getMiddleBusLineList(){
			List<BusbarSection> result = new ArrayList<BusbarSection>();
			int max = 0;
			for(BusbarSection busbarSection  : busbarSectionList){
				int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
				if(max<voltLevel){
					max = voltLevel;
				}
			}
			int min = Integer.parseInt(busbarSectionList.get(0).getVoltageLevel());
			for(BusbarSection busbarSection  : busbarSectionList){
				int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
				if(min>voltLevel){
					min = voltLevel;
				}
			}
			for(BusbarSection busbarSection  : busbarSectionList){
				int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
				if(min<voltLevel && max>voltLevel) {
					result.add(busbarSection);
				}
			}
			return result;
		}
	//获取低压母线
	private List<BusbarSection> getEndBusLineList(){
		List<BusbarSection> result = new ArrayList<BusbarSection>();
		int min = Integer.parseInt(busbarSectionList.get(0).getVoltageLevel());
		for(BusbarSection busbarSection  : busbarSectionList){
			int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
			if(min>voltLevel){
				min = voltLevel;
			}
		}
		List<BusbarSection> busList = new ArrayList<BusbarSection>();
		for(BusbarSection busbarSection  : busbarSectionList){
			int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
			if(min==voltLevel) {
				result.add(busbarSection);
			}
		}
		return result;
	}
	
	public int getBusLineType(){
		int result = 0;
		int tranListCount = this.tranList.size();
		int lowBusLineCount = getEndBusLineList().size();
		System.out.println("tranCount="+tranListCount+"lowBusLineCount="+lowBusLineCount);
		if(tranListCount==0||tranListCount==lowBusLineCount){
			result = BusConnType.SINGLE;
		}else if(lowBusLineCount==tranListCount*2){
			result = BusConnType.DOUBLE;
		}else if(lowBusLineCount>=tranListCount+1&&getMiddleBusLineList().size()==0){
			result = BusConnType.FIXED;
		}else if(getMiddleBusLineList().size()!=0){
			result = BusConnType.DOUBLEWITHMIDDLEBUS;
		}
		return result;
	}
	
	/**
	 * 合并母线连接的开关和刀闸数据集合
	 * @param connBreakerList
	 * @param connDisconnectorList
	 * @return
	 */
	public List<Map<String,String>> joinBreakerData(List<Breaker> connBreakerList,List<Disconnector> connDisconnectorList){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(Breaker b : connBreakerList){
			Map<String,String> map = new HashMap<String, String>();
			map.put("name", b.getName());
			map.put("id",b.getMRID());
			map.put("physicNodeBegin", b.getPhysicNodeBegin());
			map.put("physicNodeEnd", b.getPhysicNodeEnd());
			map.put("type", DeviceType.BREAKER);
			result.add(map);
		}
		for(Disconnector d : connDisconnectorList){
			Map<String,String> map = new HashMap<String, String>();
			map.put("name", d.getName());
			map.put("id",d.getMRID());
			map.put("physicNodeBegin", d.getPhysicNodeBegin());
			map.put("physicNodeEnd", d.getPhysicNodeEnd());
			map.put("type", DeviceType.DISCONNECTOR);
			result.add(map);
		}
		return result;
	}
	/**
	 * 根据连接点获取连接的主变信息
	 * @param connId
	 * @return
	 */
	private List<PowerTransformer> getTranByConnId(String connId){
		List<PowerTransformer> result = new ArrayList<PowerTransformer>();
		for(PowerTransformer powerTransformer : tranList){
			Map<String,TransformerWinding> twMap = powerTransformer.getTransformerWindingMap();//获取主变绕组
			for(String twType:twMap.keySet()){
				TransformerWinding transformerWinding = twMap.get(twType);
				String connectId = transformerWinding.getPhysicNodeBegin();//获取变压器绕组的连接点信系
				if(connectId.equals(connId)){
					result.add(powerTransformer);
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取变压器另一侧连接点
	 */
	private String getTranAnotherConnId(PowerTransformer powerTransformer, String connId){
		Map<String,TransformerWinding> twMap = powerTransformer.getTransformerWindingMap();
		for(String twType:twMap.keySet()){
			if("低".equals(twType)){
			TransformerWinding transformerWinding = twMap.get(twType);
			String id = transformerWinding.getPhysicNodeBegin();
				return id;
			}
			}
		return null;
		
	}
	
	/**
	 * 根据连接点获取连接的开关信息
	 * @param connId
	 * @return
	 */
	private List<Breaker> getBreakerByConnId(String connId){
		List<Breaker> result = new ArrayList<Breaker>();
		for(Breaker breaker : breakerList){
			if(connId.equals(breaker.getPhysicNodeBegin())  || connId.equals(breaker.getPhysicNodeEnd()) ){
				result.add(breaker);
			}
		}
		return result;
	}
	
	
	/*private List<Map<String,String>> getBreakerByConnId(String connId){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(Breaker breaker : breakerList){
			if(connId.equals(breaker.getPhysicNodeBegin())  || connId.equals(breaker.getPhysicNodeEnd()) ){
				Map<String,String> map = new HashMap<String, String>();
				map.put("name", breaker.getName());
				map.put("id",breaker.getMRID());
				map.put("physicNodeBegin", breaker.getPhysicNodeBegin());
				map.put("physicNodeEnd", breaker.getPhysicNodeEnd());
				map.put("type", DeviceType.BREAKER);
				result.add(map);
			}
		}
		
		for(Disconnector disconnector : disconnectorList){
			if(connId.equals(disconnector.getPhysicNodeBegin())  || connId.equals(disconnector.getPhysicNodeEnd()) ){
				Map<String,String> map = new HashMap<String, String>();
				map.put("name", disconnector.getName());
				map.put("id",disconnector.getMRID());
				map.put("physicNodeBegin", disconnector.getPhysicNodeBegin());
				map.put("physicNodeEnd", disconnector.getPhysicNodeEnd());
				map.put("type", DeviceType.DISCONNECTOR);
				result.add(map);
			}
		}
		
		return result;
	}*/

	
	/**
	 * 根据连接点获取连接的刀闸信息
	 * @param connId
	 * @return
	 */
	private List<Disconnector> getDisconnectorByConnId(String connId){
		List<Disconnector> result = new ArrayList<Disconnector>();
		for(Disconnector disconnector : disconnectorList){
			if(connId.equals(disconnector.getPhysicNodeBegin())  || connId.equals(disconnector.getPhysicNodeEnd()) ){
				result.add(disconnector);
			}
		}
		return result;
	}
	
	/**
	 * 根据连接点获取连接的母线信息
	 * @param connId
	 * @return
	 */
	private List<BusbarSection> getBusByConnId(String connId){
		List<BusbarSection> result = new ArrayList<BusbarSection>();
		for(BusbarSection busbarSection : busbarSectionList){
			if(connId.equals(busbarSection.getPhysicNodeBegin())){
				result.add(busbarSection);
			}
		}
		return result;
	}
	
	/**
	 * 根据连接点获取连接的线路信息
	 * @param connId
	 * @return
	 */
	private List<ACLineDot> getLineByConnId(String connId){
		List<ACLineDot> result = new ArrayList<ACLineDot>();
		for(ACLineDot acLineDot : lineList){
			if(connId.equals(acLineDot.getPhysicNodeBegin())){
				result.add(acLineDot);
			}
		}
		return result;
	}

	
	
}
