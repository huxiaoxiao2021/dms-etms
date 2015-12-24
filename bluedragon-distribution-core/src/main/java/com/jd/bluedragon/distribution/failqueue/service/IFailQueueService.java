package com.jd.bluedragon.distribution.failqueue.service;

import com.jd.bluedragon.distribution.departure.domain.Departure;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;

import java.util.List;
import java.util.Map;

public interface IFailQueueService {
	
	public static final Integer ERROR_TYPE = -1;
	/**
	 * 发货类型-三方
	 */
	public static final Integer SEND_TYPE_3PL = 1;
	/**
	 * 发车类型
	 */
	public static final Integer DEPARTURE_TYPE = 2;
	
	/**
	 * 发车类型
	 */
	public static final Integer DEPARTURE_TYPE_3PL = 6;
	
	/**
	 * 发货类型-自营
	 */
	public static final Integer SEND_TYPE_SELF = 3;
	/**
	 * 发货批次-按批次处理
	 */
	public static final Integer SEND_TYPE_BATCH = 4;
	
	public static final Integer DMS_SEND_SELF = 10;
	public static final Integer DMS_SEND_3PL = 30;
	
	public void departureNewData(Departure departure,long shieldsCarId,boolean pushDeparture);
	
	/**
	 * 将支线三方发车的数据推送到3PL
	 * @param list
	 */
    public void departure3PLData(List<TaskFailQueue> list);
	
	
	public void failData(List<TaskFailQueue> list);
	
    public void sendCodeNewData(String sendCode,Integer type);
	public void sendCodeNewData(List<String> sendCodes,Integer type);
	public List<TaskFailQueue> query(Map<String,Object> param);	
    public void sendDatailBatchData(List<TaskFailQueue> sendDatailAl_batch);
    public void lock(List<TaskFailQueue> tasks);
}
