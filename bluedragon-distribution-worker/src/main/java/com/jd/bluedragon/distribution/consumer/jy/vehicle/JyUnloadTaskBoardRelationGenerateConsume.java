package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanPackageDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadVehicleCheckTysService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/

@Service("jyUnloadTaskBoardRelationGenerateConsume")
public class JyUnloadTaskBoardRelationGenerateConsume extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InitUnloadVehicleConsumer.class);
    @Autowired
    private JyUnloadVehicleCheckTysService jyUnloadVehicleCheckTysService;

    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyUnloadTaskBoardRelationGenerateConsume consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyUnloadTaskBoardRelationGenerateConsume consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        ScanPackageDto mqDto = JsonHelper.fromJson(message.getText(), ScanPackageDto.class);
        if(logger.isInfoEnabled()) {
            logger.info("JyUnloadTaskBoardRelationGenerateConsume--消息来了-msg={}", message.getText());
        }
        InvokeResult<Void> InvokeResultRes = jyUnloadVehicleCheckTysService.saveUnloadVehicleBoardHandler(mqDto);
        if(InvokeResult.RESULT_SUCCESS_CODE != InvokeResultRes.getCode()) {
            throw new JyBizException("获取锁失败或获取锁服务异常");
        }
    }
}
