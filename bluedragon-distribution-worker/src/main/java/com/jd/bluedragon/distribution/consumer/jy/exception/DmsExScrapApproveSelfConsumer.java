package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpScrappedDetailReq;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 异常-报废审批自消费处理（提交审批）
 *
 * @author hujiping
 * @date 2023/3/21 7:52 PM
 */
@Service("dmsExScrapApproveSelfConsumer")
public class DmsExScrapApproveSelfConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DmsExScrapApproveSelfConsumer.class);

    @Autowired
    private JyScrappedExceptionService jyScrappedExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DmsExScrapApproveSelfConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("异常-报废提交审批消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            ExpScrappedDetailReq expScrappedDetailReq = JsonHelper.fromJson(message.getText(), ExpScrappedDetailReq.class);
            if(expScrappedDetailReq == null || StringUtils.isEmpty(expScrappedDetailReq.getBizId())){
                logger.warn("异常-报废审批消息体异常，内容为【{}】", message.getText());
                return;
            }
            // 审批处理
            jyScrappedExceptionService.dealApprove(expScrappedDetailReq);
            
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("异常-报废提交审批处理异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}