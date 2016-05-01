package com.jb.genemap.next.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flowCal.device.model.main.ACLineDot;
import com.flowCal.device.model.main.Breaker;
import com.flowCal.device.model.main.BusbarSection;
import com.flowCal.device.model.main.ControlArea;
import com.flowCal.device.model.main.Disconnector;
import com.flowCal.device.model.main.PowerTransformer;
import com.jb.genemap.next.model.ElementType;
import com.jb.genemap.next.service.utils.ReadFromCime;
import com.jb.genemap.next.view.Main;

/**
 * ����cime�ļ�������
 * @author yjl
 *
 */
public class ParseCime  implements IParse{
	
	private Map<String,Map<String,Object>> devicesMap = null;
	private ControlArea controlArea = null;

	public  ParseCime(String subId){
		ReadFromCime rf = new ReadFromCime();
		String readFileName = Main.cimeFilePath;
		File f = new File(readFileName);
		String deptName = "";
		try {
			BufferedReader bf = new BufferedReader(new FileReader(f));
			String titleStr = bf.readLine();
			deptName = titleStr.substring(titleStr.indexOf("Entity=")+"Entity=".length(),titleStr.indexOf("type")-1);
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			controlArea = rf.getControlArea(readFileName, subId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		devicesMap = controlArea.getDevicesMap();//�豸devsMap key:devType,value:devMap
	}
	
	/**
	 * ��cim�ļ���ȡ������Ϣ
	 * @return
	 */
	public List<PowerTransformer> getTranMap(){
		List<PowerTransformer> result = new ArrayList<PowerTransformer>();
		Map<String,Object> transformerMap = devicesMap.get(ElementType.Transformer_2.toString());
		if(transformerMap != null){
			for (String tf : transformerMap.keySet()) {
				PowerTransformer powerTransformer = (PowerTransformer)transformerMap.get(tf);
				powerTransformer.getTransformerWindingMap().get("��");
				result.add(powerTransformer);
			}
		}
		
		Map<String,Object> transformerMap2 = devicesMap.get(ElementType.Transformer_3.toString());
		if(transformerMap2 != null){
			for (String tf : transformerMap2.keySet()) {
				PowerTransformer powerTransformer = (PowerTransformer)transformerMap2.get(tf);
				result.add(powerTransformer);
			}
		}
		return result;
	}
	
	/**
	 * ��cime�ļ���ȡ������Ϣ
	 * @return
	 */
	public List<Breaker> getBreakerMap(){
		List<Breaker> result = new ArrayList<Breaker>();
		Map<String,Object> devMap = new HashMap<String,Object>();//devMap key:devId,value:devObj
		devMap = devicesMap.get(ElementType.Breaker.toString());
		if(devMap != null){
			for (String devId : devMap.keySet()) {
				Breaker breaker = (Breaker) devMap.get(devId);
				result.add(breaker);
			}
		}
		return result;
	}
	
	/**
	 * ��cime�ļ���ȡ��բ��Ϣ
	 * @return
	 */
	public List<Disconnector> getDisconnectorMap(){
		List<Disconnector> result = new ArrayList<Disconnector>();
		Map<String,Object> devMap = new HashMap<String,Object>();//devMap key:devId,value:devObj
		devMap = devicesMap.get(ElementType.Switch.toString());
		if(devMap != null){
			for (String devId : devMap.keySet()) {
				Disconnector disconnector = (Disconnector) devMap.get(devId);
				result.add(disconnector);
			}
		}
		return result;
	}
	
	/**
	 * ��cime�ļ���ȡĸ����Ϣ
	 * @return
	 */
	public List<BusbarSection> getBusMap(){
		List<BusbarSection> result = new ArrayList<BusbarSection>();
		Map<String,Object> devMap = new HashMap<String,Object>();//devMap key:devId,value:devObj
		devMap = devicesMap.get(ElementType.Bus.toString());
		if(devMap != null){
			for (String devId : devMap.keySet()) {
				BusbarSection busbarSection = (BusbarSection) devMap.get(devId);
				result.add(busbarSection);
			}
		}
		return result;
	}
	
	/**
	 * ��cime�ļ���ȡ��·��Ϣ
	 * @return
	 */
	public List<ACLineDot> getLineMap(){
		return controlArea.getAclDotList();
	}
}
