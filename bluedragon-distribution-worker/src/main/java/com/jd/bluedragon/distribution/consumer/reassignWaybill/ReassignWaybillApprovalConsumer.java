package com.jd.bluedragon.distribution.consumer.reassignWaybill;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumer.print.RecycleBasketAbolishConsumer;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybillApprovalRecordMQ;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishReq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/5 13:59
 * @Description: 返调度申请异步消费
 */
@Service("reassignWaybillApprovalConsumer")
public class ReassignWaybillApprovalConsumer  extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReassignWaybillApprovalConsumer.class);


    @Autowired
    private ReassignWaybillService reassignWaybillService;


    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("reassignWaybillApprovalConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);

        if(logger.isInfoEnabled()){
            logger.info("运单返调度审批 -{}", JSON.toJSONString(message.getText()));
        }
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("运单返调度审批消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            ReassignWaybillApprovalRecordMQ approvalRecordMQ = JsonHelper.fromJsonUseGson(message.getText(), ReassignWaybillApprovalRecordMQ.class);
            if(approvalRecordMQ == null || StringUtils.isBlank(approvalRecordMQ.getBarCode())) {
                logger.warn("运单返调度审批消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            reassignWaybillService.dealReassignWaybillApprove(approvalRecordMQ);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("运单返调度审批消费异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
