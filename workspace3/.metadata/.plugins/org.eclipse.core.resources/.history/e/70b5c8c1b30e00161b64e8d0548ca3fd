package com.jb.genemap.dist.model;

public class DistLine extends DistDeviceInfo{
	
	public DistLine(){
		setSvgStr(getSvgStr());
		setType("DISLINE");
	}
	public String getSvgStr(){
		String orientation = getOrientation();
		String str = "";
		if("H".equals(orientation)){
			str = "<path fill='#027871' d='M31,15H1c-0.552,0-1,0.448-1,1s0.448,1,1,1h30c0.553,0,1-0.448,1-1S31.553,15,31,15z'" +
					" transform='matrix(4,0,0,1,"+getX()+","+getY()+")' xmlns='http://www.w3.org/2000/svg' />";
		}else{
			str = "<path fill='#027871' d='M16,0c-0.55,0-1,0.45-1,1v30c0,0.55,0.45,1,1,1s1-0.45,1-1V1C17,0.45,16.55,0,16,0z'" +
					" transform='matrix(1,0,0,4,"+getX()+","+getY()+")' xmlns='http://www.w3.org/2000/svg' />";
		}
		return str;
	}
	
}
