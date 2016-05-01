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
 * �����ݿ��ȡ������תΪcime�ļ�
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
		int index = 1;// ���
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				// System.out.println(fileName + "�ı��ļ������ɹ�");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			writer = new FileWriter(fileName);
			// ��Ŀ<!Entity=Ȫ�� type=����ģ�� time='2014-03-13T02:00:12'!>
			String txt = "<!Entity=SQ type=����ģ��    time='" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss") + "'!>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ControlArea::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Parent	p	q	area_type";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	��������	�������ʶ	���й�	���޹�	�������� ";
			writer.write(txt + "\r\n");
			txt = "</ControlArea::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<BaseVoltage::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	nomkV";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	��׼��ѹ��	��׼��ѹ";
			writer.write(txt + "\r\n");
			txt = "</BaseVoltage::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Substation::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	ControlArea	p	q	x	y	i_flag	mGdis_flag	mUnXf_flag";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·����վȫ��	��վ����	���������ʶ	���й�	���޹�	��վ����	��վγ��	���������ʶ	�ص������ʶ	�����ѹ�������ʶ";
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
			txt = "//���	��ʶ	����ԭ��	��׼��·����ѹ�ȼ�ȫ��	��ѹ����	��ѹ����	������վ��ʶ	��׼��ѹ��ʶ	��������";
			writer.write(txt + "\r\n");
			txt = "</VoltageLevel::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Bay::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	VoltageLevel	type";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	������վ��ʶ	������ѹ�ȼ���ʶ	��������";
			writer.write(txt + "\r\n");
			txt = "</Bay::SQ>";
			writer.write(txt + "\r\n");

			/**
			 * ����<Breaker::����ׯվ></Breaker::����ׯվ>
			 */
			index = 1;
			// txt = "//����";
			// writer.write(txt + "\r\n");
			txt = "<Breaker::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	I_node	J_node	Substation	BaseVoltage	VoltageLevel	mvarating	status	pnt_qual";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	��·������	�������ӽڵ��	�������ӽڵ��	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	�ڶ�����	״̬	ң������";
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
			// txt = "//����";
			// writer.write(txt + "\r\n");
			txt = "<Disconnector::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	I_node	J_node	Substation	BaseVoltage	VoltageLevel	status	pnt_qual";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	�������ӽڵ��	�������ӽڵ��	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	״̬	ң������";
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
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	�ص�����	�������ӽڵ��	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	״̬	ң������";
			writer.write(txt + "\r\n");
			txt = "</GroundDisconnector::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<BusbarSection::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	I_node	Substation	BaseVoltage	VoltageLevel	Location	V	v_qual	A	ang_qual	maxV	minV";
			writer.write(txt + "\r\n");
			txt = "//���	ԭ��ʶ	����ԭ��	��׼��·��ȫ��	ĸ�߽ڵ��	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	λ����Ϣ	��ѹ����	�ߵ�ѹ������	�������	���������	��ѹ����	��ѹ����";
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
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	���������߶���";
			writer.write(txt + "\r\n");
			txt = "</ACLine::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ACLineSegment::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	StartSt	EndSt	ratedMW	ratedCurrent	BaseVoltage	r	x	bch	r0	x0	b0ch	ACLine";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	�׶˳�վ��ʶ	ĩ�˳�վ��ʶ	������ֵ	����������	��׼��ѹ��ʶ	�������	����翹	�������	�������	����翹	�������	������·��ʶ";
			writer.write(txt + "\r\n");
			txt = "</ACLineSegment::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			// txt = "//����";
			// writer.write(txt + "\r\n");
			txt = "<ACLineDot::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	ACLineSegment	Substation	I_node	BaseVoltage	VoltageLevel	P	p_qual	Q	q_qual";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	�����߶α�ʶ	��վ��ʶ	�������ӽڵ�	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	�й�����	�й�������	�޹�����	�޹�������";
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
			txt = "//���	��ʶ	�ֽ�ͷ������	���λ	�е��ѹ	���λ	��С��λ	����	��λ����";
			writer.write(txt + "\r\n");
			txt = "</TapChangerType::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<PowerTransformer::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	type	Substation	NoLoadLoss	ExcitingCurrent";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	����	��վ��ʶ	�������	���ص����ٷֱ�";
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
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	��������	��վ��ʶ	��ѹ����ʶ	�������ӽڵ�	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	�ֽ�ͷ���ͱ�ʶ	�����	���ѹ	��·���	��·��ѹ�ٷֱ�	����	�翹	�������	����翹	�й�����	�й�������	�޹�����	�޹�������	��λ����	��λ������";
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
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	���������	�������ӽڵ�	������վ��ʶ	��׼��ѹ��ʶ	������ѹ�ȼ���ʶ	�����	����ѹ��ֵ	��С��ѹ��ֵ	����޹���ֵ	��С�޹���ֵ	����й���ֵ	��С�й���ֵ	�������	����翹	�������	����翹	���õ���	�й�����	�й�������	�޹�����	�޹�������";
			writer.write(txt + "\r\n");
			txt = "</SynchronousMachine::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Load::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	I_node	BaseVoltage	VoltageLevel	P	p_qual	Q	q_qual";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	��վ��ʶ	�������ӽڵ�	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	�й�����	�й�������	�޹�����	�޹�������";
			writer.write(txt + "\r\n");
			txt = "</Load::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	nomQ	V_rate	I_node	BaseVoltage	VoltageLevel	Substation	Q	q_qual";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	�����	���ѹ	�������ӽڵ�	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	��վ��ʶ	�޹�����	�޹�������";
			writer.write(txt + "\r\n");
			txt = "</ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	r	x	I_node	J_node	BaseVoltage	VoltageLevel	Substation	Pi	Qi	Pj	Qj";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	����	�翹	�������ӽڵ�	�������ӽڵ�	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	��վ��ʶ	I���й�����	I���޹�����	J���й�����	I���޹�����";
			writer.write(txt + "\r\n");
			txt = "</ShuntCompensator::SQ>";
			writer.write(txt + "\r\n");
			
			
			index = 1;
			txt = "<RelaySignal::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	rly_type	BaseVoltage	VoltageLevel";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	��վ��ʶ	��������	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ";
			writer.write(txt + "\r\n");
			txt = "</RelaySignal::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<EndDevice::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	Substation	term_type	BaseVoltage	VoltageLevel	I_node";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��׼��·��ȫ��	��վ��ʶ	�ն�����	��׼��ѹ��ʶ	��ѹ�ȼ���ʶ	�������ӽڵ�";
			writer.write(txt + "\r\n");
			txt = "</EndDevice::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<ComputeValue::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��վ��ʶ";
			writer.write(txt + "\r\n");
			txt = "</ComputeValue::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<State::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��վ��ʶ";
			writer.write(txt + "\r\n");
			txt = "</State::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Signal::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��վ��ʶ";
			writer.write(txt + "\r\n");
			txt = "</Signal::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Measure::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	Substation";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	����ԭ��	��վ��ʶ";
			writer.write(txt + "\r\n");
			txt = "</Measure::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Analog::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	devName	devID	type";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	��������ԭ��	��׼��·��ȫ��	�豸����	�豸���ʶ	��������";
			writer.write(txt + "\r\n");
			txt = "</Analog::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Analog::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	devName	devID	type";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	��������ԭ��	��׼��·��ȫ��	�豸����	�豸���ʶ	��������";
			writer.write(txt + "\r\n");
			txt = "</Analog::SQ>";
			writer.write(txt + "\r\n");
			
			index = 1;
			txt = "<Discrete::SQ>";
			writer.write(txt + "\r\n");
			txt = "@Num	mRID	name	pathName	devName	devID	type";
			writer.write(txt + "\r\n");
			txt = "//���	��ʶ	��������ԭ��	��׼��·��ȫ��	�豸�����豸���ʶ	��������";
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
	 * ��cim�ļ���ȡ������Ϣ
	 * 
	 * @return
	 */
	public void setTranList() {
		String transWindSql = "SELECT T.OBJ_ID,T.TW_NAME,T.TRAN_ID,decode(T.TW_TYPE,'01','��','02','��','03','��') TW_TYPE,T1.T_CONN_ID,T1.SUBS_ID SUBID FROM LOSS_ARCH_EQUIP_TRANSWIND T, LOSS_ARCH_TOPO_TERMINAL T1"
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
			if(Integer.parseInt("01")==Integer.parseInt(type)){//�����
				type = "���Ʊ�";
			}else if (Integer.parseInt("02")==Integer.parseInt(type)){//�����
				type = "���Ʊ�";
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
	 * �����ݿ��ȡ������Ϣ
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
	 * �����ݿ��ȡ��բ��Ϣ
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
	 * �����ݿ��ȡĸ����Ϣ
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
	 * �����ݿ��ȡ��·��Ϣ
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
					po.setName(name + "�׶�");
					po.setPathName(name + "�׶�");
					po.setPhysicNodeBegin(conns[0]);
					po.setAclineSegment(id);
					po.setSubStation(subId);
					ACLineDotList.add(po);
				} else {
					po.setMRID(id + "2");
					po.setName(name + "ĩ��");
					po.setPathName(name + "ĩ��");
					po.setPhysicNodeBegin(conns[0]);
					po.setAclineSegment(id);
					po.setSubStation(subId);
					ACLineDotList.add(po);
				}
			}

		}
	}
}
