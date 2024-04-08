package com.jd.bluedragon.distribution.consumer.jy.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.DirectDeliverySortCollectMQ;
import com.jd.bluedragon.distribution.collect.domain.DirectDeliverySortCollectRequest;
import com.jd.bluedragon.distribution.collect.service.DirectDeliverySortCollectWaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 直送分拣揽收消费
 *
 * @author hujiping
 * @date 2024/3/7 6:17 PM
 */
@Slf4j
@Service("directDeliverySortCollectConsumer")
public class DirectDeliverySortCollectConsumer extends MessageBaseConsumer {
    
    @Autowired
    private DirectDeliverySortCollectWaybillService directDeliverySortCollectWaybillService;

    @Override
    public void consume(Message message) throws Exception {

        CallerInfo info = Profiler.registerInfo("DirectDeliverySortCollectConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("直送分拣揽收消息非JSON格式，内容为:{}", message.getText());
                return;
            }
            DirectDeliverySortCollectMQ collectMQ = JsonHelper.fromJsonUseGson(message.getText(), DirectDeliverySortCollectMQ.class);
            if(collectMQ == null){
                log.warn("直送分拣揽收消息为空!message:{}", message.getText());
                return;
            }
            if(log.isInfoEnabled()){
                log.info("直送分拣揽收消息开始处理,单号:{},消息内容:{}", collectMQ.getPackageCode(), message.getText());
            }
            DirectDeliverySortCollectRequest collectRequest = DirectDeliverySortCollectRequest.builder()
                    .packOrWaybillCode(collectMQ.getPackageCode())
                    .operateSiteCode(collectMQ.getOperateSiteCode()).operateSiteName(collectMQ.getOperateSiteName())
                    .operateUserId(collectMQ.getOperateUserId()).operateUserName(collectMQ.getOperateUserName())
                    .weight(collectMQ.getWeight()).volume(collectMQ.getVolume())
                    .length(collectMQ.getLength()).width(collectMQ.getWidth()).high(collectMQ.getHigh())
                    .operateTime(collectMQ.getOperateTime())
                    .build();
            InvokeResult<Void> collectResult = directDeliverySortCollectWaybillService.directDeliverySortCollect(collectRequest);
            if(!collectResult.codeSuccess()){
                log.warn("直送分拣揽收包裹:{}揽收失败！失败原因:{}", "", collectResult.getMessage());
            }
            
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("直送分拣揽收消息处理失败, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
