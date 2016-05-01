package com.jb.genemap.next.service.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
import com.flowCal.device.model.main.Substation;
import com.flowCal.device.model.main.TransformerWinding;
import com.jb.genemap.next.model.CimeType;
import com.jb.genemap.next.model.ElementType;
import com.jb.genemap.next.service.utils.Tools;

public class ReadFromCime {
	ControlArea c = new ControlArea();
	String[] dataLine;
	
	public ControlArea getControlArea(String readFileName,String subId) throws Exception{
		ReadCIME(readFileName,subId);
		Map<String,Map<String,Object>> devicesMap = c.getDevicesMap();
		Map<String,Object> transformerWindingMap = devicesMap.get(ElementType.TRANSFORMER_1.toString());
		Map<String,Map<String,TransformerWinding>> twMap = new HashMap<String,Map<String,TransformerWinding>>();
		if(transformerWindingMap != null){
			for (String twId : transformerWindingMap.keySet()) {
				TransformerWinding transformerWinding = (TransformerWinding)transformerWindingMap.get(twId);
				String tranId = transformerWinding.getPowerTransformer();
				Map<String,TransformerWinding> tranWMap = new HashMap<String,TransformerWinding>();;
				if(!twMap.containsKey(tranId)){
					tranWMap = new HashMap<String, TransformerWinding>();
				}else{
					tranWMap = twMap.get(tranId);
					
				}
				tranWMap.put(transformerWinding.getType(), transformerWinding);
				twMap.put(tranId, tranWMap);
			}
		}

		Map<String,Object> transformerMap = devicesMap.get(ElementType.Transformer_2.toString());
		if(transformerMap != null){
			for (String tf : transformerMap.keySet()) {
				PowerTransformer powerTransformer = (PowerTransformer)transformerMap.get(tf);
				powerTransformer.getTransformerWindingMap().putAll(twMap.get(powerTransformer.getMRID()));
				transformerMap.put(powerTransformer.getMRID(), powerTransformer);
			}
		}
		c.getDevicesMap().put(ElementType.Transformer_2, transformerMap);
		
		Map<String,Object> transformerMap2 = devicesMap.get(ElementType.Transformer_3.toString());
		if(transformerMap2 != null){
			for (String tf : transformerMap2.keySet()) {
				PowerTransformer powerTransformer = (PowerTransformer)transformerMap2.get(tf);
				powerTransformer.getTransformerWindingMap().putAll(twMap.get(powerTransformer.getMRID()));
				transformerMap2.put(powerTransformer.getMRID(), powerTransformer);
			}
		}
		c.getDevicesMap().put(ElementType.Transformer_3, transformerMap2);
		return c;
	}
	
