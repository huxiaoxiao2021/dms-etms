package com.jd.bluedragon.distribution.consumer.reverse;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.domain.DmsRouter;
import com.jd.bluedragon.core.base.DtcDataReceiverManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("dmsRouterConsumer")
public class DmsRouterConsumer extends MessageBaseConsumer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static List<String> stores = Lists.newArrayList("6,6,51", "6,6,53", "10,10,51");

	@Autowired
	private DtcDataReceiverManager dtcDataReceiverManager;
//	private Inbound inbound;

	@SuppressWarnings("rawtypes")
	public void consume(Message message) throws Exception {
		if (message == null || message.getText() == null) {
			return;
		}

		DmsRouter dmsRouter = JsonHelper.fromJson(message.getText(), DmsRouter.class);
		if (dmsRouter.getType() == 80) {
			this.log.debug("[分拣中心OEM推送WMS]messageContent:{}" , dmsRouter.getBody());

			Map map = JsonHelper.json2Map(dmsRouter.getBody());
			if (map != null && map.get("orgId") != null && map.get("cky2") != null && map.get("storeId") != null
					&& map.get("orderId") != null) {

				String target = map.get("orgId").toString() + "," + map.get("cky2").toString() + ","
						+ map.get("storeId").toString();

				if (stores.contains(target)) {
//					String methodName = "XTQ";
					String messageValue = dmsRouter.getBody();
					String outboundNo = map.get("orderId").toString();
					String outboundType = "XTQDl";
					String source = "DMS";

//					Result result = this.inbound.forwardHandleMessage(target, methodName, messageValue, outboundNo,
//							outboundType, source);
					com.jd.staig.receiver.rpc.Result result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, outboundNo);
					
					this.log.debug("[分拣中心OEM推送WMS]:接口访问成功，result.getResultCode()={}" , result.getResultCode());
					this.log.debug("[分拣中心OEM推送WMS]:接口访问成功，result.getResultMessage()={}" , result.getResultMessage());
					this.log.debug("[分拣中心OEM推送WMS]:接口访问成功，result.getResultValue()={}" , result.getResultValue());
					if (result.getResultCode()== 1) {
						this.log.warn("[分拣中心OEM推送WMS]消息失败，运单号为:{}" , outboundNo);
					}
				}
			}
		}
	}
}
