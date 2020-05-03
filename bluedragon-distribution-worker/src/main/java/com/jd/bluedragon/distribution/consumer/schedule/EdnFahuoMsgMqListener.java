package com.jd.bluedragon.distribution.consumer.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.entity.EdnFahuoMsgMq;
import com.jd.bluedragon.distribution.schedule.entity.EdnFahuoMsgMq.Order;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.print.utils.StringHelper;

@Service("bdWaybillScheduleMqListener")
public class EdnFahuoMsgMqListener extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(EdnFahuoMsgMqListener.class);

    @Autowired
    private DmsScheduleInfoService dmsScheduleInfoService;

    @Override
    public void consume(Message message) throws Exception {
    	EdnFahuoMsgMq mqObj = JsonHelper.fromJson(message.getText(), EdnFahuoMsgMq.class);
    	if(mqObj == null){
    		log.warn("消息转换失败！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	if(StringHelper.isEmpty(mqObj.getEdnBatchNum())){
    		log.warn("消息体无效，ednBatchNum为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	if(mqObj.getOrderList() == null || mqObj.getOrderList().isEmpty()){
    		log.warn("消息体无效，orderList为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	for(Order order : mqObj.getOrderList()){
        	DmsScheduleInfo dmsScheduleInfo = new DmsScheduleInfo();
        	dmsScheduleInfo.setParentOrderId(mqObj.getParentOrderId());
        	dmsScheduleInfo.setBusinessBatchCode(mqObj.getEdnBatchNum());
        	dmsScheduleInfo.setWaybillCode(order.getWaybillCode());
        	dmsScheduleInfo.setBusinessUpdateTime(mqObj.getOperationTime());
        	dmsScheduleInfoService.syncEdnFahuoMsgToDb(dmsScheduleInfo);
    	}
    }
    
}
