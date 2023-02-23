package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.popPrint.dto.PushPrintRecordDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/2/20 14:14
 * @Description:
 */
@Service("changeOrderPrintSecondConsumer")
public class ChangeOrderPrintSecondConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ChangeOrderPrintSecondConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        logger.info("ChangeOrderPrintSecondConsumer -message {}", message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("ChangeOrderPrintSecondConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("ChangeOrderPrintSecondConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        PushPrintRecordDto printRecordDto = JsonHelper.fromJson(message.getText(), PushPrintRecordDto.class);
        JyExceptionPrintDto dto = getJyExceptionPrintDto(printRecordDto);
        jyExceptionService.printSuccess(dto);
    }

    /**
     * 获取三无单换单打印实体
     *
     * @return
     */
    private JyExceptionPrintDto getJyExceptionPrintDto(PushPrintRecordDto printRecordDto) {
        JyExceptionPrintDto dto = new JyExceptionPrintDto();
        dto.setOperateType(printRecordDto.getOperateType());
        dto.setSiteCode(printRecordDto.getSiteCode());
        dto.setPackageCode(printRecordDto.getPackageCode());
        return dto;
    }
}
