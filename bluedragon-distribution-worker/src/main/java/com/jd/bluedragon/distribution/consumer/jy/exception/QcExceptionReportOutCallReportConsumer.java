package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExpCustomerReturnMQ;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 质控异常上报消息消费
 *
 * @author 陈亚国
 * @date 2023/3/16 10:39 AM
 */
@Service("qcExceptionReportOutCallReportConsumer")
public class QcExceptionReportOutCallReportConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(QcExceptionReportOutCallReportConsumer.class);

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;
    
    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("QcExceptionReportOutCallReportConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            logger.info("质控异常上报消息回传消息体-{}",message.getText());
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("质控异常上报消息回传消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            QcReportJmqDto qcReportJmqDto = JsonHelper.fromJson(message.getText(), QcReportJmqDto.class);

            if(qcReportJmqDto == null){
                logger.warn("QcAbnormalReportOutCallReportConsumer 消息转换失败！[{}-{}]:[{}]", message.getTopic(), message.getBusinessId(), message.getText());
                return;
            }
            if (StringUtils.isBlank(qcReportJmqDto.getAbnormalDocumentNum()) || StringUtils.isBlank(qcReportJmqDto.getCreateDept())) {
                logger.warn("abnormalDocumentNum or createDept 为空！");
                return;
            }
            //处理异常破损数据
            jyDamageExceptionService.dealExpDamageInfoByAbnormalReportOutCall(qcReportJmqDto);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("质控异常上报消息传消息处理异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
