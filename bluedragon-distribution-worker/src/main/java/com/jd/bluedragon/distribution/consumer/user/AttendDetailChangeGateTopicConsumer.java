package com.jd.bluedragon.distribution.consumer.user;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumer.record.DmsHasnoPresiteWaybillMqListener;
import com.jd.bluedragon.distribution.station.entity.AttendDetailChangeGateTopicData;
import com.jd.bluedragon.distribution.station.entity.AttendDetailChangeTopicData;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 人闸签退
 */
@Service("attendDetailChangeGateTopicConsumer")
public class AttendDetailChangeGateTopicConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DmsHasnoPresiteWaybillMqListener.class);

    @Autowired
    private UserSignRecordService userSignRecordService;
    
    @Override
    public void consume(Message message) throws Exception {
		// {"createTime":"2024-05-11T11:31:27","deviceName":"杭州枢纽6号库人脸机内进1","erp":"jiangtingju1","floor":"杭州富阳散货分拣中心",
		// "gateName":"正门","name":"姜庭菊","organization":"杭州富阳散货分拣中心","passResult":"成功","passStatus":"进门","passTime":"2024-05-11T11:31:35",
		// "province":"杭州枢纽","staffType":"正式工"}
		AttendDetailChangeGateTopicData mqData = JsonHelper.fromJson(message.getText(), AttendDetailChangeGateTopicData.class);
    	if(log.isDebugEnabled()) {
    		log.debug("AttendDetailChangeGateTopicConsumer：[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    	}
    	if(mqData == null){
    		log.warn("消息转换失败！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
//		JdResult<Integer> result0 = userSignRecordService.autoHandleSignOutByAttendJmq(mqData);
		log.info("SR 开始消费 mqData:{}", mqData);
		JdResult<Integer> result = userSignRecordService.autoHandleSignOutByAttendGateJmq(mqData);

    	//处理异常-重试
    	if(result != null && result.isError()) {
    		log.error("AttendDetailChangeGateTopicConsumer-error:[{}-{}]:[{}] 异常原因:[{}]",message.getTopic(),message.getBusinessId(),message.getText(),result.getMessage());
    		throw new Exception("AttendDetailChangeGateTopicConsumer-error:" + result.getMessage());
    	}
    }
    
}
