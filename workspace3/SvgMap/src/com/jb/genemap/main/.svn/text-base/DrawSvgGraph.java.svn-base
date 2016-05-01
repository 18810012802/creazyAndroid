package com.jb.genemap.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

//生成。svg文件的主程序
public class DrawSvgGraph {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GetParamMap getParamMap = new GetParamMap();
		ConcatDevSvgStr concatDevSvgStr = new ConcatDevSvgStr();
		getParamMap.setDeviceParam();
		StringBuilder svgStr = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?><svg width='1000.0' height='500.0'>");
		List<DeviceInfo> paramList = getParamMap.getParamList();
		for(DeviceInfo deviceInfo : paramList){
			String devType = deviceInfo.getDevType();
			String str = "";
			if("busline".equals(devType)){
				str = concatDevSvgStr.getBusStr(deviceInfo);
			}else if("hline".equals(devType)){
				str = concatDevSvgStr.getHLineStr(deviceInfo);
			}else if("vline".equals(devType)){
				str = concatDevSvgStr.getVLineStr(deviceInfo);
			}else if("breaker".equals(devType)){
				str = concatDevSvgStr.getBreakerStr(deviceInfo);
			}else if("disconnector".equals(devType)){
				str = concatDevSvgStr.getDisconnector(deviceInfo);
			}else if("tran".equals(devType)){
				str = concatDevSvgStr.getTranStr(deviceInfo);
			}
			svgStr.append(str);
		}
		svgStr.append("</svg>");
		try {
			File f = new File("c:/1.svg");
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(svgStr.toString());
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
