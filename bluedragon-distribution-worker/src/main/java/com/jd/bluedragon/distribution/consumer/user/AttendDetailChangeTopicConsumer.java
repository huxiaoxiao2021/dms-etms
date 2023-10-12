package com.jd.bluedragon.distribution.consumer.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumer.record.DmsHasnoPresiteWaybillMqListener;
import com.jd.bluedragon.distribution.station.entity.AttendDetailChangeTopicData;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.jmq.common.message.Message;

@Service("attendDetailChangeTopicConsumer")
public class AttendDetailChangeTopicConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DmsHasnoPresiteWaybillMqListener.class);

    @Autowired
    private UserSignRecordService userSignRecordService;
    
    @Override
    public void consume(Message message) throws Exception {
    	AttendDetailChangeTopicData mqData = JsonHelper.fromJson(message.getText(), AttendDetailChangeTopicData.class);
    	if(log.isDebugEnabled()) {
    		log.debug("attendDetailChangeTopicListener：[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    	}
    	if(mqData == null){
    		log.warn("消息转换失败！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	JdResult<Integer> result = userSignRecordService.autoHandleSignOutByAttendJmq(mqData);
    	//处理异常-重试
    	if(result != null && result.isError()) {
    		log.error("attendDetailChangeTopicListener-error:[{}-{}]:[{}] 异常原因:[{}]",message.getTopic(),message.getBusinessId(),message.getText(),result.getMessage());
    		throw new Exception("attendDetailChangeTopicListener-error:" + result.getMessage());
    	}
    }
    
}
