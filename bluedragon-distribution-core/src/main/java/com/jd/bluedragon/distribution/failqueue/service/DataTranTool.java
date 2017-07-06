package com.jd.bluedragon.distribution.failqueue.service;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.failqueue.domain.DealData_Departure;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_SendDatail;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.finance.wss.pojo.SortingCar;

public class DataTranTool {
	/*
	 * 发货，取消发货 的字段处理
	 * waybillCode	运单号	String	订单号
	 * sortingCenterId	发货方	int	分拣中心编号
	 * targetSiteId	站点编号	Int	订单对应的目标站点编号
	 * deliveryTime	发货时间 	String 	yyyy-MM-dd hh24:mm:ss
	 * sortBatchNo 发货批次
	 */
	
//	public static ArrayList<DealData_SendDatail> transSendDataToDealDate(List<SendDetail> sendDatailArray){
//		ArrayList<DealData_SendDatail> transdata = new ArrayList<DealData_SendDatail>();
//		for(SendDetail sendDatail:sendDatailArray){
//			long primaryKey = sendDatail.getSendDId();
//			String waybillCode = sendDatail.getWaybillCode();
//			int sortingCenterId = sendDatail.getCreateSiteCode();
//			int targetSiteId = sendDatail.getReceiveSiteCode();
//			String deliveryTime = DateHelper.formatDateTime(sendDatail.getUpdateTime());
//			String sortBatchNo = sendDatail.getSendCode();
//			/**处理数据*/
//			DealData_SendDatail tmpdata = new DealData_SendDatail(primaryKey,waybillCode,sortingCenterId,targetSiteId,deliveryTime,sortBatchNo);
//			transdata.add(tmpdata);
//		}
//		
//		return transdata;
//	}
	
	public static TaskFailQueue transSendDataToDealDate(SendDetail sendDatail){
		Long primaryKey = sendDatail.getSendDId();
		String waybillCode = sendDatail.getWaybillCode();
		int sortingCenterId = sendDatail.getCreateSiteCode();
		int targetSiteId = sendDatail.getReceiveSiteCode();
		String deliveryTime = DateHelper.formatDateTime(sendDatail.getUpdateTime());
		String sortBatchNo = sendDatail.getSendCode();
		/**处理数据*/
		DealData_SendDatail tmpdata = new DealData_SendDatail(primaryKey,waybillCode,sortingCenterId,targetSiteId,deliveryTime,sortBatchNo);
				
		TaskFailQueue taskFailQueueData = new TaskFailQueue();

		String body = JsonHelper.toJson(tmpdata);
		
		taskFailQueueData.setBusiId(primaryKey);
		if(sendDatail.getSendType().equals(IFailQueueService.DMS_SEND_3PL)){				
			/*发货*/
			taskFailQueueData.setBusiType(IFailQueueService.SEND_TYPE_3PL);
		}else if(sendDatail.getSendType().equals(IFailQueueService.DMS_SEND_SELF)){
			/*取消发货*/
			taskFailQueueData.setBusiType(IFailQueueService.SEND_TYPE_SELF);
		}else{
			taskFailQueueData.setBusiType(-1);
		}
		
		taskFailQueueData.setBody(body);
		
		return taskFailQueueData;

	}
	
	
	/*************************        数据转化          ***************************************/
//	public static TaskFailQueue transTaskFailQueue_SendDatail(DealData_SendDatail dealData,Integer busiType){
//		TaskFailQueue taskFailQueueData = new TaskFailQueue();
//		/*准备数据*/
//		long busiId = dealData.getPrimaryKey();
//		String body = JsonHelper.toJson(dealData);
//		
//		taskFailQueueData.setBusiId(busiId);
//		taskFailQueueData.setBusiType(busiType);
//		taskFailQueueData.setBody(body);
//		
//		return taskFailQueueData;
//	}
	
	public static TaskFailQueue transTaskFailQueue_SendCode(String sendCode){
		TaskFailQueue taskFailQueueData = new TaskFailQueue();
		taskFailQueueData.setBusiType(IFailQueueService.SEND_TYPE_BATCH);
		taskFailQueueData.setBody(sendCode);
		
		return taskFailQueueData;
	}
	
	public static TaskFailQueue transTaskFailQueue_Departure(DealData_Departure dealData,Integer busiType){
		TaskFailQueue taskFailQueueData = new TaskFailQueue();
		
		String body = JsonHelper.toJson(dealData);
		
		taskFailQueueData.setBusiId(dealData.getSortCarId());
		taskFailQueueData.setBusiType(busiType);
		taskFailQueueData.setBody(body);
		
		return taskFailQueueData;
	}
	
