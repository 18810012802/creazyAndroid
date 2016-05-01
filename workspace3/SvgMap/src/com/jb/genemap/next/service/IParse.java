package com.jb.genemap.next.service;

import java.util.List;

import com.flowCal.device.model.main.ACLineDot;
import com.flowCal.device.model.main.Breaker;
import com.flowCal.device.model.main.BusbarSection;
import com.flowCal.device.model.main.Disconnector;
import com.flowCal.device.model.main.PowerTransformer;

public interface IParse {

	/**
	 * 获取主变信息
	 * @return
	 */
	public List<PowerTransformer> getTranMap();
	
	/**
	 * 获取开关信息
	 * @return
	 */
	public List<Breaker> getBreakerMap();
	
	/**
	 * 获取刀闸信息
	 * @return
	 */
	public List<Disconnector> getDisconnectorMap();
	
	/**
	 * 获取母线信息
	 * @return
	 */
	public List<BusbarSection> getBusMap();
	
	/**
	 * 获取线路信息
	 * @return
	 */
	public List<ACLineDot> getLineMap();
}
