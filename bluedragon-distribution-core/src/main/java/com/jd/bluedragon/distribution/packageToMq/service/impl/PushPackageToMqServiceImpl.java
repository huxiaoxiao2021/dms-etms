package com.jd.bluedragon.distribution.packageToMq.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.MessageConstant;
import com.jd.bluedragon.distribution.api.request.WmsOrderPackageRequest;
import com.jd.bluedragon.distribution.api.request.WmsOrderPackagesRequest;
import com.jd.bluedragon.distribution.packageToMq.domain.Pack;
import com.jd.bluedragon.distribution.packageToMq.domain.WaybillMqMsg;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service("IPushPackageToMqService")
public class PushPackageToMqServiceImpl implements IPushPackageToMqService {

	private final Logger logger = Logger.getLogger(PushPackageToMqServiceImpl.class);

    @Autowired
    @Qualifier("packageMQ")
    private DefaultJMQProducer packageMQ;

    private static final String STATEMENTID = "com.jd.orderpackage.pack";

    private static final String MQ_ERROR_UMP_ALERT_KEY = "Bluedragon_dms_center.mq.write.timeout";
    private static final String MQ_ERROR_UMP_ALERT_MSG = "MQ推送超时【1500ms】";
    
    /*MQ操作时间*/
    private static final long MQ_TIME = 1500;
    
	@Override
	public void pushPackageToMq(WmsOrderPackagesRequest orderPackages) throws Exception{
		WaybillMqMsg[] waybillmqs = transData(orderPackages);
		execPush(waybillmqs);
	}

	private WaybillMqMsg[] transData(WmsOrderPackagesRequest orderPackages){
		HashMap<String,WaybillMqMsg> waybillmqmsg = new HashMap<String,WaybillMqMsg>();

		for(WmsOrderPackageRequest orderPackage : orderPackages.getOrderPackages()){
			Pack pack = new Pack();
			String waybillno = orderPackage.getExpNo();

			/*非必输项*///pack.setOperatorId();
			pack.setOperatorName(orderPackage.getPackWkNo());
			pack.setOperatorTime(DateHelper.getSeverTime(orderPackage.getPackDate()));
			pack.setPackCode(orderPackage.getPackageSn());
			pack.setPackVolume(orderPackage.getVolume()==null?"0":orderPackage.getVolume().toString());
			pack.setPackWeight(orderPackage.getWeight()==null?"0":orderPackage.getWeight().toString());

			WaybillMqMsg tmpwaybill = waybillmqmsg.containsKey(waybillno)?waybillmqmsg.get(waybillno):new WaybillMqMsg();
			tmpwaybill.setStatementId(PushPackageToMqServiceImpl.STATEMENTID);
			tmpwaybill.addPack(pack);
			tmpwaybill.setWaybillCode(waybillno);
			waybillmqmsg.put(waybillno, tmpwaybill);

			logger.info("PushPackageToMqServiceImpl.transData 运单号[" +waybillno + "] 包裹号[" + orderPackage.getPackageSn() + "] 转换完毕，准备推送mq");
		}

		return waybillmqmsg.values().toArray(new WaybillMqMsg[0]);
	}

	@JProfiler(jKey= "DMSWEB.PushPackageToMqServiceImpl.execPush",mState = {JProEnum.TP})
	private void execPush(WaybillMqMsg[] waybillmqs) throws Exception{
        try {
        	for(WaybillMqMsg waybillmq:waybillmqs){
        		/*按照运单进行分组，businessId 是  'ORDER_PACKAGE_' + waybillno*/
        		//pubshMq("package",JsonHelper.toJson(waybillmq),MessageConstant.OrderPacke.getName() + waybillmq.getMsgObj().getWaybillCode());
                packageMQ.send(MessageConstant.OrderPacke.getName() + waybillmq.getMsgObj().getWaybillCode(),JsonHelper.toJson(waybillmq));
                logger.info("PushPackageToMqServiceImpl.execPush 运单号[" + waybillmq.getMsgObj().getWaybillCode() + "] 包裹数量[" + waybillmq.getMsgObj().getPackList().size() + "] 推送完毕");
        	}
        }catch(Exception e){
        	logger.error("PushPackageToMqServiceImpl.execPush 推送MQ异常",e);
        	throw e;
        }
	}

	@Override
	public void pubshMq(String key,String body,String busiId){
		/*开始时间*/
		long beign = System.currentTimeMillis();
		//this.messageClient.sendMessage(key,body,busiId);
		/*结束时间*/
		long end = System.currentTimeMillis()-beign;
		/*如果超过1500ms这启用UMP预警*/
		if(end>MQ_TIME){
			pushMqErrorUmpAlert(key,end);
		}
	}
	
	private void pushMqErrorUmpAlert(String key,long time){
		try{
			pushAlert(MQ_ERROR_UMP_ALERT_KEY,"MQ推送  业务主键:【" + key + "】超时【" + time + "ms】");
		}catch(Exception e){
			logger.error("PushPackageToMqServiceImpl.pushMqErrorUmpAlert 推送UMP异常",e);
		}
	}

	@Override
	public void pushAlert(String key,String msg) {
		logger.info("PushPackageToMqServiceImpl.execPush ");
		Profiler.businessAlarm(key, new Date().getTime(), msg);
	}
}
