package com.jb.genemap.next.model.deviceTypes;

import com.jb.genemap.next.model.DeviceType;
import com.jb.genemap.next.model.OrientationType;
import com.jb.genemap.next.model.deviceTypes.DeviceInfo;
import com.jb.genemap.next.service.DeviceInfoList;

/**
 * 设备svg图形参数信息
 * @author yjl
 *
 */
public abstract class DeviceInfo {
	private String sortNo;
	private double standardDistance=32;
	public double getStandardDistance() {
		return standardDistance;
	}

	public void setStandardDistance(double standardDistance) {
		this.standardDistance = standardDistance;
	}
	private String belongTranConnId;
	public String getBelongTranConnId() {
		return belongTranConnId;
	}

	public void setBelongTranConnId(String belongTranConnId) {
		this.belongTranConnId = belongTranConnId;
	}

	public String getSortNo() {
		return sortNo;
	}
	private String belongTranNo;
	public String getBelongTranNo() {
		return belongTranNo;
	}

	public void setBelongTranNo(String belongTranNo) {
		this.belongTranNo = belongTranNo;
	}

	public DeviceInfo() {
		super();
	}

	public void setSortNo(String sortNo) {
		this.sortNo = sortNo;
	}

	private String belongBreakerNo;//来源开关

	public String getBelongBreakerNo() {
		return belongBreakerNo;
	}
	public void setBelongBreakerNo(String belongBreakerNo) {
		this.belongBreakerNo = belongBreakerNo;
	}
	public String getBelongBusNo() {
		return belongBusNo;
	}
	public void setBelongBusNo(String belongBusNo) {
		this.belongBusNo = belongBusNo;
	}
	private String belongBusNo;//来源母线
	private String name;//设备名称
	private double x=0;//横坐标值
	private double y=0;//纵坐标值
	private String type;//设备类型
	private String id ;//设备id
	private String color;//svg图形颜色
	private double width=32;//设备宽度
	private double height=32;//设备高度
	private DeviceInfo parentDeviceInfo;//存储父节点
	private String svgStr;//svg图形字串
	private String orientation;//所画方向
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public String getSvgStr() {
		return svgStr;
	}
	public void setSvgStr(String svgStr) {
		this.svgStr = svgStr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public DeviceInfo getParentDeviceInfo() {
		return parentDeviceInfo;
	}
	public void setParentDeviceInfo(DeviceInfo parentDeviceInfo) {
		this.parentDeviceInfo = parentDeviceInfo;
	}
	
	private boolean assistBottomLine;
	public boolean isAssistBottomLine() {
		return assistBottomLine;
	}
	public void setAssistBottomLine(boolean assistBottomLine) {
		this.assistBottomLine = assistBottomLine;
	}                                   
	
	
	
	/**
	 * 连接竖线
	 * @return
	 */
	public DeviceInfo withVLineStr(DeviceInfo deviceInfo,DeviceInfo lastDeviceInfo){
		String orientation=deviceInfo.getOrientation();
		if(orientation!=null){
			if(orientation.equals(OrientationType.NORTH)){
				deviceInfo.setY(deviceInfo.getY()-deviceInfo.getHeight());
				if(!lastDeviceInfo.getType().equals(DeviceType.BUSLINE)){
					deviceInfo.setY(deviceInfo.getY()-deviceInfo.getHeight()/2);
					
				}
				//deviceInfo.setSvgStr(deviceInfo.svgStr()+strBottom);
			}else{
				
				deviceInfo.setY(deviceInfo.getY()+deviceInfo.getHeight());
				if(!lastDeviceInfo.getType().equals(DeviceType.BUSLINE)){
					deviceInfo.setY(deviceInfo.getY()+deviceInfo.getHeight()/2);
					
				}
			}
			
		}
		double x = deviceInfo.getX();
		
		double yTop = deviceInfo.getY()-deviceInfo.getHeight()/2;
		
		double yBottom = deviceInfo.getY()+deviceInfo.getHeight();
		
		/*String len = String.valueOf(deviceInfo.getWidth());
		String name = deviceInfo.getName();*/
//		String color = dev.getColor();//如果一种设备的颜色不变可以直接写死，不用get获取
		String strTop = "<path fill=\"#027871\" d=\"M16,0c-0.55,0-1,0.45-1,1v30c0,0.55,0.45,1,1,1s1-0.45,1-1V1C17,0.45,16.55,0,16,0z\"" +
				" transform=\"matrix(1,0,0,0.5,"+x+","+yTop+")\" xmlns=\"http://www.w3.org/2000/svg\" />\n";
		String strBottom = "<path fill=\"#027871\" d=\"M16,0c-0.55,0-1,0.45-1,1v30c0,0.55,0.45,1,1,1s1-0.45,1-1V1C17,0.45,16.55,0,16,0z\"" +
				" transform=\"matrix(1,0,0,0.5,"+x+","+yBottom+")\" xmlns=\"http://www.w3.org/2000/svg\" />\n";
			
		String assistLineStr="";
		if(assistBottomLine){
			double bottomY=0;
			if(this.getOrientation().equals(OrientationType.NORTH)){
				bottomY=getY()+getHeight();
			}else if(this.getOrientation().equals(OrientationType.SOUTH)){
				bottomY=getY()-getHeight();
			}
			assistLineStr="<path fill='#027871' d='M31.16,15H0.839C0.375,15,0,15.536,0,16s0.376,1,0.84,1H31.16c0.463,0,0.84-0.536,0.84-1S31.623,15,31.16,15&#xD;"+
	                              "z' transform='matrix("+(DeviceInfoList.breakerToBreakerDistance/50)*getWidth()/getHeight()+",0,0,1,"+((getX()-getWidth()*DeviceInfoList.breakerToBreakerDistance/50)+getWidth()/2)+","+bottomY+")'/>";
		}
		if(orientation!=null){
			if(orientation.equals(OrientationType.NORTH)){
				deviceInfo.setSvgStr(deviceInfo.svgStr()+strBottom+assistLineStr);
			}else{
				deviceInfo.setSvgStr(strTop+deviceInfo.svgStr()+assistLineStr);
			}
			
		}else{
			deviceInfo.setSvgStr(strTop+deviceInfo.svgStr()+strBottom+assistLineStr);
		}
	
		return deviceInfo;
	}
	/**
	 * 连接横线
	 */
	public DeviceInfo withHLineStr(DeviceInfo deviceInfo){
		String orientation=deviceInfo.getOrientation();
		if(orientation!=null){
			if(orientation.equals(OrientationType.EAST)){
				deviceInfo.setX(deviceInfo.getX()+deviceInfo.getWidth()/2);
				//deviceInfo.setSvgStr(deviceInfo.svgStr()+strBottom);
			}else{
				deviceInfo.setX(deviceInfo.getX()-deviceInfo.getWidth()/2);
			}
			
		}
		double xLeft = deviceInfo.getX()-deviceInfo.getWidth()/2;
		double xRight = deviceInfo.getX()+deviceInfo.getWidth();
		if(deviceInfo.getType().equals(DeviceType.BUSLINE)){
			xLeft = deviceInfo.getX()-16;
			xRight = deviceInfo.getX()+deviceInfo.getWidth();
		}
		double y = deviceInfo.getY();
		String len = String.valueOf(deviceInfo.getWidth());
		String strLeft = "<path fill='#027871' d='M31.16,15H0.839C0.375,15,0,15.536,0,16s0.376,1,0.84,1H31.16c0.463,0,0.84-0.536,0.84-1S31.623,15,31.16,15&#xD;\n" +
				"z' transform='matrix(0.5,0,0,1,"+xLeft+","+y+")' xmlns='http://www.w3.org/2000/svg' />";
		String strRight = "<path fill='#027871' d='M31.16,15H0.839C0.375,15,0,15.536,0,16s0.376,1,0.84,1H31.16c0.463,0,0.84-0.536,0.84-1S31.623,15,31.16,15&#xD;\n" +
				"z' transform='matrix(0.5,0,0,1,"+xRight+","+y+")' xmlns='http://www.w3.org/2000/svg' />";
		deviceInfo.setSvgStr(strLeft+deviceInfo.svgStr()+strRight);
		if(orientation!=null){
			if(orientation.equals(OrientationType.EAST)){
				deviceInfo.setSvgStr(deviceInfo.svgStr()+strRight);
			}else{
				deviceInfo.setSvgStr(strLeft+deviceInfo.svgStr());
			}
			
		}else{
			deviceInfo.setSvgStr(strLeft+deviceInfo.svgStr()+strRight);
		}
		return deviceInfo;
	}
	
	//设备svg描述
	public abstract String svgStr();
	//添加名称
	public String addText(){
		String names=getName()!=null&&!"null".equals(getName())?getName().replace(getName().substring(0,getName().indexOf("站")+1),"").replace("开关", "")/*.replace("闸刀", "").replace("刀闸", "").replace("_", "").replace("/", "")*/:"";
		String text="<text x=\""+0+"\" y=\""+0+"\" stroke=\"0\" transform=\"matrix(1,0,0,1,"+(getX()+getWidth()/2)+","+(getY()+getHeight())+")\" font-size=\"10.0\" font-family=\"黑体\" fill=\"rgb(0,0,0)\">"+names+"</text>";
		return text;
	}
			
	
}
