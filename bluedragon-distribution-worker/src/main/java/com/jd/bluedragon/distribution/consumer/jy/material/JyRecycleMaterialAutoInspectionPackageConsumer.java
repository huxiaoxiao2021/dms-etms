package com.jd.bluedragon.distribution.consumer.jy.material;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionPackageDto;
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
 * 物资循环进场自动验货包裹维度处理
 * @Author zhengchengfa
 * @Date 2024/2/29 14:58
 * @Description
 */
@Service
public class JyRecycleMaterialAutoInspectionPackageConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(JyRecycleMaterialAutoInspectionPackageConsumer.class);


    @Autowired
    private JyTrustHandoverAutoInspectionService jyTrustHandoverAutoInspectionService;


    private void logInfo(String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyRecycleMaterialAutoInspectionPackageConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyRecycleMaterialAutoInspectionPackageConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyRecycleMaterialAutoInspectionPackageConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        RecycleMaterialAutoInspectionPackageDto mqBody = JsonHelper.fromJson(message.getText(), RecycleMaterialAutoInspectionPackageDto.class);
        if(mqBody == null){
            logger.error("JyRecycleMaterialAutoInspectionPackageConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }

        logInfo("物资循环进场自动验货包裹维度处理开始消费，mqBody={}", message.getText());
        try{
            jyTrustHandoverAutoInspectionService.recycleMaterialPackageAutoInspection(mqBody);
        }catch (Exception ex) {
            logger.error("物资循环进场自动验货包裹维度处理消费异常，businessId={},errMsg={},content={}");
            throw new JyBizException("物资循环进场自动验货包裹维度处理消息消费异常,businessId=" + message.getBusinessId());
        }
        logInfo("物资循环进场自动验货包裹维度处理消息消费成功，内容{}", message.getText());

    }

}
