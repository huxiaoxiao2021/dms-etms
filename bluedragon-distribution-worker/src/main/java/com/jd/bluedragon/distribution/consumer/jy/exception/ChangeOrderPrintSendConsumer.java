package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.print.domain.ChangeOrderPrintMq;
import com.jd.bluedragon.distribution.print.domain.RePrintRecordMq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/2/20 14:14
 * @Description:
 */
@Service("changeOrderPrintSendConsumer")
public class ChangeOrderPrintSendConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ChangeOrderPrintSendConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        logger.info("ChangeOrderPrintSendConsumer -message {}", message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("ChangeOrderPrintSendConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("ChangeOrderPrintSendConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        ChangeOrderPrintMq changeOrderPrintMq = JsonHelper.fromJson(message.getText(), ChangeOrderPrintMq.class);
        //按包裹维度处理
        if(CollectionUtils.isNotEmpty(changeOrderPrintMq.getReprintPackages())){
            for (String packageCode: changeOrderPrintMq.getReprintPackages()) {
                JyExceptionPrintDto jyExceptionPrintDto = getJyExceptionPrintDto(changeOrderPrintMq, packageCode);
                jyExceptionService.printSuccess(jyExceptionPrintDto);
            }
        }
    }

    /**
     * 获取三无单换单打印实体
     *
     * @return
     */
    private JyExceptionPrintDto getJyExceptionPrintDto(ChangeOrderPrintMq mq, String packageCode) {
        JyExceptionPrintDto dto = new JyExceptionPrintDto();
        dto.setOperateType(mq.getOperateType());
        dto.setSiteCode(mq.getSiteCode());
        dto.setPackageCode(packageCode);
        return dto;
    }
}
