package com.jb.genemap.next.service;

import java.util.List;

import com.flowCal.device.model.main.ACLineDot;
import com.flowCal.device.model.main.Breaker;
import com.flowCal.device.model.main.BusbarSection;
import com.flowCal.device.model.main.Disconnector;
import com.flowCal.device.model.main.PowerTransformer;

public interface IParse {

	/**
	 * ��ȡ������Ϣ
	 * @return
	 */
	public List<PowerTransformer> getTranMap();
	
	/**
	 * ��ȡ������Ϣ
	 * @return
	 */
	public List<Breaker> getBreakerMap();
	
	/**
	 * ��ȡ��բ��Ϣ
	 * @return
	 */
	public List<Disconnector> getDisconnectorMap();
	
	/**
	 * ��ȡĸ����Ϣ
	 * @return
	 */
	public List<BusbarSection> getBusMap();
	
	/**
	 * ��ȡ��·��Ϣ
	 * @return
	 */
	public List<ACLineDot> getLineMap();
}
