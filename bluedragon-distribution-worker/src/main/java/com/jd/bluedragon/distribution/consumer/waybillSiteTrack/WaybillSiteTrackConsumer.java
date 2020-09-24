package com.jd.bluedragon.distribution.consumer.waybillSiteTrack;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.WaybillSiteTrackMq;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 异常平台 发送给终端、分拣、3pl 、城配  最新站点信息
 * @see <a>https://cf.jd.com/pages/viewpage.action?pageId=349684238</a>
 *
 * @author fanggang7
 * @time 2020-09-09 15:49:38 周三
 */
@Service("waybillSiteTrackConsumer")
public class WaybillSiteTrackConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(WaybillSiteTrackConsumer.class);

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            // 消息格式错误，加入自定义告警
            String profilerKey = "DMSWEB.WaybillSiteTrackConsumer.consume.badJsonMessage";
            Profiler.businessAlarm(profilerKey, MessageFormat.format("可换单消息MQ-消息体非JSON格式，businessId为【{0}】", message.getBusinessId()));
            return;
        }
        WaybillSiteTrackMq waybillSiteTrackMq = JsonHelper.fromJsonUseGson(message.getText(), WaybillSiteTrackMq.class);
        InvokeResult<Boolean> handleResult = waybillCancelService.handleWaybillSiteTrackMq(waybillSiteTrackMq);
        if(!handleResult.getData()){
            log.error("WaybillSiteTrackConsumer fail " + JSON.toJSONString(handleResult));
            throw new RuntimeException("WaybillSiteTrackConsumer 处理失败 " + handleResult.getMessage());
        }
    }
}
