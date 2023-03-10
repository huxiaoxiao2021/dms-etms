package com.jd.bluedragon.distribution.consumer.jy.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.BatchUpdateCollectStatusDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectBatchUpdateTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 批量修改集齐状态
 */
@Service("jyCollectBatchUpdateStatusConsumer")
public class JyCollectBatchUpdateStatusConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyCollectBatchUpdateStatusConsumer.class);

    @Autowired
    private JyCollectService jyCollectService;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyCollectBatchUpdateStatusConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyCollectBatchUpdateStatusConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyCollectBatchUpdateStatusConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        BatchUpdateCollectStatusDto mqBody = JsonHelper.fromJson(message.getText(),BatchUpdateCollectStatusDto.class);
        if(mqBody == null){
            log.error("JyCollectBatchUpdateStatusConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("消费处理 jy_batch_update_collect_status 开始，内容{}",message.getText());
        }
        if(!deal(mqBody)){
            //处理失败 重试
            log.error("消费处理 jy_batch_update_collect_status 失败，内容{}",message.getText());
            throw new JyBizException("消费处理 jy_batch_update_collect_status 失败");
        }else{
            if(log.isInfoEnabled()) {
                log.info("消费处理 jy_batch_update_collect_status 成功，内容{}", message.getText());
            }
        }
    }

    /**
     * 处理逻辑
     * @param batchUpdateCollectStatusDto
     * @return
     */
    private boolean deal(BatchUpdateCollectStatusDto batchUpdateCollectStatusDto){

        if(batchUpdateCollectStatusDto.getBatchType() == CollectBatchUpdateTypeEnum.WAYBILL_BATCH.getCode()) {
            return this.waybillBatchUpdateCollectStatus(batchUpdateCollectStatusDto);
        }else {
            log.info("按批修改集齐状态，当前类型消息不做处理，丢掉");
        }
        return true;

    }

    /**
     * 运单维度批处理集齐状态更新
     * @param paramDto
     * @return
     */
    private boolean waybillBatchUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto) {
        InvokeResult invokeResult = jyCollectService.waybillBatchUpdateCollectStatus(paramDto);
        if(!invokeResult.codeSuccess()) {
            log.info("按运单批量修改运单集齐状态失败，param={},res={}", JsonUtils.toJSONString(paramDto), JsonUtils.toJSONString(invokeResult));
            return false;
        }
        return true;
    }



}
