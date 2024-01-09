package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.CalculateWaitPickingItemNumDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingTaskAggsService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:36
 * @Description
 */
@Service("JyPickingGoodWaitScanItemNumUpdateConsumer")
public class JyPickingGoodWaitScanItemNumUpdateConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyPickingGoodWaitScanItemNumUpdateConsumer.class);


    @Autowired
    private JyPickingTaskAggsService jyPickingTaskAggsService;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyPickingGoodWaitScanItemNumUpdateConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logWarn("JyPickingGoodWaitScanItemNumUpdateConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyPickingGoodWaitScanItemNumUpdateConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        CalculateWaitPickingItemNumDto mqBody = JsonHelper.fromJson(message.getText(), CalculateWaitPickingItemNumDto.class);
        if(Objects.isNull(mqBody)){
            log.error("JyPickingGoodWaitScanItemNumUpdateConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        logInfo("航空提货待提明细件数消费开始，businessId={}, 内容{}", message.getBusinessId(), message.getText());
        try{

            if(!Boolean.TRUE.equals(mqBody.getCalculateNextSiteAggFlag())) {
                //bizId维度待扫字段更新
                jyPickingTaskAggsService.updatePickingAggWaitScanItemNum(mqBody);

            }else {
                //bizId+流向维度待扫更新
                jyPickingTaskAggsService.updatePickingSendAggWaitScanItemNum(mqBody);
            }
            logInfo("航空提货待提明细件数消费结束，businessId={}，任务BizId={}", message.getBusinessId(), mqBody.getBizId());
        }catch (Exception ex) {
            log.error("航空提货待提明细件数消费异常,businessId={},mqBody={}", message.getBusinessId(), message.getText());
            throw new JyBizException(String.format("航空提货待提明细件数消费异常,businessId：%s", message.getBusinessId()));
        }

    }

}
