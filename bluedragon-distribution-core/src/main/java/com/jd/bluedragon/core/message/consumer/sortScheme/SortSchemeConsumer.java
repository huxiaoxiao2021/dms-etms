package com.jd.bluedragon.core.message.consumer.sortScheme;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.domain.DmsSortSchemeRouter;
import com.jd.bluedragon.core.base.DtcDataReceiverManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeSyncService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 分拣方案MQ消费
 * Created by wuzuxiang on 2017/1/13.
 */
@Service("sortSchemeConsumer")
public class SortSchemeConsumer extends MessageBaseConsumer{
    private static final Log logger = LogFactory.getLog(SortSchemeConsumer.class);

    private static List<String> stores = Lists.newArrayList("6,6,51", "6,6,80", "6,010,002","6,1,2");

    @Autowired
    SortSchemeSyncService sortSchemeSyncService;

    @Autowired
    DtcDataReceiverManager dtcDataReceiverManager;

    @Override
    public void consume(Message message) throws Exception {
        if(!JsonHelper.isJsonString(message.getText())){
            if(this.logger.isDebugEnabled()){
                logger.warn(MessageFormat.format("分拣方案推送DTC消息——非JSON格式，消息体内容为{0}",message.getText()));
            }
            return;
        }
        Map schemeMq = JsonHelper.json2Map(message.getText());
        DmsSortSchemeRouter dmsSortScheme = JsonHelper.fromJson(schemeMq.get("messageValue").toString(),DmsSortSchemeRouter.class);
        if(null == dmsSortScheme.getType() && !"SortScheme".equalsIgnoreCase(dmsSortScheme.getType()) && !"SortSchemeDetail".equalsIgnoreCase(dmsSortScheme.getType())){
            logger.info(MessageFormat.format("分拣中心推送DTC分拣方案消息抛弃，内容为：{0}",dmsSortScheme.toString()));
            return;
        }

//        Map map = JsonHelper.json2Map(dmsSortScheme.getBody());
        if (schemeMq != null && schemeMq.get("target") != null ) {

            String target = schemeMq.get("target").toString();

//            if (stores.contains(target)) {
                String methodName = schemeMq.get("methodName").toString();
                String messageValue = schemeMq.get("messageValue").toString();
                String outboundNo = schemeMq.get("outboundNo").toString();
                String outboundType = schemeMq.get("outboundType").toString();
                String source = schemeMq.get("source").toString();

                com.jd.staig.receiver.rpc.Result result = this.dtcDataReceiverManager.downStreamHandle(target,outboundType, messageValue, source, outboundNo);

                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultCode()=" + result.getResultCode());
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultMessage()=" + result.getResultMessage());
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultValue()=" + result.getResultValue());
                if (result.getResultCode()!= 1) {
                    this.logger.error("[分拣中心分拣方案推送DTC]消息失败，消息体为" + messageValue);
                }
//            }else{
//                this.logger.info("分拣方案推送失败：对应的target信息不符"+target+"{"+ stores +"}");
//            }
        }
    }

}
