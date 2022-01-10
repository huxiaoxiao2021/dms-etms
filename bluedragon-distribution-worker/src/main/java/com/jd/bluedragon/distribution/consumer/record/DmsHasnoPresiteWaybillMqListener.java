package com.jd.bluedragon.distribution.consumer.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.print.utils.StringHelper;

@Service("dmsHasnoPresiteWaybillMqListener")
public class DmsHasnoPresiteWaybillMqListener extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DmsHasnoPresiteWaybillMqListener.class);

    @Autowired
    private WaybillHasnoPresiteRecordService waybillHasnoPresiteRecordService;
    
    @Override
    public void consume(Message message) throws Exception {
    	DmsHasnoPresiteWaybillMq mqObj = JsonHelper.fromJson(message.getText(), DmsHasnoPresiteWaybillMq.class);
    	if(mqObj == null){
    		log.warn("消息转换失败！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	if(StringHelper.isEmpty(mqObj.getWaybillCode())){
    		log.warn("消息体无效，WaybillCode为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	waybillHasnoPresiteRecordService.syncMqDataToDb(mqObj);
    }
    
}
