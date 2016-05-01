package com.jb.genemap.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.flowCal.device.model.main.ACLineDot;
import com.flowCal.device.model.main.Breaker;
import com.flowCal.device.model.main.BusbarSection;
import com.flowCal.device.model.main.Disconnector;
import com.flowCal.device.model.main.PowerTransformer;
import com.flowCal.device.model.main.TransformerWinding;

/**
 * 获取svg图形参数信息
 * 
 * @author yjl
 * 
 */
public class GetParamMap {
	public GetParamMap() {
		ParseCime parseCime = new ParseCime();// 创建解析cime文件的类
		this.tranList = parseCime.getTranMap();
		this.breakerList = parseCime.getBreakerMap();
		this.disconnectorList = parseCime.getDisconnectorMap();
		this.busbarSectionList = parseCime.getBusMap();
		this.lineList = parseCime.getLineMap();
	}

	private List<DeviceInfo> paramList = new ArrayList<DeviceInfo>();// 设备图形参数集合
	private List<PowerTransformer> tranList = new ArrayList<PowerTransformer>();// cime文件中的主变集合数据信息
	private List<Breaker> breakerList = new ArrayList<Breaker>();// cime文件中的开关集合数据信息
	private List<Disconnector> disconnectorList = new ArrayList<Disconnector>();// cime文件中的刀闸集合数据信息
	private List<BusbarSection> busbarSectionList = new ArrayList<BusbarSection>();// cime文件中的母线集合数据信息
	private List<ACLineDot> lineList = new ArrayList<ACLineDot>();// cime文件中的线路集合数据信息
	private List<Breaker> tranBreakerList = new ArrayList<Breaker>();// 主变绕组连接的开关集合（判断母线所连接的开关的绘图方向）
	private List<Disconnector> tranDisconnectorList = new ArrayList<Disconnector>();// 主变绕组连接的刀闸集合（判断母线所连接的刀闸的绘图方向）

	private double x = 1000;// 画布长度

	private double y = 500;// 画布宽度

	private double topBusDistance = 100;// 母线距离画布上方距离

	private double busAndBusDistance = 2;// 母线与母线间的距离

	private double leftBusDistance = 100;// 母线距离画布上方距离

	private double connLineLength = 0.5;// 连线宽度

	private double connLineLength1 = 20;// 连线宽度

	private double connLineThicker = 1;// 连线高度`

	private double breakerWidth = 32;// 开关宽度

	private double breakerHeight = 32;// 开关高度

	private double disconnectorWidth = 32;// 刀闸宽度

	private double disconnectorHeight = 32;// 刀闸高度

	private double busThicker = 2;// 母线厚度

	private double tranWidth = 32;// 主变宽度

	private double tranHeight = 32;// 主变高度

	private double busLineLength = 250;// 母线长度
	
	private double lineLength = 4;

	public static int i = 0;
	
	public boolean transformFlag= true;
	
	public  boolean f = true;//回调只执行一次的标识
	
	public  int flagNum = 0;//回调只执行2次的标识
	
//	public void

	/**
	 * 设置主变数据集
	 */
	public void setDevMap(DeviceInfo deviceInfo,String connId) {

		List<Breaker> connBreakerList = getBreakerByConnId(connId);// 获取连接母线的所有开关数据集合
		List<Disconnector> connDisconnectorList = getDisconnectorByConnId(connId);// 获取连接母线的所有开关数据集合
		List<Map<String, String>> breakerMapList = joinBreakerData(
				connBreakerList, connDisconnectorList);
		int breakerCount = 0;
		int connDisconnectorCount = 0;
		if (connBreakerList != null && connBreakerList.size() != 0) {
			breakerCount = connBreakerList.size() - 1;
		}

		if (connDisconnectorList != null && connDisconnectorList.size() != 0) {
			connDisconnectorCount = connDisconnectorList.size() - 1;
		}
		double breakerX = busLineLength
				/ (breakerCount + connDisconnectorCount + 2);// 计算母线上方刀闸间距离
		// int upBreakerFlag = 0;//母线上方的开关数量计数器
		// int upDisconnectorFlag = 0;//母线上方的开关数量计数器
		int switchFlag = 0;

		for (int j = 0; j < breakerMapList.size(); j++) {
			Map<String, String> breakerMap = breakerMapList.get(j);
			String breakerConnId = GetAnotherNodeStr(breakerMap, connId);

//			if (tranBreakerMapList.contains(breakerMap)) {// 判断当前母线所连开关的绘图方向（向下绘图）
//
//				x = newX + busLineLength / 2;// 设置连线x坐标值
//				y = topBusDistance + connLineLength1;// 设置y坐标值
//				DeviceInfo cLine = addConnectLineParamList(x, y, "hline");
//				cLine.setDev(deviceInfo);
//				x = newX + busLineLength / 2;// 设置x坐标值
//				y = y + disconnectorHeight / 2;// 设置y坐标值 10连线长度
//				DeviceInfo breakerInfo = addDisconnectorParamList(x, y,
//						breakerMap.get("name"), breakerMap.get("id"),
//						breakerMap.get("type"));
//				breakerInfo.setDev(cLine);
//
//				findDeviceByConnIdDown(breakerConnId, breakerInfo);
//
//			} else {// （向上绘图）
				switchFlag++;
				x = deviceInfo.getX() + breakerX * switchFlag;// 设置连线x坐标值
				y = deviceInfo.getY() - connLineLength;// 设置y坐标值
				DeviceInfo cLine = addConnectLineParamList(x, y, "hline");
				cLine.setDev(deviceInfo);
				y = y - disconnectorWidth;// 设置y坐标值 10连线长度
				DeviceInfo breakerInfo = addBreakerParamList(x, y,
						breakerMap.get("name"), breakerMap.get("id"),
						breakerMap.get("type"));
				breakerInfo.setDev(cLine);
			   //从list中移除开关或刀闸
				removeBreak( breakerMap.get("id"),breakerMap.get("type"));
				// x = newX+breakerX*switchFlag;//设置连线x坐标值
				// y = y-breakerHeight/2;//设置y坐标值
				// addConnectLineParamList(x, y);

				findDeviceByConnIdUp(breakerConnId, breakerInfo,deviceInfo);
//			}
		}
		i++;
		// }
	}
	
