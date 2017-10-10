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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log logger= LogFactory.getLog(TransbillMSyncConsumer.class);

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
        if(!transbillMService.saveOrUpdate(transbillM)){
        	logger.warn(MessageFormat.format("城配运单推送MQ-消息同步失败，内容为【{0}】", message.getText()));
        }
    }
}