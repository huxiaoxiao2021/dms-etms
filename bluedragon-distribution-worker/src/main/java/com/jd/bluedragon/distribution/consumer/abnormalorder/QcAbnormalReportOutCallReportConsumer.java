package com.jd.bluedragon.distribution.consumer.abnormalorder;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 质控异常提报mq消费
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-02-16 17:05:19 周三
 */
@Service("qcAbnormalReportOutCallReportConsumer")
public class QcAbnormalReportOutCallReportConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(QcAbnormalReportOutCallReportConsumer.class);

    @Autowired
    private QualityControlService qualityControlService;

    @Override
    public void consume(Message message) throws Exception {
        QcReportOutCallJmqDto qcReportJmqDto = JsonHelper.fromJson(message.getText(), QcReportOutCallJmqDto.class);
        if(qcReportJmqDto == null){
            log.warn("QcAbnormalReportOutCallReportConsumer 消息转换失败！[{}-{}]:[{}]", message.getTopic(), message.getBusinessId(), message.getText());
            return;
        }

        final Result<Boolean> result = qualityControlService.handleQcOutCallReportConsume(qcReportJmqDto);
        if(!result.isSuccess() || !result.getData()){
            log.error("QcAbnormalReportOutCallReportConsumer fail {}", JsonHelper.toJson(result));
            throw new RuntimeException("处理失败");
        }
    }

}
