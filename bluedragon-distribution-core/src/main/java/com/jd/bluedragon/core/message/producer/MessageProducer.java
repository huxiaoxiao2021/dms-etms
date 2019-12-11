package com.jd.bluedragon.core.message.producer;

import java.util.Date;

import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.jd.etms.message.produce.client.MessageClient;
//import com.jd.ump.profiler.proxy.Profiler;

@Service("messageProducer")
public class MessageProducer {


	private static final long MQ_ALERT_TIME = 1500;
	private static final String MQ_ALERT_UMP_KEY = "Bluedragon_dms_center.mq.write.timeout";

	//@Autowired
	//private MessageClient messageClient;

    @JProfiler(jKey = "MessageProducer.send", mState = {JProEnum.TP})
	public void send(String key, String message, String index) {
		/*long begin_ms = System.currentTimeMillis();

		this.messageClient.sendMessage(key, message, index);

		long end_ms = System.currentTimeMillis() - begin_ms;

		// 如果超过1500ms这启用UMP预警 /
		if (end_ms > MQ_ALERT_TIME) {
			try {
				String alertMessage = "MQ推送  业务主键:【" + key + "】超时【" + end_ms + "ms】";
				Profiler.businessAlarm(MQ_ALERT_UMP_KEY, new Date().getTime(), alertMessage);
			} catch (Exception e) {
				this.log.error("推送UMP发生异常.", e);
			}
		}
		*/
	}
}