	private void removeBreak(String id, String type) {
		if("breaker".equals(type)){
			for(Breaker a :breakerList){
				if(id.equals(a.getMRID())){
					breakerList.remove(a);
					break;
				}
			}
		}else{
			for(Disconnector a :disconnectorList){
				if(id.equals(a.getMRID())){
					disconnectorList.remove(a);
					break;
				}
			}
		}
	
}

	public void setDeviceParam(){
		Map<String,Object> devMap = getDev();//获取起点母线
		DeviceInfo deviceInfo = (DeviceInfo) devMap.get("devInfo");
		String connId = String.valueOf(devMap.get("connId"));
		setDevMap(deviceInfo,connId);
	}
	
	/**
	 * 获取第一条母线设备信息
	 * @return
	 */
	public Map<String,Object> getDev(){
		Map<String,Object> result = new HashMap<String, Object>();
		List<BusbarSection> busList = getStartBusLineList();// 获取起始高压侧母线数据集合（绘图以此为起点）
		// setTranSwith();//设置主变所连接开关或刀闸集合
		// int busCount = busList.size();//获取母线高压侧根数
		// findDeviceByConnId2(busList.get(0));
		// double length =
		// (x-leftBusDistance*2-(busCount-1)*busAndBusDistance)/busCount;//获取母线长度
		// for(int i=0;i<busList.size();i++){//循环cime文件解析的母线数据集
		BusbarSection busbarSection = null;// 获取母线对象
		for(BusbarSection bus : busList){
			String connId = bus.getPhysicNodeBegin();
			int connBusCount = 0;
			List<Breaker> connBreakerList = getBreakerByConnId(connId);// 获取连接母线的所有开关数据集合
			List<Disconnector> connDisconnectorList = getDisconnectorByConnId(connId);// 获取连接母线的所有开关数据集合
			List<Map<String, String>> breakerBusMapList = joinBreakerData(
					connBreakerList, connDisconnectorList);
			for(Map<String, String> map : breakerBusMapList){
				String id = map.get("id");
				String breakerConnId = GetAnotherNodeStr(map, connId);
				List<BusbarSection> busbarSectionList = getBusByConnId(breakerConnId);
//					else if(breakerBusMapList.size()==1){//找不到任何设备，1是： 当前设备
//					continue;
//				}else if(breakerBusMapList.size()==2){//找到了下一个开关
				List<Breaker> breakerList = getBreakerByConnId(breakerConnId);
				List<Disconnector> disconnectorList = getDisconnectorByConnId(breakerConnId);
				List<Map<String, String>> breakerMapList = joinBreakerData(
						breakerList, disconnectorList);

				while ((busbarSectionList == null || busbarSectionList.size() == 0)) {
					if (breakerMapList.size() == 1) {
						break;
					} else {
						for (Map<String, String> bMap : breakerMapList) {
							String bId = bMap.get("id");
							if (!id.equals(bId)) {
								breakerConnId = GetAnotherNodeStr(bMap, breakerConnId);
								breakerList = getBreakerByConnId(breakerConnId);
								disconnectorList = getDisconnectorByConnId(breakerConnId);
								breakerMapList = joinBreakerData(breakerList,
										disconnectorList);
								busbarSectionList = getBusByConnId(breakerConnId);
								id = bId;
								break;
							}
						}
					}

				}
				if(busbarSectionList!=null && busbarSectionList.size()!=0){//找到母线，结束
					connBusCount ++;
					continue;
				}
			}
			if(connBusCount==1){
				busbarSection = bus;
				break;
			}
		}
		double busX = 0;// 设置x坐标值
		double busY = 100;// 设置y坐标值
		DeviceInfo deviceInfo = addBusParamList(busX, busY, busbarSection.getName(),
				busbarSection.getMRID(), "red", busLineLength);// 增加母线图形参数
		busbarSectionList.remove(busbarSection);
		String connId = busbarSection.getPhysicNodeBegin();// 获取母线的连接点信息
		result.put("devInfo", deviceInfo);
		result.put("connId", connId);
		return result;
	}

//	public void findDeviceByConnIdDown(String connId, DeviceInfo deviceInfo) {
//		String type = deviceInfo.getDevType();
//		Map<String, Object> result = new HashMap<String, Object>();
//		List<BusbarSection> busbarSectionList = getBusByConnId(connId);
//
//		List<Breaker> breakerList = getBreakerByConnId(connId);
//
//		List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);
//
//		List<Map<String, String>> breakerMapList = joinBreakerData(breakerList,
//				disconnectorList);
//
//		List<ACLineDot> lineList = getLineByConnId(connId);
//
//		List<PowerTransformer> powerTransformerList = getTranByConnId(connId);
//
//		if (powerTransformerList.size() != 0 && powerTransformerList != null
//				&& !"tran".equals(deviceInfo.getDevType())||transformFlag==true) {
//			//根据devinfo找父节点,回找list
//			//向下移动，将变压器的list数据，以母线为轴，对称移动？？？？？？？注意：立体图形！
//			//找当前变压器低压侧绕组（只执行一次）
//			//向下递归画图
//			
//			transformFlag =false;
//			
//			
////			PowerTransformer powerTransformer = powerTransformerList.get(0);
////			double breakX = deviceInfo.getX();
////			double breakY = deviceInfo.getY();
////			addConnectLineParamList(breakX, breakY + breakerHeight, "hline");
////			DeviceInfo dev = addTranParamList(breakX, breakY + breakerHeight
////					+ connLineLength1, "tran", powerTransformer.getMRID());
////			String anotherConnId = getTranAnotherConnId(powerTransformer,
////					connId);
////			findDeviceByConnIdDown(anotherConnId, dev);
//
//		} else if (busbarSectionList != null && busbarSectionList.size() != 0) {
//			BusbarSection busbarSection = busbarSectionList.get(0);
//			double busX = deviceInfo.getX();
//			double busY = deviceInfo.getY();
//			addConnectLineParamList(busX, busY + breakerHeight, "hline");
//			DeviceInfo Busdev = addBusParamList(busX, busY + breakerHeight
//					+ connLineLength1, busbarSection.getName(),
//					busbarSection.getMRID(), "green", 20);
//
//		} else if (breakerMapList != null && breakerMapList.size() != 0) {
//			String breakerType = breakerMapList.get(0).get("type");
//			double breakX = deviceInfo.getX();
//			double breakY = deviceInfo.getY();
//			int i = 0;// 连接多个开关计数器
//			Boolean flag = true;
//			for (Map<String, String> idMap : breakerMapList) {// 只有一个，所以可用breakX、breakY的值画线
//				String id = idMap.get("id");
//				String devId = deviceInfo.getId();
//
//				if (!id.equals(deviceInfo.getId()) && devId != null) {// 找到设备，并将<连接线,设备>组合绘画
//					if (busbarSectionList == null
//							|| busbarSectionList.size() == 0) {
//						if ("tran".equals(deviceInfo.getDevType())) {
//							double tranBreakerX = breakX + i * 50;
//							double tranBreakerY = breakY + tranHeight;
//							addConnectLineParamList(tranBreakerX, tranBreakerY,
//									"hline");
//							if (flag == true) {
//								addConnectLineParamList(tranBreakerX
//										+ disconnectorWidth / 2,
//										tranBreakerY - 13, "vline");
//								flag = false;
//							}
//							DeviceInfo tranBreakerInfo = addBreakerParamList(
//									tranBreakerX, tranBreakerY
//											+ disconnectorWidth / 2,
//									idMap.get("name"), idMap.get("id"),
//									idMap.get("type"));
//							removeBreak( idMap.get("id"),idMap.get("type"));
//							String breakerConnId = GetAnotherNodeStr(idMap,
//									connId);
//							i++;
//							findDeviceByConnIdDown(breakerConnId,
//									tranBreakerInfo);
//						} else {
//							String name = deviceInfo.getName();
//							double lineX = breakX + i * 50;// 设置连线x坐标值
//							double lineY = breakY + breakerHeight;// 设置y坐标值
//							addConnectLineParamList(lineX, lineY, "hline");
//							DeviceInfo breakerInfo = addBreakerParamList(lineX,
//									lineY + connLineLength1, idMap.get("name"),
//									idMap.get("id"), idMap.get("type"));
//							// 编写连接点函数
//							String breakerConnId = GetAnotherNodeStr(idMap,
//									connId);
//							if ("".equals(breakerConnId)) {// 如果下一个连接点为空，当前循环终止
//								break;
//							}
//
//							findDeviceByConnIdDown(breakerConnId, breakerInfo);
//
//						}
//					} else { //开关另一个连接点，连接到母线上，此时，判断此为母联
//                           //??待写
//						//根据devinfo找父节点,找到母联list
//						//将母联list，移动到母线的左、右侧。（根据母联数量判断，加一次母联，则数量加1）
//						//画连接线，一横，一竖
//						//画母线
//						//调用setDevMap()函数，回调
//					}
//				}
//			}
//		}
//	}

