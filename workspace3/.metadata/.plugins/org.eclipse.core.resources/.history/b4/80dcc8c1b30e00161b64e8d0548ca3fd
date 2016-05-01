package com.jb.genemap.dist.model;

public class DistDeviceInfo {
	private String id;
	private String name;
	private double x;
	private double y;
	private double width=32;
	private double height=32;
	private String orientation;
	private String svgStr;//svg图形字串
	private String type;//设备类型
	
	public String getId() {
		return id;
	}
	public void setId(String id){
		this.id = id;
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
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	//添加名称
	public String addText(){
		String devType = getType();
		double x = getX()+30;
		double y = getY()+40;
		if("DISLINE".equals(devType)){
			x = getX()+10;
		}else if("DISTRAN".equals(devType)){
			x = getX()+35;
			y = y-20;
		}
		String text="<text x=\""+0+"\" y=\""+0+"\" stroke=\"0\" transform=\"matrix(1,0,0,1,"+x+","+y+")\" font-size=\"10.0\" font-family=\"黑体\" fill=\"rgb(0,0,0)\">"+getName()+"</text>";
		return text;
	}
}
