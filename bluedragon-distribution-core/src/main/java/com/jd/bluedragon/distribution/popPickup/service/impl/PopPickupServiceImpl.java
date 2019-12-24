package com.jd.bluedragon.distribution.popPickup.service.impl;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.distribution.popPickup.dao.PopPickupDao;
import com.jd.bluedragon.distribution.popPickup.domain.PopPickup;
import com.jd.bluedragon.distribution.popPickup.service.PopPickupService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;

@Service
public class PopPickupServiceImpl implements PopPickupService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PopPickupDao popPickupDao;
	
	@Autowired
	private IPushPackageToMqService pushPackageToMqService;

    @Autowired
    @Qualifier("dmsPopPickupMQ")
    private DefaultJMQProducer dmsPopPickupMQ;

	private static final String MQ_KEY = "dms_pop_pickup";
	
	@Override
	public void insertOrUpdate( PopPickup popPickup ){
		if(Constants.NO_MATCH_DATA ==popPickupDao.update(PopPickupDao.namespace, popPickup)){
			popPickupDao.add(PopPickupDao.namespace, popPickup);
		}
	}

	public void pushPopPickupRequest(PopPickupRequest popPickupRequest) throws Exception{

		if(popPickupRequest == null){
			return;
		}
		
		try{
//			PopPickupMq popPickupMq = new PopPickupMq();
//			/** 运单号. */
//			popPickupMq.setWayBillNo(popPickupRequest.getWaybillCode());
//			/** 包裹号. */
//			popPickupMq.setPackNo(popPickupRequest.getPackageBarcode());
//			/** 包裹是否取消 1是；0否. */
//			popPickupMq.setIsCancel(popPickupRequest.getIsCancel());
//			/** 操作人id. */
//			popPickupMq.setUserCode(popPickupRequest.getUserCode().toString());
//			/** 操作人姓名 */
//			popPickupMq.setUserName(popPickupRequest.getUserName());
//			/** 站点编号. */
//			popPickupMq.setSiteCode(popPickupRequest.getSiteCode());
//			/** 站点名称 */
//			popPickupMq.setSiteName(popPickupRequest.getSiteName());
//			/** 来源 --分拣中心收货默认为4*/
//			popPickupMq.setSource(4);
//			/** 车牌号 */
//			popPickupMq.setCarCode(popPickupRequest.getCarCode());
//				
			String mq = JsonHelper.toJson(popPickupRequest);
			
			//pushPackageToMqService.pubshMq(MQ_KEY, mq, popPickupRequest.getPackageBarcode());
            dmsPopPickupMQ.send(popPickupRequest.getPackageBarcode(),mq);
		}catch(Exception e){
			this.log.error("PopPickupServiceImpl.pushPopPickupRequest to MQ error",e);
			throw e;
		}
	}

	@Override
	public void doPickup(Task task) {
		PopPickup popPickup = this.parseTask2Pickup(task);
		this.insertOrUpdate(popPickup);
	}
	
	public void doPickup(PopPickupRequest popPickupRequest) {
		PopPickup popPickup = null;
		if (popPickupRequest != null) {
			popPickup = new PopPickup.Builder(popPickupRequest.getWaybillCode(), popPickupRequest.getPackageBarcode(), 
					popPickupRequest.getPopBusinessCode(), popPickupRequest.getPopBusinessName(), 1)
					.createSiteCode(popPickupRequest.getSiteCode())
					.packageNumber(popPickupRequest.getPackageNumber())
					.carCode(popPickupRequest.getCarCode())
					.operateTime(DateHelper.getSeverTime(popPickupRequest.getOperateTime()))
					.createUserCode(popPickupRequest.getUserCode()).createUser(popPickupRequest.getUserName())
					.pickupType(popPickupRequest.getPickupType()).waybillType(popPickupRequest.getWaybillType()).build();
		}
		this.insertOrUpdate(popPickup);
	}


	private PopPickup parseTask2Pickup(Task task) {
		if (StringHelper.isEmpty(task.getBody())) {
            return null;
        }
		String body = task.getBody().substring(1,task.getBody().length()-1);
		PopPickupRequest popPickupRequest = JsonHelper.jsonToArray(body, PopPickupRequest.class);
		
		PopPickup popPickup = null;
		if (popPickupRequest != null) {
			popPickup = new PopPickup.Builder(popPickupRequest.getWaybillCode(), popPickupRequest.getPackageBarcode(), 
					popPickupRequest.getPopBusinessCode(), popPickupRequest.getPopBusinessName(), 1)
					.createSiteCode(popPickupRequest.getSiteCode())
					.packageNumber(popPickupRequest.getPackageNumber())
					.carCode(popPickupRequest.getCarCode())
					.operateTime(DateHelper.getSeverTime(popPickupRequest.getOperateTime()))
					.createUserCode(popPickupRequest.getUserCode())
					.createUser(popPickupRequest.getUserName())
					.waybillType(popPickupRequest.getWaybillType())
					.pickupType(popPickupRequest.getPickupType()).build();
		}
		return popPickup;
	}
	
	/**
	 * 按条件查询POP上门接货清单总数
	 * @param paramMap
	 * @return
	 */
	@Override
	public int findPopPickupTotalCount(Map<String, Object> paramMap) {
		return this.popPickupDao.findPopPickupTotalCount(paramMap);
	}


	/**
	 * 按条件查询POP上门接货清单集合
	 * @param paramMap
	 * @return
	 */
	@Override
	public List<PopPickup> findPopPickupList(Map<String, Object> paramMap) {
		return this.popPickupDao.findPopPickupList(paramMap);
	}
}
