package com.jb.genemap.next.service.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flowCal.device.model.main.ACLineDot;
import com.flowCal.device.model.main.Breaker;
import com.flowCal.device.model.main.BusbarSection;
import com.flowCal.device.model.main.Disconnector;
import com.flowCal.device.model.main.PowerTransformer;
import com.flowCal.device.model.main.Substation;
import com.flowCal.device.model.main.TransformerWinding;
import com.jb.genemap.next.service.utils.JdbcDao;

/**
 * 从数据库获取的数据转为cime文件
 * 
 * @author yjl
 * 
 */
public class WriteToCime {
	List<Substation> SubstationList = new ArrayList<Substation>();
	List<PowerTransformer> PowerTransformerList = new ArrayList<PowerTransformer>();
	List<TransformerWinding> TransformerWindingList = new ArrayList<TransformerWinding>();
	List<Breaker> BreakerList = new ArrayList<Breaker>();
	List<Disconnector> DisconnectorList = new ArrayList<Disconnector>();
	List<BusbarSection> BusbarSectionlist = new ArrayList<BusbarSection>();
	List<ACLineDot> ACLineDotList = new ArrayList<ACLineDot>();

	public WriteToCime(String subsId) {
		this.subsId = subsId;
		setSubstationList();
		setTranList();
		setBreakerList();
		setDisconnectorList();
		setBusList();
		setLineList();
	}

	private String subsId;
	private JdbcDao jdbcDao = new JdbcDao();

	// private String orgId = "85df8d06-8054-4d94-a8d9-9c24df701d29";
	// private String subsId = "a4cef88c-004a-48c3-b4a7-cd865e512381";