	/**
	 * 获取设备连接点连接的下一个设备,向上
	 * 
	 * @param connId
	 * @return
	 */
	public void findDeviceByConnIdUp(String connId, DeviceInfo deviceInfo,DeviceInfo lastBus) {
		String type = deviceInfo.getDevType();
		Map<String, Object> result = new HashMap<String, Object>();
		List<BusbarSection> busbarSectionList = getBusByConnId(connId);

		List<Breaker> breakerList = getBreakerByConnId(connId);

		List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);

		List<Map<String, String>> breakerMapList = joinBreakerData(breakerList,
				disconnectorList);

		List<ACLineDot> lineList = getLineByConnId(connId);

		List<PowerTransformer> powerTransformerList = getTranByConnId(connId);

		// if(busbarSectionList!=null && busbarSectionList.size()!=0 &&
		// !type.equals("busline")){//母线递归添加图形参数信息
		// // findDeviceByConnId(connId,deviceInfo);
		// // result.put("busline", busbarSectionList.get(0));}else
		if (breakerMapList != null && breakerMapList.size() != 0) {// 开关递归添加图形参数信息
			String breakerType = breakerMapList.get(0).get("type");
			double breakX = deviceInfo.getX();
			double breakY = deviceInfo.getY();
			for (Map<String, String> idMap : breakerMapList) {// 只有一个，所以可用breakX、breakY的值画线
	
				String id = idMap.get("id");
				String devId = deviceInfo.getId();
				if (!id.equals(deviceInfo.getId()) && devId != null) {// 找到设备，并将<连接线,设备>组合绘画
					if (busbarSectionList == null
							|| busbarSectionList.size() == 0) {
						String name = deviceInfo.getName();
						double lineX = breakX;// 设置连线x坐标值
						double lineY = breakY - breakerHeight / 2;// 设置y坐标值
						DeviceInfo cLine = addConnectLineParamList(lineX, lineY, "hline");
						cLine.setDev(deviceInfo);
						DeviceInfo breakerInfo = addBreakerParamList(lineX,
								lineY - disconnectorWidth, idMap.get("name"),
								idMap.get("id"), idMap.get("type"));
						breakerInfo.setDev(cLine);
						removeBreak( idMap.get("id"),idMap.get("type"));
						// 编写连接点函数
						String breakerConnId = GetAnotherNodeStr(idMap, connId);
						if ("".equals(breakerConnId)) {// 如果下一个连接点为空，当前循环终止
							break;
						}

						findDeviceByConnIdUp(breakerConnId, breakerInfo,lastBus);
					}else if(f){
					//开关另一个连接点，连接到母线上，此时，判断此为母联
                    //??待写
					//根据devinfo找父节点,找到母联list
						BusbarSection currentBus = busbarSectionList.get(0);
					List<DeviceInfo> list =new ArrayList<DeviceInfo>();
					DeviceInfo bus = new DeviceInfo();
					Map<String,Object> connMap = findConnList(deviceInfo);//修改list和bus的值
					 list = (List<DeviceInfo>) connMap.get("list");
					 bus = (DeviceInfo) connMap.get("bus");
					//将母联list，移动到母线的左、右侧。（根据母联数量判断，加一次母联，则数量加1）
					 if(lastBus.getBusConnCount()==0){
						 moveBusConnList(list,lastBus,currentBus,"right");
						 
					 }else{
						 moveBusConnList(list,lastBus,currentBus,"left");
					 }
					 bus.setBusConnCount(bus.getBusConnCount()+1);
					f=false;
				}
				}
			}
			Map<String, String> breakerMap = breakerMapList.get(0);

			// x = newX+length/2;//设置连线x坐标值
			// y = topBusDistance+connLineLength1;//设置y坐标值
			// addConnectLineParamList(x, y);
			// x = newX+length/2;//设置x坐标值
			// y = y+disconnectorHeight/2;//设置y坐标值 10连线长度
			// addDisconnectorParamList(x, y, breakerMap.get("name"));
			//
			// x = newX+length/2;//设置连线x坐标值
			// y = y+connLineLength1+10;//设置y坐标值
			// addConnectLineParamList(x, y);

		} else if (lineList != null && lineList.size() != 0
				&& !type.equals("hline")) {// 线路递归添加图形参数信息
		// result.put("hline", lineList.get(0));
			ACLineDot acLine = lineList.get(0);
			DeviceInfo line = addConnectLineParamList(deviceInfo.getX(), deviceInfo.getY()-lineLength*30, "hline",lineLength);
			
			// doub

		} else if (powerTransformerList != null
				&& powerTransformerList.size() != 0 && !type.equals("tran")) {// 主变递归添加图形参数信息
//			result.put("tran", powerTransformerList.get(0));
			List<DeviceInfo> list =new ArrayList<DeviceInfo>();
			DeviceInfo bus = new DeviceInfo();
			//根据devinfo找父节点,回找list
			 Map<String,Object> connMap = findConnList(deviceInfo);//修改list和bus的值
			 list = (List<DeviceInfo>) connMap.get("list");
			 bus = (DeviceInfo) connMap.get("bus");
			//向下移动，将变压器的list数据，以母线为轴，对称移动？？？？？？？注意：立体图形！
			 moveTransList(list,bus,powerTransformerList.get(0));
			//找当前变压器低压侧绕组（只执行一次）
			 
			//向下递归画图
//			 findDeviceByConnIdUp(breakerConnId, breakerInfo,lastBus); 
			 
			 
			 
			 
			 
			 
			 
			 
			 
		} else {
			return;
		}
	}

	private void moveTransList(List<DeviceInfo> list,DeviceInfo bus,PowerTransformer powerTransformer) {
		double lineX =0;
		double x = 0;
		double y = bus.getY();
		double lowY = 0;
		for(DeviceInfo dev :list){
			String id = dev.getId();
			for(DeviceInfo devParam : paramList){
				String paramId = devParam.getId();
				if(paramId.equals(id)){
			
					if("hline".equals(devParam.getDevType())){
						double newY = 2*y-devParam.getY()+16;
						devParam.setY(newY);
						if(lowY<newY){
							lowY=newY;
						}
						lineX = devParam.getX();
					}else{
						double newBreakerY = 2*y-devParam.getY();
						devParam.setY(newBreakerY);
						if(lowY<newBreakerY){
							lowY=newBreakerY;
						}
						x = devParam.getX();
					}
					break;
				}
			}
		}
		DeviceInfo  lineDev = addConnectLineParamList(lineX, lowY+disconnectorHeight, "hline");
		DeviceInfo dev = addTranParamList(x, lowY+disconnectorHeight+connLineLength*30, "tran", powerTransformer.getMRID());
		
	// TODO Auto-generated method stub
	
	}

	private void moveBusConnList(List<DeviceInfo> list, DeviceInfo bus,BusbarSection currentBus,String flag) {
		if("right".equals(flag)){
			double x =bus.getX()+busLineLength;
			double y = 5500;
			//向右移动母联设备
			for(DeviceInfo dev :list){
				String id = dev.getId();
				for(DeviceInfo devParam : paramList){
					String paramId = devParam.getId();
					if(paramId.equals(id)){
						if(y>devParam.getY()){
							y=devParam.getY();
						}
						devParam.setX(x);
						break;
					}
				}
			}
			//画一次，bus的母联数量加1
			//添加方法：获取母联list中y值最小的设备，并返回
			//画连线
			addConnectLineParamList(x+breakerWidth/2, y-breakerHeight/2, "vline",busAndBusDistance);//横线,
			addConnectLineParamList(x+busAndBusDistance*30, y, "hline",(bus.getY()-y)/30);//竖线       1:30
			//画母线
			DeviceInfo currBus = addBusParamList(x+busAndBusDistance*30, bus.getY(), currentBus.getName(),
					currentBus.getMRID(), "red", busLineLength);
			busbarSectionList.remove(currentBus);
			//回调setDevMap(参数当前的母线)
			setDevMap(currBus,currentBus.getPhysicNodeBegin());
			
		}else if("left".equals(flag)){
			double x =bus.getX();
			double y = 5500;
			//向左移动母联设备
			for(DeviceInfo dev :list){
				String id = dev.getId();
				for(DeviceInfo devParam : paramList){
					String paramId = devParam.getId();
					if(paramId.equals(id)){
						if(y>devParam.getY()){
							y=devParam.getY();
						}
						devParam.setX(x);
						break;
					}
				}
			}
			//添加方法：获取母联list中y值最小的设备，并返回
			addConnectLineParamList(x-busAndBusDistance*30+breakerWidth/2, y-breakerHeight/2, "vline",busAndBusDistance);//横线,
			addConnectLineParamList(x-busAndBusDistance*30, y, "hline",(bus.getY()-y)/30);//竖线       1:30
			//画母线
			DeviceInfo currBus = addBusParamList(x-busLineLength-busAndBusDistance*30, bus.getY(), currentBus.getName(),
					currentBus.getMRID(), "red", busLineLength);
			//回调setDevMap(参数当前的母线)
			busbarSectionList.remove(currentBus);
//			setDevMap(currBus,currentBus.getPhysicNodeBegin());
			
		}
		bus.setBusConnCount(bus.getBusConnCount()+1);
//		double x = bus.getX();
//		if(bus.)//添加母联数量字段，否则，不能判断左右
//		double newX = x+busLineLength-10;
//		for(int )
	}

	private Map<String,Object> findConnList(DeviceInfo deviceInfo) {
		Map<String,Object> result = new HashMap<String, Object>();
		List<DeviceInfo> list = new ArrayList<DeviceInfo>();
		while(!("busline").equals(deviceInfo.getDevType())){
			list.add(deviceInfo);
			deviceInfo = deviceInfo.getDev();
		}
		result.put("list", list);
		result.put("bus", deviceInfo);
		return result;
	}

	/**
	 * 获取母联顺序
	 * 
	 * @param connId
	 * @return
	 */
	public void findDeviceByConnId2(BusbarSection busbarSection) {
		String connId = busbarSection.getMRID();

		Map<String, List<BusbarSection>> busSort = new HashMap<String, List<BusbarSection>>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<BusbarSection> busbarSectionList = getBusByConnId(connId);

		List<Breaker> breakerList = getBreakerByConnId(connId);

		List<Disconnector> disconnectorList = getDisconnectorByConnId(connId);

		List<Map<String, String>> breakerMapList = joinBreakerData(breakerList,
				disconnectorList);

		List<ACLineDot> lineList = getLineByConnId(connId);

		List<PowerTransformer> powerTransformerList = getTranByConnId(connId);

		if (busbarSectionList != null && busbarSectionList.size() != 0) {// 母线递归添加图形参数信息
		// busSort.put(busbarSection.getMRID(), busbarSectionList.get(0));

		} else if (breakerMapList != null && breakerMapList.size() != 0) {// 开关递归添加图形参数信息\

			for (Map<String, String> idMap : breakerMapList) {// 只有一个，所以可用breakX、breakY的值画线
				String id = idMap.get("id");
				String devId = busbarSection.getMRID();
				if (!id.equals(devId) && devId != null) {// 找到设备，并将<连接线,设备>组合绘画
					if (busbarSectionList == null
							|| busbarSectionList.size() == 0) {

					}
				}
			}
		}
	}

	/**
	 * 获取当前设备的另一个连接点
	 * 
	 * @param breakerMap
	 *            设备
	 * @param connId
	 *            当前设备一侧的连接点
	 * @return
	 */
	public String GetAnotherNodeStr(Map<String, String> breakerMap,
			String connId) {
		String breakerConnId = "";

		String sId = breakerMap.get("physicNodeBegin");
		String eId = breakerMap.get("physicNodeEnd");
		if ("".equals(connId) || connId == null) {
			return breakerConnId;
		}
		if (!sId.equals(connId)) {// 查找开关所连的另一个端点信息
			breakerConnId = sId;
		} else if (!eId.equals(connId)) {
			breakerConnId = eId;
		}
		return breakerConnId;
	}

	/**
	 * 合并母线连接的开关和刀闸数据集合
	 * 
	 * @param connBreakerList
	 * @param connDisconnectorList
	 * @return
	 */
	public List<Map<String, String>> joinBreakerData(
			List<Breaker> connBreakerList,
			List<Disconnector> connDisconnectorList) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Breaker b : connBreakerList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", b.getName());
			map.put("id", b.getMRID());
			map.put("physicNodeBegin", b.getPhysicNodeBegin());
			map.put("physicNodeEnd", b.getPhysicNodeEnd());
			map.put("type", "breaker");
			result.add(map);
		}

		for (Disconnector d : connDisconnectorList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", d.getName());
			map.put("id", d.getMRID());
			map.put("physicNodeBegin", d.getPhysicNodeBegin());
			map.put("physicNodeEnd", d.getPhysicNodeEnd());
			map.put("type", "disconnector");
			result.add(map);
		}
		return result;
	}

	/**
	 * 增加连线图形参数
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public DeviceInfo addConnectLineParamList(double x, double y, String type) {
		DeviceInfo deviceInfo = new DeviceInfo();// 设置开关设备参数信息
		deviceInfo.setX(x);
		deviceInfo.setY(y);
		deviceInfo.setDevType(type);
		deviceInfo.setLen(connLineLength);
		deviceInfo.setThicker(connLineThicker);
		deviceInfo.setId(String.valueOf(UUID.randomUUID()));
		paramList.add(deviceInfo);// 增加开关图形参数信息
		return deviceInfo;
	}
	/**
	 * 增加连线图形参数
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public DeviceInfo addConnectLineParamList(double x, double y, String type,double len) {
		DeviceInfo deviceInfo = new DeviceInfo();// 设置开关设备参数信息
		deviceInfo.setX(x);
		deviceInfo.setY(y);
		deviceInfo.setDevType(type);
		deviceInfo.setLen(len);
		deviceInfo.setThicker(connLineThicker);
		deviceInfo.setId(String.valueOf(UUID.randomUUID()));
		paramList.add(deviceInfo);// 增加开关图形参数信息
		return deviceInfo;
	}

	/**
	 * 增加线路图形参数
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public DeviceInfo addLineParamList(double x, double y, String name,
			String type) {
		DeviceInfo deviceInfo = new DeviceInfo();// 设置开关设备参数信息
		deviceInfo.setX(x);
		deviceInfo.setY(y);
		deviceInfo.setDevType(type);
		deviceInfo.setName(name);
		deviceInfo.setLen(connLineLength);
		deviceInfo.setThicker(connLineThicker);
		paramList.add(deviceInfo);// 增加开关图形参数信息
		return deviceInfo;
	}

	/**
	 * 增加刀闸图形参数
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public DeviceInfo addDisconnectorParamList(double x, double y, String name,
			String id, String type) {
		DeviceInfo deviceInfo = new DeviceInfo();// 设置开关设备参数信息
		deviceInfo.setX(x);
		deviceInfo.setY(y);
		deviceInfo.setDevType("disconnector");
		deviceInfo.setName(name);
		deviceInfo.setId(id);
		deviceInfo.setWidth(disconnectorWidth);
		deviceInfo.setHeight(disconnectorHeight);
		paramList.add(deviceInfo);// 增加开关图形参数信息
		return deviceInfo;
	}

	/**
	 * 增加母线图形参数
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public DeviceInfo addBusParamList(double x, double y, String name,
			String id, String color, double length) {
		DeviceInfo deviceInfo = new DeviceInfo();// 设置开关设备参数信息
		deviceInfo.setX(x);
		deviceInfo.setY(y);
		deviceInfo.setDevType("busline");
		deviceInfo.setName(name);
		deviceInfo.setId(id);
		deviceInfo.setLen(length);
		deviceInfo.setThicker(busThicker);
		deviceInfo.setColor(color);
		paramList.add(deviceInfo);// 增加母线图形参数信息
		return deviceInfo;
	}

	/**
	 * 增加开关图形参数
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public DeviceInfo addBreakerParamList(double x, double y, String name,
			String id, String type) {
		DeviceInfo deviceInfo = new DeviceInfo();// 设置开关设备参数信息
		deviceInfo.setX(x);
		deviceInfo.setY(y);
		deviceInfo.setDevType(type);
		deviceInfo.setId(id);
		deviceInfo.setName(name);
		deviceInfo.setWidth(breakerWidth);
		deviceInfo.setHeight(breakerHeight);
//		deviceInfo.setDev(dev);
		paramList.add(deviceInfo);// 增加开关图形参数信息
		return deviceInfo;
	}

	/**
	 * 增加主变图形参数
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public DeviceInfo addTranParamList(double x, double y, String name,
			String id) {
		DeviceInfo deviceInfo = new DeviceInfo();// 设置开关设备参数信息
		deviceInfo.setX(x);
		deviceInfo.setY(y);
		deviceInfo.setDevType("tran");
		deviceInfo.setName(name);
		deviceInfo.setId(id);
		deviceInfo.setWidth(tranWidth);
		deviceInfo.setHeight(tranHeight);
		paramList.add(deviceInfo);// 增加开关图形参数信息
		return deviceInfo;
	}

	/**
	 * 设置变压器绕组链接的开关刀闸数据集合
	 * 
	 * @return
	 */
	private void setTranSwith() {
		for (PowerTransformer powerTransformer : tranList) {
			Map<String, TransformerWinding> twMap = powerTransformer
					.getTransformerWindingMap();// 获取主变绕组
			for (String twType : twMap.keySet()) {
				TransformerWinding transformerWinding = twMap.get(twType);
				String connectId = transformerWinding.getPhysicNodeBegin();// 获取变压器绕组的连接点信系
				tranBreakerList.addAll(getBreakerByConnId(connectId));
				tranDisconnectorList.addAll(getDisconnectorByConnId(connectId));
			}
		}
	}

	private List<BusbarSection> getStartBusLineList() {
		List<BusbarSection> result = new ArrayList<BusbarSection>();
		int max = 0;
		for (BusbarSection busbarSection : busbarSectionList) {
			int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
			if (max < voltLevel) {
				max = voltLevel;
			}
		}
		List<BusbarSection> busList = new ArrayList<BusbarSection>();
		for (BusbarSection busbarSection : busbarSectionList) {
			int voltLevel = Integer.parseInt(busbarSection.getVoltageLevel());
			if (max == voltLevel) {
				result.add(busbarSection);
			}
		}
		return result;
	}

	/**
	 * 根据连接点获取连接的主变信息
	 * 
	 * @param connId
	 * @return
	 */
	private List<PowerTransformer> getTranByConnId(String connId) {
		List<PowerTransformer> result = new ArrayList<PowerTransformer>();
		for (PowerTransformer powerTransformer : tranList) {
			Map<String, TransformerWinding> twMap = powerTransformer
					.getTransformerWindingMap();// 获取主变绕组
			for (String twType : twMap.keySet()) {
				TransformerWinding transformerWinding = twMap.get(twType);
				String connectId = transformerWinding.getPhysicNodeBegin();// 获取变压器绕组的连接点信系
				if (connectId.equals(connId)) {
					result.add(powerTransformer);
				}
			}
		}
		return result;
	}

	/**
	 * 获取变压器另一侧连接点
	 */
	private String getTranAnotherConnId(PowerTransformer powerTransformer,
			String connId) {
		Map<String, TransformerWinding> twMap = powerTransformer
				.getTransformerWindingMap();
		for (String twType : twMap.keySet()) {
			if ("低".equals(twType)) {
				TransformerWinding transformerWinding = twMap.get(twType);
				String id = transformerWinding.getPhysicNodeBegin();
				return id;
			}
		}
		return null;

	}

	/**
	 * 根据连接点获取连接的开关信息
	 * 
	 * @param connId
	 * @return
	 */
	private List<Breaker> getBreakerByConnId(String connId) {
		List<Breaker> result = new ArrayList<Breaker>();
		for (Breaker breaker : breakerList) {
			if (connId.equals(breaker.getPhysicNodeBegin())
					|| connId.equals(breaker.getPhysicNodeEnd())) {
				result.add(breaker);
			}
		}
		return result;
	}

	/**
	 * 根据连接点获取连接的刀闸信息
	 * 
	 * @param connId
	 * @return
	 */
	private List<Disconnector> getDisconnectorByConnId(String connId) {
		List<Disconnector> result = new ArrayList<Disconnector>();
		for (Disconnector disconnector : disconnectorList) {
			if (connId.equals(disconnector.getPhysicNodeBegin())
					|| connId.equals(disconnector.getPhysicNodeEnd())) {
				result.add(disconnector);
			}
		}
		return result;
	}

	/**
	 * 根据连接点获取连接的母线信息
	 * 
	 * @param connId
	 * @return
	 */
	private List<BusbarSection> getBusByConnId(String connId) {
		List<BusbarSection> result = new ArrayList<BusbarSection>();
		for (BusbarSection busbarSection : busbarSectionList) {
			if (connId.equals(busbarSection.getPhysicNodeBegin())) {
				result.add(busbarSection);
			}
		}
		return result;
	}

	/**
	 * 根据连接点获取连接的线路信息
	 * 
	 * @param connId
	 * @return
	 */
	private List<ACLineDot> getLineByConnId(String connId) {
		List<ACLineDot> result = new ArrayList<ACLineDot>();
		for (ACLineDot acLineDot : lineList) {
			if (connId.equals(acLineDot.getPhysicNodeBegin())) {
				result.add(acLineDot);
			}
		}
		return result;
	}

	public List<DeviceInfo> getParamList() {
		return paramList;
	}

	public void setParamList(List<DeviceInfo> paramList) {
		this.paramList = paramList;
	}

	public List<PowerTransformer> getTranList() {
		return tranList;
	}

	public void setTranList(List<PowerTransformer> tranList) {
		this.tranList = tranList;
	}

	public List<Breaker> getBreakerList() {
		return breakerList;
	}

	public void setBreakerList(List<Breaker> breakerList) {
		this.breakerList = breakerList;
	}

	public List<Disconnector> getDisconnectorList() {
		return disconnectorList;
	}

	public void setDisconnectorList(List<Disconnector> disconnectorList) {
		this.disconnectorList = disconnectorList;
	}

	public List<BusbarSection> getBusbarSectionList() {
		return busbarSectionList;
	}

	public void setBusbarSectionList(List<BusbarSection> busbarSectionList) {
		this.busbarSectionList = busbarSectionList;
	}

	public List<ACLineDot> getLineList() {
		return lineList;
	}

	public void setLineList(List<ACLineDot> lineList) {
		this.lineList = lineList;
	}

	public List<Breaker> getTranBreakerList() {
		return tranBreakerList;
	}

	public void setTranBreakerList(List<Breaker> tranBreakerList) {
		this.tranBreakerList = tranBreakerList;
	}

	public List<Disconnector> getTranDisconnectorList() {
		return tranDisconnectorList;
	}

	public void setTranDisconnectorList(List<Disconnector> tranDisconnectorList) {
		this.tranDisconnectorList = tranDisconnectorList;
	}
}
