package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.inspection.JyTrustHandoverAutoInspectionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 围栏到车包裹重试自动验货
 * @Author zhengchengfa
 * @Date 2024/3/1 20:45
 * @Description
 */
@Service
public class JyArriveCarPackageRetryAutoInspectionConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(JyArriveCarPackageRetryAutoInspectionConsumer.class);

    @Autowired
    private JyTrustHandoverAutoInspectionService jyTrustHandoverAutoInspectionService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    private void logInfo(String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyArriveCarPackageRetryAutoInspectionConsume.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyArriveCarPackageRetryAutoInspectionConsume consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyArriveCarPackageRetryAutoInspectionConsume consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        PackageArriveAutoInspectionDto mqBody = JsonHelper.fromJson(message.getText(), PackageArriveAutoInspectionDto.class);
        if(mqBody == null){
            logger.error("JyArriveCarPackageRetryAutoInspectionConsume consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }

        logInfo("围栏到车包裹重试自动验货消息开始消费，mqBody={}", message.getText());
        try{
            jyTrustHandoverAutoInspectionService.packageArriveCarAutoInspection(mqBody);
        }catch (Exception ex) {
            logger.error("围栏到车包裹重试自动验货消息消费异常，businessId={},errMsg={},content={}");
            throw new JyBizException("围栏到车包裹重试自动验货消息消费异常,businessId=" + message.getBusinessId());
        }
        logInfo("围栏到车包裹重试自动验货消息消费成功，内容{}", message.getText());
    }
}