	public void mainSubsData2CIME(String fileName) {

		File file = new File(fileName);
		FileWriter writer = null;
		int index = 1;// 序号
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				// System.out.println(fileName + "文本文件创建成功");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(fileName);
			// 题目<!Entity=泉州 type=电网模型 time='2014-03-13T02:00:12'!>
			String txt = "<!Entity=SQ type=电网模型    time='" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss") + "'!>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ControlArea::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Parent	p	q	area_type";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	本区域名	父区域标识	总有功	总无功	区域类型 ";
			writer.write(txt + "\r\n");
			txt = "</ControlArea::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<BaseVoltage::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	nomkV";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	基准电压名	基准电压";
			writer.write(txt + "\r\n");
			txt = "</BaseVoltage::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Substation::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	ControlArea	p	q	x	y	i_flag	mGdis_flag	mUnXf_flag";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径厂站全名	厂站类型	所属区域标识	总有功	总无功	厂站经度	厂站纬度	电流量测标识	地刀量测标识	机组变压器量测标识";
			writer.write(txt + "\r\n");
			for (Substation s : SubstationList) {
				txt = "#	" + (index++) + "    "
						+ s.getMRID() + "    "
						+ s.getName() + "    "
						+ s.getName() + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null";
				writer.write(txt + "\r\n");
			}
			txt = "</Substation::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<VoltageLevel::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	highkV	lowkV	Substation	BaseVoltage	type";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径电压等级全名	电压上限	电压下限	所属厂站标识	基准电压标识	结线类型";
			writer.write(txt + "\r\n");
			txt = "</VoltageLevel::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Bay::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	VoltageLevel	type";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	所属厂站标识	所属电压等级标识	结线类型";
			writer.write(txt + "\r\n");
			txt = "</Bay::SQ>";
			writer.write(txt + "\r\n");

			/**
			 * 开关<Breaker::永和庄站></Breaker::永和庄站>
			 */
			index = 1;
			// txt = "//地区";
			// writer.write(txt + "\r\n");
			txt = "<Breaker::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	I_node	J_node	Substation	BaseVoltage	VoltageLevel	mvarating	status	pnt_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	断路器类型	物理连接节点号	物理连接节点号	所属厂站标识	基准电压标识	所属电压等级标识	遮断容量	状态	遥信质量";
			writer.write(txt + "\r\n");
			for (Breaker b : BreakerList) {
				txt = "#	" + (index++) + "    "
						+ b.getMRID() + "    "
						+ b.getName() + "    "
						+ b.getName() + "    "
						+ "null" + "    "
						+ b.getPhysicNodeBegin() + "    "
						+ b.getPhysicNodeEnd() + "    "
						+ subsId + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null";
				writer.write(txt + "\r\n");
			}
			txt = "</Breaker::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			// txt = "//地区";
			// writer.write(txt + "\r\n");
			txt = "<Disconnector::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	I_node	J_node	Substation	BaseVoltage	VoltageLevel	status	pnt_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	物理连接节点号	物理连接节点号	所属厂站标识	基准电压标识	所属电压等级标识	状态	遥信质量";
			writer.write(txt + "\r\n");
			for (Disconnector  d: DisconnectorList) {
				txt = "#	" + (index++) + "    "
						+ d.getMRID() + "    "
						+ d.getName() + "    "
						+ d.getName() + "    "
						+ d.getPhysicNodeBegin() + "    "
						+ d.getPhysicNodeEnd() + "    "
						+ subsId + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null";;
				writer.write(txt + "\r\n");
			}
			txt = "</Disconnector::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<GroundDisconnector::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	I_node	Substation	BaseVoltage	VoltageLevel	status	pnt_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	地刀类型	物理连接节点号	所属厂站标识	基准电压标识	所属电压等级标识	状态	遥信质量";
			writer.write(txt + "\r\n");
			txt = "</GroundDisconnector::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<BusbarSection::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	I_node	Substation	BaseVoltage	VoltageLevel	Location	V	v_qual	A	ang_qual	maxV	minV";
			writer.write(txt + "\r\n");
			txt = "//序号	原标识	中文原名	标准带路径全名	母线节点号	所属厂站标识	基准电压标识	所属电压等级标识	位置信息	电压量测	线电压质量码	相角量测	相角质量码	电压上限	电压下限";
			writer.write(txt + "\r\n");
			for (BusbarSection  b: BusbarSectionlist) {
				txt = "#	" + (index++) + "    "
						+ b.getMRID() + "    "
						+ b.getName() + "    "
						+ b.getName() + "    "
						+ b.getPhysicNodeBegin() + "    "
						+ subsId + "    "
						+ b.getVoltageLevel() + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null";
				writer.write(txt + "\r\n");
			}
			txt = "</BusbarSection::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ACLine::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	aclnNum";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	包含交流线段数";
			writer.write(txt + "\r\n");
			txt = "</ACLine::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ACLineSegment::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	StartSt	EndSt	ratedMW	ratedCurrent	BaseVoltage	r	x	bch	r0	x0	b0ch	ACLine";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	首端厂站标识	末端厂站标识	功率限值	允许载流量	基准电压标识	正序电阻	正序电抗	正序电纳	零序电阻	零序电抗	零序电纳	所属线路标识";
			writer.write(txt + "\r\n");
			txt = "</ACLineSegment::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			// txt = "//地区";
			// writer.write(txt + "\r\n");
			txt = "<ACLineDot::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	ACLineSegment	Substation	I_node	BaseVoltage	VoltageLevel	P	p_qual	Q	q_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	交流线段标识	厂站标识	物理连接节点	基准电压标识	电压等级标识	有功量测	有功质量码	无功量测	无功质量码";
			writer.write(txt + "\r\n");
			for (ACLineDot  a: ACLineDotList) {
				txt = "#	" + (index++) + "    "
						+ a.getMRID() + "    "
						+ a.getName() + "    "
						+ a.getName() + "    "
						+ a.getAclineSegment() + "    "
						+ subsId + "    "
						+ a.getPhysicNodeBegin() + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null" + "    "
						+ "null";
				writer.write(txt + "\r\n");
			}
			txt = "</ACLineDot::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<TapChangerType::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	neutralStep	neutralKV	highStep	lowStep	stepVolIncre	D";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	分接头类型名	额定档位	中点电压	最大档位	最小档位	步长	档位量测";
			writer.write(txt + "\r\n");
			txt = "</TapChangerType::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<PowerTransformer::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	Substation	NoLoadLoss	ExcitingCurrent";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	类型	厂站标识	空载损耗	空载电流百分比";
			writer.write(txt + "\r\n");
			for (PowerTransformer  p: PowerTransformerList) {
				txt = "#	" + (index++) + "    "
						+ p.getMRID() + "    "
						+ p.getName() + "    "
						+ p.getName() + "    "
						+ p.getType() + "    "
						+ subsId + "    "
						+ "null" + "    "
						+ "null";;
				writer.write(txt + "\r\n");
			}
			txt = "</PowerTransformer::SQ>";
			writer.write(txt + "\r\n");
			
			
			index = 1;
			txt = "<TransformerWinding::SQ>";
			writer.write(txt + "\r\n");
			txt = "@@Num	mRID	name	pathName	WindingType	Substation	PowerTransformer	I_node	BaseVoltage	VoltageLevel	TapChangerType	ratedMVA	ratedkV	loadLoss	leakageImpedence	r	x	r0	x0	P	p_qual	Q	q_qual	D	d_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	绕组类型	厂站标识	变压器标识	物理连接节点	基准电压标识	电压等级标识	分接头类型标识	额定功率	额定电压	短路损耗	短路电压百分比	电阻	电抗	零序电阻	零序电抗	有功量测	有功质量码	无功量测	无功质量码	档位量测	档位质量码";
			writer.write(txt + "\r\n");
			for (PowerTransformer  p: PowerTransformerList) {
				Map<String,TransformerWinding> tfwMap = (Map<String, TransformerWinding>) p.getTransformerWindingMap();
				for(String twId:tfwMap.keySet()){
					TransformerWinding transformerWinding = (TransformerWinding)tfwMap.get(twId);
					txt = "#	" + (index++) + "    "
							+ transformerWinding.getMRID() + "    "
							+ transformerWinding.getName() + "    "
							+ transformerWinding.getName() + "    "
							+ transformerWinding.getType() + "    "
							+ subsId + "    "
							+ transformerWinding.getPowerTransformer() + "    "
							+ transformerWinding.getPhysicNodeBegin() + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null" + "    "
							+ "null";
					writer.write(txt + "\r\n");
				}
			}
			txt = "</TransformerWinding::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<SynchronousMachine::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	I_node	Substation	BaseVoltage	VoltageLevel	RatedMW	maxU	minU	maxQ	minQ	maxP	minP	r	x	r0	x0	AuxRatio	P	p_qual	Q	q_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	发电机类型	物理连接节点	所属厂站标识	基准电压标识	所属电压等级标识	额定功率	最大电压限值	最小电压限值	最大无功限值	最小无功限值	最大有功限值	最小有功限值	正序电阻	正序电抗	零序电阻	零序电抗	厂用电率	有功量测	有功质量码	无功量测	无功质量码";
			writer.write(txt + "\r\n");
			txt = "</SynchronousMachine::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Load::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	I_node	BaseVoltage	VoltageLevel	P	p_qual	Q	q_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	厂站标识	物理连接节点	基准电压标识	电压等级标识	有功量测	有功质量码	无功量测	无功质量码";
			writer.write(txt + "\r\n");
			txt = "</Load::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	nomQ	V_rate	I_node	BaseVoltage	VoltageLevel	Substation	Q	q_qual";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	额定容量	额定电压	物理连接节点	基准电压标识	电压等级标识	厂站标识	无功量测	无功质量码";
			writer.write(txt + "\r\n");
			txt = "</ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	r	x	I_node	J_node	BaseVoltage	VoltageLevel	Substation	Pi	Qi	Pj	Qj";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	电阻	电抗	物理连接节点	物理连接节点	基准电压标识	电压等级标识	厂站标识	I侧有功量测	I侧无功量测	J侧有功量测	I侧无功量测";
			writer.write(txt + "\r\n");
			txt = "</ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			
			
			index = 1;
			txt = "<RelaySignal::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	rly_type	BaseVoltage	VoltageLevel";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	厂站标识	保护类型	基准电压标识	电压等级标识";
			writer.write(txt + "\r\n");
			txt = "</RelaySignal::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<EndDevice::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	term_type	BaseVoltage	VoltageLevel	I_node";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	标准带路径全名	厂站标识	终端类型	基准电压标识	电压等级标识	物理连接节点";
			writer.write(txt + "\r\n");
			txt = "</EndDevice::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ComputeValue::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	厂站标识";
			writer.write(txt + "\r\n");
			txt = "</ComputeValue::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<State::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	厂站标识";
			writer.write(txt + "\r\n");
			txt = "</State::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Signal::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	厂站标识";
			writer.write(txt + "\r\n");
			txt = "</Signal::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Measure::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	中文原名	厂站标识";
			writer.write(txt + "\r\n");
			txt = "</Measure::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Analog::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	devName	devID	type";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	量测中文原名	标准带路径全名	设备类名	设备类标识	量测类型";
			writer.write(txt + "\r\n");
			txt = "</Analog::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Analog::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	devName	devID	type";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	量测中文原名	标准带路径全名	设备类名	设备类标识	量测类型";
			writer.write(txt + "\r\n");
			txt = "</Analog::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Discrete::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	devName	devID	type";
			writer.write(txt + "\r\n");
			txt = "//序号	标识	量测中文原名	标准带路径全名	设备类名设备类标识	量测类型";
			writer.write(txt + "\r\n");
			txt = "</Discrete::SQ>";
			writer.write(txt + "\r\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setSubstationList(){
		String sql = "SELECT T.SUBS_ID ID,T.SUBS_NAME NAME FROM LOSS_ARCH_EQUIP_SUBS T WHERE T.SUBS_ID IN('"+subsId.replace(",", "','")+"')";
		List<Map<String, String>> subsList = jdbcDao.queryBySql(sql);
		for (Map<String, String> map : subsList) {
			String id = map.get("ID");
			String name = map.get("NAME");
			if(name.contains("SQ")){
				name = name.substring(3);
			}
			Substation s = new Substation();
			s.setMRID(id);
			s.setName(name);
			s.setPathName(name);
			SubstationList.add(s);
		}
	}

	/**
	 * 从cim文件获取主变信息
	 * 
	 * @return
	 */
	public void setTranList() {
		String transWindSql = "SELECT T.OBJ_ID,T.TW_NAME,T.TRAN_ID,decode(T.TW_TYPE,'01','高','02','中','03','低') TW_TYPE,T1.T_CONN_ID,T1.SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_TRANSWIND T, LOSS_ARCH_TOPO_TERMINAL T1"
				+ " WHERE T.OBJ_ID = T1.T_DEVICE_ID AND T.TRAN_ID IN(SELECT T.TRAN_ID FROM LOSS_ARCH_EQUIP_TRAN T WHERE T.SUBS_ID IN('"+subsId.replace(",", "','")+"') AND T.TRAN_TYPE='01')";
		List<Map<String, String>> twList = jdbcDao.queryBySql(transWindSql);
		Map<String, Map<String, TransformerWinding>> twMap = new HashMap<String, Map<String, TransformerWinding>>();
		for (Map<String, String> map : twList) {
			String objId = map.get("OBJ_ID");
			String twName = map.get("TW_NAME");
			String id = map.get("TRAN_ID");
			String type = map.get("TW_TYPE");
			String connId = map.get("T_CONN_ID");
			String subId = map.get("SUBID");
			TransformerWinding tw = new TransformerWinding();
			tw.setMRID(objId);
			tw.setName(twName);
			tw.setPathName(twName);
			tw.setPowerTransformer(id);
			tw.setType(type);
			tw.setPhysicNodeBegin(connId);
			tw.setSubStation(subId);
			TransformerWindingList.add(tw);
			Map<String, TransformerWinding> tranWMap = new HashMap<String, TransformerWinding>();
			;
			if (!twMap.containsKey(id)) {
				tranWMap = new HashMap<String, TransformerWinding>();
			} else {
				tranWMap = twMap.get(id);

			}
			tranWMap.put(type, tw);
			twMap.put(id, tranWMap);
		}
		String sql = "SELECT T.TRAN_ID ID, T.TRAN_NAME  NAME,T.RZXS TYPE,T.SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_TRAN T WHERE T.SUBS_ID IN('"+subsId.replace(",", "','")+"') AND T.TRAN_TYPE='01'";
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for (Map<String, String> map : list) {
			String id = map.get("ID");
			String name = map.get("NAME");
			String type = map.get("TYPE");
			String subId = map.get("SUBID");
			if(type==null || type.trim().length()==0 || "null".equals(type.trim())){
				type = "01";
			}
			if(Integer.parseInt("01")==Integer.parseInt(type)){//两卷变
				type = "二绕变";
			}else if (Integer.parseInt("02")==Integer.parseInt(type)){//三卷变
				type = "三绕变";
			}
			PowerTransformer po = new PowerTransformer();
			po.setMRID(id);
			po.setName(name);
			po.setPathName(name);
			po.setType(type);
			po.getTransformerWindingMap().putAll(twMap.get(id));
			po.setSubStation(subId);
			PowerTransformerList.add(po);
		}
	}

	/**
	 * 从数据库获取开关信息
	 * 
	 * @return
	 */
	public void setBreakerList() {
		String sql = "SELECT T.SWITCH_ID ID,T.SWITCH_NAME NAME,T.SWITCH_SORT_TYPE TYPE,WM_CONCAT(T1.T_CONN_ID) CONNMSG,T.SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_SWITCH T,LOSS_ARCH_TOPO_TERMINAL T1"
				+ " WHERE T.SWITCH_ID=T1.T_DEVICE_ID AND T.SUBS_ID IN('"+subsId.replace(",", "','")+"') AND T.SWITCH_SORT_TYPE IN('1205015','1205019')"
				+ " GROUP BY T.SWITCH_ID,T.SWITCH_NAME,T.SWITCH_SORT_TYPE,T.SUBS_ID";
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for (Map<String, String> map : list) {
			String id = map.get("ID");
			String name = map.get("NAME");
			String type = map.get("TYPE");
			String connMsg = map.get("CONNMSG");
			String subId = map.get("SUBID");
			String[] conns = null;
			if (connMsg != null && connMsg.trim().length() != 0) {
				conns = connMsg.split(",");
			}
			Breaker po = new Breaker();
			po.setMRID(id);
			po.setName(name);
			po.setPathName(name);
			po.setType(type);
			po.setPhysicNodeBegin(conns[0]);
			po.setSubStation(subId);
			if (conns.length == 2) {
				po.setPhysicNodeEnd(conns[1]);
			}
			BreakerList.add(po);
		}
	}

	/**
	 * 从数据库获取刀闸信息
	 * 
	 * @return
	 */
	public void setDisconnectorList() {
		String sql = "SELECT T.SWITCH_ID ID,T.SWITCH_NAME NAME,T.SWITCH_SORT_TYPE TYPE,WM_CONCAT(T1.T_CONN_ID) CONNMSG,T.SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_SWITCH T,LOSS_ARCH_TOPO_TERMINAL T1"
				+ " WHERE T.SWITCH_ID=T1.T_DEVICE_ID AND T.SUBS_ID IN('"+subsId.replace(",", "','")+"') AND T.SWITCH_SORT_TYPE IN('1205016', '1205017')"
				+ " GROUP BY T.SWITCH_ID,T.SWITCH_NAME,T.SWITCH_SORT_TYPE,T.SUBS_ID";
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for (Map<String, String> map : list) {
			String id = map.get("ID");
			String name = map.get("NAME");
			String type = map.get("TYPE");
			String connMsg = map.get("CONNMSG");
			String subId = map.get("SUBID");
			String[] conns = null;
			if (connMsg != null && connMsg.trim().length() != 0) {
				conns = connMsg.split(",");
			}
			Disconnector po = new Disconnector();
			po.setMRID(id);
			po.setName(name);
			po.setPathName(name);
			po.setType(type);
			po.setPhysicNodeBegin(conns[0]);
			if (conns.length == 2) {
				po.setPhysicNodeEnd(conns[1]);
			}
			po.setSubStation(subId);
			DisconnectorList.add(po);
		}
	}

	/**
	 * 从数据库获取母线信息
	 * 
	 * @return
	 */
	public void setBusList() {
		String sql = "SELECT T.BUSLINE_ID ID,T.BUSLINE_NAME NAME,T.VOLT_LEVEL,T1.T_CONN_ID CONNMSG,T.SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_BUSLINE T,LOSS_ARCH_TOPO_TERMINAL T1"
				+ " WHERE T.BUSLINE_ID=T1.T_DEVICE_ID AND T.SUBS_ID IN('"+subsId.replace(",", "','")+"')";
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for (Map<String, String> map : list) {
			String id = map.get("ID");
			String name = map.get("NAME");
			String voltLevel = map.get("VOLT_LEVEL");
			String connMsg = map.get("CONNMSG");
			String subId = map.get("SUBID");
			BusbarSection po = new BusbarSection();
			po.setMRID(id);
			po.setName(name);
			po.setPathName(name);
			po.setVoltageLevel(voltLevel);
			po.setPhysicNodeBegin(connMsg);
			po.setSubStation(subId);
			BusbarSectionlist.add(po);
		}
	}

	/**
	 * 从数据库获取线路信息
	 * 
	 * @return
	 */
	public void setLineList() {
		String sql = "SELECT T.LINE_ID ID, T.LINE_NAME NAME,T1.T_CONN_ID CONNMSG,T.START_SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_LINE T, LOSS_ARCH_TOPO_TERMINAL T1"
				+ " WHERE T.LINE_ID = T1.T_DEVICE_ID AND T.START_SUBS_ID IN('"+subsId.replace(",", "','")+"')" +
				" UNION ALL " +
				"SELECT T.LINE_ID ID, T.LINE_NAME NAME,T1.T_CONN_ID CONNMSG,T.END_SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_LINE T, LOSS_ARCH_TOPO_TERMINAL T1"
				+ " WHERE T.LINE_ID = T1.T_DEVICE_ID AND T.END_SUBS_ID IN('"+subsId.replace(",", "','")+"')";
		
		List<Map<String, String>> list = jdbcDao.queryBySql(sql);
		for (Map<String, String> map : list) {
			String id = map.get("ID");
			String name = map.get("NAME");
			String connMsg = map.get("CONNMSG");
			String subId = map.get("SUBID");
			String[] conns = null;
			if (connMsg != null && connMsg.trim().length() != 0) {
				conns = connMsg.split(",");
			}
			for (int i = 0; i < conns.length; i++) {
				ACLineDot po = new ACLineDot();
				if (i == 0) {
					po.setMRID(id + "1");
					po.setName(name + "首端");
					po.setPathName(name + "首端");
					po.setPhysicNodeBegin(conns[0]);
					po.setAclineSegment(id);
					po.setSubStation(subId);
					ACLineDotList.add(po);
				} else {
					po.setMRID(id + "2");
					po.setName(name + "末端");
					po.setPathName(name + "末端");
					po.setPhysicNodeBegin(conns[0]);
					po.setAclineSegment(id);
					po.setSubStation(subId);
					ACLineDotList.add(po);
				}
			}

		}
	}
}
