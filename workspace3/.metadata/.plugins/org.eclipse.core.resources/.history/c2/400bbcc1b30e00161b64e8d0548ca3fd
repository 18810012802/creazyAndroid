package com.jb.genemap.dist.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.jb.genemap.dist.model.DistDeviceInfo;
import com.jb.genemap.dist.service.ParseDistDB;

//生成。svg文件的主程序
public class Main {

	public static String filePath="c:/3.svg";
	public static String cimeFilePath="F:\\main.cime";
	public static double canvasWidth=20000.0;
	public static double canvasHeight=4000.0;
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		StringBuilder svgStr = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?><svg width='"+canvasWidth+"' height='"+canvasHeight+"'>");
		ParseDistDB p = new ParseDistDB("407707999");//使用数据库生成svg图形
//		ParseDistCime p= new ParseDistCime("上暮S220线");//使用cime文件生成svg图形
		List<DistDeviceInfo> list = p.getSvgList();
		for(DistDeviceInfo distDeviceInfo : list){
			String str = distDeviceInfo.getSvgStr();
			svgStr.append(str).append(distDeviceInfo.addText());
		}
		svgStr.append("</svg>");
		try {
			File f = new File(filePath);
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
