package com.jd.bluedragon.core.message.consumer.reverse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.reverse.domain.PickWare;
import com.jd.bluedragon.distribution.reverse.service.PickWareService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.message.Message;
@Service("pickWareConsumer")
public class PickWareConsumer extends MessageBaseConsumer {  
	 
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	PickWareService pickWareService;
	
	public void consume(Message message) throws Exception {
		if (message == null || message.getContent() == null) {
			return;
		}
		String messageContent = message.getContent();
        this.logger.info("[备件库取件单-交接/拆包]messageContent：" + messageContent);
        PickWare pickWare = JsonHelper.fromJson(messageContent, PickWare.class);
        pickWare.setPickwareTime(DateHelper.parseDateTime(pickWare.getOperateTime()));
        pickWareService.add(pickWare);
	}

}
