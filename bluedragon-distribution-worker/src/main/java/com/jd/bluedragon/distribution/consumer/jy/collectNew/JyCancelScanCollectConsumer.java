package com.jd.bluedragon.distribution.consumer.jy.collectNew;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyCancelScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectNew.factory.JyScanCollectStrategyFactory;
import com.jd.bluedragon.distribution.jy.service.collectNew.strategy.JyScanCollectStrategy;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 19:59
 * @Description
 */

@Service("jyCancelScanCollectConsumer")
public class JyCancelScanCollectConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyCancelScanCollectConsumer.class);


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.jyCancelScanCollectConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("jyCancelScanCollectConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("jyCancelScanCollectConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyCancelScanCollectMqDto mqBody = JsonHelper.fromJson(message.getText(), JyCancelScanCollectMqDto.class);
        if(mqBody == null){
            log.error("jyCancelScanCollectConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("消费处理jyCancelScanCollectConsumer开始，内容{}",message.getText());
        }

        if(StringUtils.isBlank(mqBody.getJyPostType())) {
            return;
        }


        boolean consumeRes;
        try {
            if(JyPostEnum.SEND_SEAL_WAREHOUSE.getCode().equals(mqBody.getJyPostType())) {
                consumeRes = cancelScanCollectDeal(mqBody);

            }else {
                log.warn("拣运取消扫描处理集齐服务暂不支持接货仓发货岗之外的岗位取消集齐处理，当前岗位=【{}】，msg={}", JyPostEnum.getDescByCode(mqBody.getJyPostType()), message.getText());
                return;
            }
        } catch (Exception e) {
            log.error("jyCancelScanCollectConsumer.deal服务异常，businessId={}, errMsg={},内容{}", message.getBusinessId(), e.getMessage(), message.getText(), e);
            throw new JyBizException("jyCancelScanCollectConsumer拣运取消扫描处理集齐服务消费处理异常：" + message.getBusinessId());
        }
        if (!consumeRes) {
            //处理失败 重试
            log.error("jyCancelScanCollectConsumer失败，businessId={}, 内容{}", message.getBusinessId(), message.getText());
            throw new JyBizException("jyCancelScanCollectConsumer拣运取消扫描处理集齐服务消费处理失败" + message.getBusinessId());
        } else {
            if (log.isInfoEnabled()) {
                log.info("jyCancelScanCollectConsumer成功，businessId={}, 内容{}", message.getBusinessId(), message.getText());
            }
        }
    }


    /**
     * 处理逻辑
     * @param collectDto
     * @return
     */
    private boolean cancelScanCollectDeal(JyCancelScanCollectMqDto collectDto){
        JyScanCollectStrategy jyScanCollectStrategy = JyScanCollectStrategyFactory.getJyScanCollectService(collectDto.getJyPostType());
        return jyScanCollectStrategy.cancelScanCollectDeal(collectDto);
    }

}