	public static List<DealData_SendDatail> transTaskFailQueueToDealData_SendDatail(List<TaskFailQueue> list){
		ArrayList<DealData_SendDatail> dealDataAl = new ArrayList<DealData_SendDatail>();
		for(TaskFailQueue taskFailQueue:list){
			String body = taskFailQueue.getBody();
			DealData_SendDatail tmpdealData = JsonHelper.fromJson(body, DealData_SendDatail.class);
			dealDataAl.add(tmpdealData);
		}
		
		return dealDataAl;
	}
	
	public static List<DealData_Departure> transTaskFailQueueToDealData_Departure(List<TaskFailQueue> list){
		ArrayList<DealData_Departure> dealDataAl = new ArrayList<DealData_Departure>();
		for(TaskFailQueue taskFailQueue:list){
			String body = taskFailQueue.getBody();
			DealData_Departure tmpdealData = JsonHelper.fromJson(body, DealData_Departure.class);
			dealDataAl.add(tmpdealData);
		}
		
		return dealDataAl;
	}

	public static ArrayList<com.jd.etms.finance.wss.pojo.SortingOrder> transDealDataSendDatailToFinanceData(List<DealData_SendDatail> list){
		/*准备数据*/
		ArrayList<com.jd.etms.finance.wss.pojo.SortingOrder> sendDataAl = new ArrayList<com.jd.etms.finance.wss.pojo.SortingOrder>();
		for(DealData_SendDatail dealData:list){
			com.jd.etms.finance.wss.pojo.SortingOrder tmpSortingOrder = new com.jd.etms.finance.wss.pojo.SortingOrder();
			tmpSortingOrder.setWaybillCode(dealData.getWaybillCode());
			tmpSortingOrder.setSortingCenterId(dealData.getSortingCenterId());
			tmpSortingOrder.setTargetSiteId(dealData.getTargetSiteId());
			tmpSortingOrder.setDeliveryTime(dealData.getDeliveryTime());
			tmpSortingOrder.setSortBatchNo(dealData.getSortBatchNo());
			/**
			 * 发货数据没有承运商ID，所以默认为0
			 */
			tmpSortingOrder.setCarrierId(0);
			sendDataAl.add(tmpSortingOrder);
		}

		return sendDataAl;
	}

	public static com.jd.etms.finance.wss.pojo.SortingOrder transDealDataSendDatailToFinanceData(DealData_SendDatail dealData) {
		com.jd.etms.finance.wss.pojo.SortingOrder sortingOrder = new com.jd.etms.finance.wss.pojo.SortingOrder();
		sortingOrder.setWaybillCode(dealData.getWaybillCode());
		sortingOrder.setSortingCenterId(dealData.getSortingCenterId());
		sortingOrder.setTargetSiteId(dealData.getTargetSiteId());
		sortingOrder.setDeliveryTime(dealData.getDeliveryTime());
		sortingOrder.setSortBatchNo(dealData.getSortBatchNo());
		/**
		 * 发货数据没有承运商ID，所以默认为0
		 */
		sortingOrder.setCarrierId(0);

		return sortingOrder;
	}

	public static java.util.List<com.jd.etms.finance.wss.pojo.SortingCar> transDealDataDepartureToFinanceData(List<DealData_Departure> departures){
		ArrayList<com.jd.etms.finance.wss.pojo.SortingCar> departureAl = new ArrayList<com.jd.etms.finance.wss.pojo.SortingCar>();
		
		for(DealData_Departure departure:departures){
			com.jd.etms.finance.wss.pojo.SortingCar sortingCar = new SortingCar();
			
			sortingCar.setCarrierId(departure.getCarrierId());
			sortingCar.setSortBatchNo(departure.getSortBatchNo());
			sortingCar.setSortCarId(departure.getSortCarId());
			sortingCar.setSortCarTime(departure.getSortCarTime());
			sortingCar.setSortingCenterId(departure.getSortingCenterId());
			//sortingCar.setTargetSiteId(departure.getTargetSiteId());
			sortingCar.setVolume(departure.getVolume()!=null?departure.getVolume():0);
			/**
			 * 重量单位为吨，财务需要千克，乘以1000
			 */
			sortingCar.setWeight(departure.getWeight()!=null?departure.getWeight()*1000:0);
			
			departureAl.add(sortingCar);
		}
		return departureAl;
	}
	
	public static java.util.List<com.jd.etms.finance.wss.pojo.SortingCar> transDealDataToFinanceData(DealData_Departure departure){
		ArrayList<DealData_Departure> tmpal = new ArrayList<DealData_Departure>();
		tmpal.add(departure);
		java.util.List<com.jd.etms.finance.wss.pojo.SortingCar> resultal = transDealDataDepartureToFinanceData(tmpal);
		return resultal;
	}
}
