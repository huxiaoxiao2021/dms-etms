package com.jd.bluedragon.distribution.consumer.urban;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.canal.CanalEvent;
import com.jd.bluedragon.utils.canal.CanalHelper;
import com.jd.bluedragon.utils.canal.DbOperation;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 城配运单MQ消费者
 * 
 * @ClassName: TransbillMSyncConsumer
 * @Description: (类描述信息)
 * @author wuyoude
 * @date 2017年5月2日 下午5:54:41
 *
 */
@Service("transbillMSyncConsumer")
public class TransbillMSyncConsumer extends MessageBaseConsumer{
    private static final Logger log = LoggerFactory.getLogger(TransbillMSyncConsumer.class);

    @Autowired
    private TransbillMService transbillMService;
    @Override
    @JProfiler(jKey = "DMSWEB.TransbillMSyncConsumer.consume", mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if(!JsonHelper.isJsonString(message.getText())){
        	// 消息格式错误，加入自定义告警
        	String profilerKey = "DMSWEB.TransbillMSyncConsumer.consume.badJsonMessage";
            Profiler.businessAlarm(profilerKey, MessageFormat.format("城配运单推送MQ-消息体非JSON格式，businessId为【{0}】", message.getBusinessId()));
            return;
        }
        CanalEvent<TransbillM> canalEvent = CanalHelper.parseCanalMsg(message.getText(), TransbillM.class);
        TransbillM transbillM = null;
        if(null != canalEvent){
        	if(DbOperation.INSERT.equals(canalEvent.getDbOperation()) || DbOperation.UPDATE.equals(canalEvent.getDbOperation())){
        		transbillM = canalEvent.getDataAfter();
        	}else if(DbOperation.DELETE.equals(canalEvent.getDbOperation())){
        		transbillM = canalEvent.getDataBefore();
        		if(transbillM!=null){
        			transbillM.setYn(0);
        		}
        	}
        }
        //wuyoude 2018-06-22：屏蔽调度单号字段落库
        if(transbillM != null){
        	transbillM.setScheduleBillCode(null);
        }
        if(!transbillMService.saveOrUpdate(transbillM)){
        	log.warn("城配运单推送MQ-消息同步失败，内容为【{}】", message.getText());
        }
    }
}