	/**��ȡCIME�ļ�����
	 * @param file
	 * @param company
	 */
	private void ReadCIME(String file,String subId) throws Exception{
		String strLine = "";
		//try {
			File fileEncode = new File(file);
			String encode = "";
			//try{
				encode = Tools.getFileEncode(fileEncode);
			/*}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}*/
			FileInputStream fr = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fr,encode);
			BufferedReader br = new BufferedReader(isr);
			int currentType = 0;
			strLine = br.readLine();
				while (strLine != null) {
					if (strLine.equals("<ControlArea::SQ>")) {
						currentType = CimeType.ControlArea;
					}else if(strLine.equals("<BaseVoltage::SQ>")){
						currentType = CimeType.BaseVoltage;
					}else if(strLine.equals("<Substation::SQ>")){
						currentType = CimeType.Substation;
					}else if(strLine.equals("<VoltageLevel::SQ>")){
						currentType = CimeType.VoltageLevel;
					}else if(strLine.equals("<Bay::SQ>")){
						currentType = CimeType.Bay;
					}else if(strLine.equals("<Breaker::SQ>")){
						currentType = CimeType.Breaker;
					}else if(strLine.equals("<Disconnector::SQ>")){
						currentType = CimeType.Disconnector;
					}else if(strLine.equals("<GroundDisconnector::SQ>")){
						currentType = CimeType.GroundDisconnector;
					}else if(strLine.equals("<BusbarSection::SQ>")){
						currentType = CimeType.BusbarSection;
					}else if(strLine.equals("<ACLine::SQ>")){
						currentType = CimeType.ACLine;
					}else if(strLine.equals("<ACLineSegment::SQ>")){
						currentType = CimeType.ACLineSegment;
					}else if(strLine.equals("<ACLineDot::SQ>")){
						currentType = CimeType.ACLineDot;
					}else if(strLine.equals("<TapChangerType::SQ>")){
						currentType = CimeType.TapChangerType;
					}else if(strLine.equals("<PowerTransformer::SQ>")){
						currentType = CimeType.PowerTransformer;
					}else if(strLine.equals("<TransformerWinding::SQ>")){
						currentType = CimeType.TransformerWinding;
					}else if(strLine.equals("<SynchronousMachine::SQ>")){
						currentType = CimeType.SynchronousMachine;
					}else if(strLine.equals("<Load::SQ>")){
						currentType = CimeType.Load;
					}else if(strLine.equals("<ShuntCompensator::SQ>")){
						currentType = CimeType.ShuntCompensator;
					}else if(strLine.equals("<SeriesCompensator::SQ>")){
						currentType = CimeType.SeriesCompensator;
					}else if(strLine.equals("<RelaySignal::SQ>")){
						currentType = CimeType.RelaySignal;
					}else if(strLine.equals("<EndDevice::SQ>")){
						currentType = CimeType.EndDevice;
					}else if(strLine.equals("<ComputeValue::SQ>")){
						currentType = CimeType.ComputeValue;
					}else if(strLine.equals("<State::SQ>")){
						currentType = CimeType.State;
					}else if(strLine.equals("<Signal::SQ>")){
						currentType = CimeType.Signal;
					}else if(strLine.equals("<Measure::SQ>")){
						currentType = CimeType.Measure;
					}else if(strLine.equals("<Analog::SQ>")){
						currentType = CimeType.Analog;
					}else if(strLine.equals("<Discrete::SQ>")){
						currentType = CimeType.Discrete;
					}
				
					switch (currentType) {
						case CimeType.Substation:
							subId = getSubSId(strLine,subId);
							break;
						case CimeType.Breaker:
							setBreaker(strLine,subId);
							break;
						case CimeType.Disconnector:
							setDisconnector(strLine,subId);
							break;
						case CimeType.BusbarSection:
							setBusbarSection(strLine,subId);
							break;
						case CimeType.ACLineDot:
							setACLineDot(strLine,subId);
							break;
						case CimeType.PowerTransformer:
							setPowerTransformer(strLine,subId);
							break;
						case CimeType.TransformerWinding:
							setPowerTransformerWinding(strLine,subId);
							break;
						
						
					}
					strLine = br.readLine();
			}
		/*}catch (Exception ex) {
			ex.printStackTrace();
		}*/
	}
	
	/**��ȡCIME�ļ�����
	 * @param file
	 * @param company
	 */
	public List<Substation> ReadCIMEForSubs(String file,String subId) throws Exception{
		List<Substation> result =  new ArrayList<Substation>();
		String strLine = "";
		//try {
			File fileEncode = new File(file);
			String encode = "";
			//try{
				encode = Tools.getFileEncode(fileEncode);
			/*}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}*/
			FileInputStream fr = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fr,encode);
			BufferedReader br = new BufferedReader(isr);
			int currentType = 0;
			strLine = br.readLine();
				while (strLine != null) {
					if (strLine.equals("<ControlArea::SQ>")) {
						currentType = CimeType.ControlArea;
					}else if(strLine.equals("<BaseVoltage::SQ>")){
						currentType = CimeType.BaseVoltage;
					}else if(strLine.equals("<Substation::SQ>")){
						currentType = CimeType.Substation;
					}else if(strLine.equals("<VoltageLevel::SQ>")){
						currentType = CimeType.VoltageLevel;
					}else if(strLine.equals("<Bay::SQ>")){
						currentType = CimeType.Bay;
					}else if(strLine.equals("<Breaker::SQ>")){
						currentType = CimeType.Breaker;
					}else if(strLine.equals("<Disconnector::SQ>")){
						currentType = CimeType.Disconnector;
					}else if(strLine.equals("<GroundDisconnector::SQ>")){
						currentType = CimeType.GroundDisconnector;
					}else if(strLine.equals("<BusbarSection::SQ>")){
						currentType = CimeType.BusbarSection;
					}else if(strLine.equals("<ACLine::SQ>")){
						currentType = CimeType.ACLine;
					}else if(strLine.equals("<ACLineSegment::SQ>")){
						currentType = CimeType.ACLineSegment;
					}else if(strLine.equals("<ACLineDot::SQ>")){
						currentType = CimeType.ACLineDot;
					}else if(strLine.equals("<TapChangerType::SQ>")){
						currentType = CimeType.TapChangerType;
					}else if(strLine.equals("<PowerTransformer::SQ>")){
						currentType = CimeType.PowerTransformer;
					}else if(strLine.equals("<TransformerWinding::SQ>")){
						currentType = CimeType.TransformerWinding;
					}else if(strLine.equals("<SynchronousMachine::SQ>")){
						currentType = CimeType.SynchronousMachine;
					}else if(strLine.equals("<Load::SQ>")){
						currentType = CimeType.Load;
					}else if(strLine.equals("<ShuntCompensator::SQ>")){
						currentType = CimeType.ShuntCompensator;
					}else if(strLine.equals("<SeriesCompensator::SQ>")){
						currentType = CimeType.SeriesCompensator;
					}else if(strLine.equals("<RelaySignal::SQ>")){
						currentType = CimeType.RelaySignal;
					}else if(strLine.equals("<EndDevice::SQ>")){
						currentType = CimeType.EndDevice;
					}else if(strLine.equals("<ComputeValue::SQ>")){
						currentType = CimeType.ComputeValue;
					}else if(strLine.equals("<State::SQ>")){
						currentType = CimeType.State;
					}else if(strLine.equals("<Signal::SQ>")){
						currentType = CimeType.Signal;
					}else if(strLine.equals("<Measure::SQ>")){
						currentType = CimeType.Measure;
					}else if(strLine.equals("<Analog::SQ>")){
						currentType = CimeType.Analog;
					}else if(strLine.equals("<Discrete::SQ>")){
						currentType = CimeType.Discrete;
					}
				
					switch (currentType) {
						case CimeType.Substation:
							getSubsMsg(strLine,subId,result);
							break;
					}
					strLine = br.readLine();
			}
		return result;
	}
	
	private String[] splitString(String strdata){
		strdata = dropBlank(strdata.replaceFirst("#", ""));
		strdata = strdata.replaceAll("\\s{1,}", ",");
		String[] str = strdata.split(",");
		return str;
	}
	
	private String dropBlank(String str){
		str = str.replaceAll("^[ ]*", "").replaceAll("[ ]*$", "");
		return str;
	}
	
	private void getSubsMsg(String strdata,String subId,List<Substation> result){
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(subId.contains("վ")||(subId.charAt(0)>19968 && subId.charAt(0)<40623)){
				if(dataLine[3].contains(subId)){
					Substation s = new Substation();
					s.setMRID(dataLine[2]);
					s.setName(dataLine[3]);
					result.add(s);
				}
			}
		}
	}
	
	private String getSubSId(String strdata,String subId){
		String result = subId;
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(subId.contains("վ")||(subId.charAt(0)>19968 && subId.charAt(0)<40623)){
				if(dataLine[3].equals(subId)){
					result = dataLine[2];
				}
			}
		}
		return result;
	}
	
	private void setBreaker(String strdata,String subId){
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(dataLine[8].equals(subId)){
				Map<String, Object> breakerMap = null;
				Breaker b = new Breaker();
				//���	��ʶ	����ԭ��	��׼��·��ȫ��	��·������	�������ӽڵ��	�������ӽڵ��	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	�ڶ�����	״̬	ң������
				b.setMRID(dataLine[2]);
				b.setName(dataLine[3]);
				b.setPathName(dataLine[4]);
				b.setPhysicNodeBegin(dataLine[6]);
				b.setPhysicNodeEnd(dataLine[7]);
				b.setSubStation(dataLine[8]);
				if(!c.getDevicesMap().containsKey(ElementType.Breaker)){
					breakerMap = new HashMap<String, Object>();
				}else{
					breakerMap = (Map<String, Object>) c.getDevicesMap().get(ElementType.Breaker);
				}
				breakerMap.put(dataLine[2], b);
				c.getDevicesMap().put(ElementType.Breaker,breakerMap);
			}
		}
	}
	
	private void setDisconnector(String strdata,String subId){
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(dataLine[7].equals(subId)){
				Disconnector d = new Disconnector();
				Map<String, Object> disMap = null;
				//���	��ʶ	����ԭ��	��׼��·��ȫ��	�������ӽڵ��	�������ӽڵ��	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	״̬	ң������
				d.setMRID(dataLine[2]);
				d.setName(dataLine[3]);
				d.setPathName(dataLine[4]);
				d.setPhysicNodeBegin(dataLine[5]);
				d.setPhysicNodeEnd(dataLine[6]);
				d.setSubStation(dataLine[7]);
				if(!c.getDevicesMap().containsKey(ElementType.Switch)){
					disMap = new HashMap<String, Object>();
				}else{
					disMap = (Map<String, Object>) c.getDevicesMap().get(ElementType.Switch);
				}
				disMap.put(dataLine[2], d);
				c.getDevicesMap().put(ElementType.Switch,disMap);
			}
		}
	}
	
	private void setBusbarSection(String strdata,String subId){
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(dataLine[6].equals(subId)){
				Map<String, Object> busMap = null;
				BusbarSection b = new BusbarSection();
				//���	ԭ��ʶ	����ԭ��	��׼��·��ȫ��	ĸ�߽ڵ��	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	λ����Ϣ	��ѹ����	�ߵ�ѹ������	�������	���������	��ѹ����	��ѹ����
				b.setMRID(dataLine[2]);
				b.setName(dataLine[3]);
				b.setPathName(dataLine[4]);
				String voltLevel = dataLine[7];
				if("200000001".equals(voltLevel)){
					voltLevel = "500";
				}else if("200000002".equals(voltLevel)){
					voltLevel = "220";
				}else if("200000003".equals(voltLevel)){
					voltLevel = "110";
				}else if("200000004".equals(voltLevel)){
					voltLevel = "35";
				}else if("200000005".equals(voltLevel)){
					voltLevel = "10";
				}
				b.setVoltageLevel(voltLevel);
				b.setPhysicNodeBegin(dataLine[5]);
				b.setSubStation(dataLine[6]);
				if(!c.getDevicesMap().containsKey(ElementType.Bus)){
					busMap = new HashMap<String, Object>();
				}else{
					busMap = (Map<String, Object>) c.getDevicesMap().get(ElementType.Bus);
				}
				busMap.put(dataLine[2], b);
				c.getDevicesMap().put(ElementType.Bus, busMap);
			}
		}
	}
	
	private void setACLineDot(String strdata,String subId){
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(dataLine[6].equals(subId)){
				ACLineDot a = new ACLineDot();
				//���	��ʶ	����ԭ��	��׼��·��ȫ��	�����߶α�ʶ	��վ��ʶ	�������ӽڵ�	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	�й�����	�й�������	�޹�����	�޹�������
				a.setMRID(dataLine[2]);
				a.setName(dataLine[3]);
				a.setPathName(dataLine[4]);
				a.setAclineSegment(dataLine[5]);
				a.setSubStation(dataLine[6]);
				a.setPhysicNodeBegin(dataLine[7]);
				c.getAclDotList().add(a);
			}
		}
	}
	
	private void setPowerTransformer(String strdata,String subId){
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(dataLine[6].equals(subId)){
				Map<String, Object> tranMap2 = null;//2���
				Map<String, Object> tranMap3 = null;//3���
				PowerTransformer powerTransformer = new PowerTransformer();
				//���	��ʶ	����ԭ��	��׼��·��ȫ��	����	��վ��ʶ	�������	���ص����ٷֱ�	
				powerTransformer.setMRID(dataLine[2]);
				powerTransformer.setName(dataLine[3]);
				powerTransformer.setPathName(dataLine[3]);
				powerTransformer.setType(dataLine[5]);
				powerTransformer.setSubStation(dataLine[6]);
				if(!c.getDevicesMap().containsKey(ElementType.Transformer_2)){
					tranMap2 = new HashMap<String, Object>();
				}else{
					tranMap2 = (Map<String, Object>) c.getDevicesMap().get(ElementType.Transformer_2);
				}
				
				if(!c.getDevicesMap().containsKey(ElementType.Transformer_3)){
					tranMap3 = new HashMap<String, Object>();
				}else{
					tranMap3 = (Map<String, Object>) c.getDevicesMap().get(ElementType.Transformer_3);
				}
				if("���Ʊ�".equals(dataLine[5])){//�����
					tranMap2.put(dataLine[2], powerTransformer);
				}else if ("���Ʊ�".equals(dataLine[5])){//�����
					tranMap3.put(dataLine[2], powerTransformer);
				}
				c.getDevicesMap().put(ElementType.Transformer_2, tranMap2);
				c.getDevicesMap().put(ElementType.Transformer_3, tranMap3);
			}
		}
	}
	
	private void setPowerTransformerWinding(String strdata,String subId){
		if(strdata.contains("#"))
		{
			dataLine = splitString(strdata);
			if(dataLine[6].equals(subId)){
				
				Map<String, Object> tranWindMap = null;//��ѹ������
				TransformerWinding tw = new TransformerWinding();
				//���	��ʶ	����ԭ��	��׼��·��ȫ��	��������	��վ��ʶ	��ѹ����ʶ	�������ӽڵ�	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	�ֽ�ͷ���ͱ�ʶ	�����	���ѹ	��·���	��·��ѹ�ٷֱ�	����	�翹	�������	����翹	�й�����	�й�������	�޹�����	�޹�������	��λ����	��λ������
				tw.setMRID(dataLine[2]);
				tw.setName(dataLine[3]);
				tw.setPathName(dataLine[4]);
				tw.setType(dataLine[5]);
				tw.setSubStation(dataLine[6]);
				tw.setPowerTransformer(dataLine[7]);
				tw.setPhysicNodeBegin(dataLine[8]);
				if(!c.getDevicesMap().containsKey(ElementType.TRANSFORMER_1)){
					tranWindMap = new HashMap<String, Object>();
				}else{
					tranWindMap = (Map<String, Object>) c.getDevicesMap().get(ElementType.TRANSFORMER_1);
				}
				tranWindMap.put(dataLine[2], tw);
				c.getDevicesMap().put(ElementType.TRANSFORMER_1, tranWindMap);
			}
		}
	}
}
