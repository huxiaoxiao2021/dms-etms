package com.jd.bluedragon.distribution.consumer.sortScheme;

import com.jd.bluedragon.common.domain.DmsSortSchemeRouter;
import com.jd.bluedragon.core.base.DtcDataReceiverManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeSyncService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 分拣方案MQ消费
 * Created by wuzuxiang on 2017/1/13.
 */
public class SortSchemeConsumer extends MessageBaseConsumer{
    private static final Logger log = LoggerFactory.getLogger(SortSchemeConsumer.class);

    private List<String> stores ;

    @Autowired
    SortSchemeSyncService sortSchemeSyncService;

    @Autowired
    DtcDataReceiverManager dtcDataReceiverManager;

    @Override
    public void consume(Message message) throws Exception {
        if(!JsonHelper.isJsonString(message.getText())){
            log.warn("分拣方案推送DTC消息——非JSON格式，消息体内容为{}",message.getText());
            return;
        }
        Map schemeMq = JsonHelper.json2Map(message.getText());
        DmsSortSchemeRouter dmsSortScheme = JsonHelper.fromJson(schemeMq.get("messageValue").toString(),DmsSortSchemeRouter.class);
        if(null == dmsSortScheme.getType() && !"SortScheme".equalsIgnoreCase(dmsSortScheme.getType()) && !"SortSchemeDetail".equalsIgnoreCase(dmsSortScheme.getType())){
            log.warn("分拣中心推送DTC分拣方案消息抛弃，内容为：{}",dmsSortScheme.toString());
            return;
        }

//        Map map = JsonHelper.json2Map(dmsSortScheme.getBody());
        if (schemeMq != null && schemeMq.get("target") != null ) {

            String target = schemeMq.get("target").toString();

            if (stores.contains(target)) {
                String methodName = schemeMq.get("methodName").toString();
                String messageValue = schemeMq.get("messageValue").toString();
                String outboundNo = schemeMq.get("outboundNo").toString();
                String outboundType = schemeMq.get("outboundType").toString();
                String source = schemeMq.get("source").toString();

                try{
                    com.jd.staig.receiver.rpc.Result result = this.dtcDataReceiverManager.downStreamHandle(target,outboundType, messageValue, source, outboundNo);

                    this.log.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultCode()={}", result.getResultCode());
                    this.log.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultMessage()={}", result.getResultMessage());
                    this.log.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultValue()={}", result.getResultValue());
                    if (result.getResultCode()!= 1) {
                        this.log.warn("[分拣中心分拣方案推送DTC]消息失败，消息体为:{}", messageValue);
                    }
                }catch(Exception e){
                    this.log.error("[分拣中心分拣方案推送DTC]:接口访问异常，消息体为:{}", messageValue, e);
                }
            }else{
                this.log.warn("分拣方案推送失败：对应的target信息不符：{}-{}",target,stores );
            }
        }
    }

    public List<String> getStores() {
        return stores;
    }

    public void setStores(List<String> stores) {
        this.stores = stores;
    }
}
