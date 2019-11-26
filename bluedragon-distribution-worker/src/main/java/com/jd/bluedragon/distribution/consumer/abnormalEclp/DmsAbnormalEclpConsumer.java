package com.jd.bluedragon.distribution.consumer.abnormalEclp;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpCondition;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpResponse;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DmsAbnormalEclpConsumer
 *
 * @author tangchunqing
 * @ClassName: DmsAbnormalEclpConsumer
 * @Description: 处理外呼返回的结果
 * @date 2018年04月12日 21时:09分
 */
@Service("dmsAbnormalEclpConsumer")
public class DmsAbnormalEclpConsumer extends MessageBaseConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DmsAbnormalEclpService dmsAbnormalEclpService;

    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        this.log.debug("DmsAbnormalEclpConsumer consume --> 消息Body为【{}】",message.getText());
        if (message == null || "".equals(message.getText()) || null == message.getText()) {
            this.log.warn("DmsAbnormalEclpConsumer consume -->消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("DmsAbnormalEclpConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        DmsAbnormalEclpResponse dmsAbnormalEclpResponse = JsonHelper.fromJson(message.getText(), DmsAbnormalEclpResponse.class);
        if (dmsAbnormalEclpResponse == null) {
            this.log.warn("DmsAbnormalEclpConsumer consume -->消息转换对象失败：{}" , message.getText());
            return;
        }
        if (dmsAbnormalEclpResponse.getWaybillCode() == null) {
            this.log.warn("DmsAbnormalEclpConsumer consume : 运单号为空！消息：{}" , message.getText());
            return;
        }
        //查出要回写的外呼申请
        DmsAbnormalEclpCondition condition = new DmsAbnormalEclpCondition();
        condition.setWaybillCode(dmsAbnormalEclpResponse.getWaybillCode());
        condition.setIsReceipt(DmsAbnormalEclp.DMSABNORMALECLP_RECEIPT_NO);
        PagerResult result = dmsAbnormalEclpService.queryByPagerCondition(condition);
        if (result.getTotal() == 0) {
            this.log.warn("DmsAbnormalEclpConsumer consume : 未查到外呼申请！运单号：{}" , dmsAbnormalEclpResponse.getWaybillCode());
            return;
        }
        List<DmsAbnormalEclp> dmsAbnormalEclps = result.getRows();
        for (DmsAbnormalEclp dmsAbnormalEclp : dmsAbnormalEclps) {
            try {
                dmsAbnormalEclp.setReceiptMark(dmsAbnormalEclpResponse.getReceiptMark());
                if (StringHelper.isNotEmpty(dmsAbnormalEclpResponse.getReceiptTime())) {
                    dmsAbnormalEclp.setReceiptTime(DateHelper.parseDate(dmsAbnormalEclpResponse.getReceiptTime(), Constants.DATE_TIME_FORMAT));
                }
                dmsAbnormalEclp.setReceiptValue(dmsAbnormalEclpResponse.getReceiptValue());
                dmsAbnormalEclpService.updateResult(dmsAbnormalEclp);
            } catch (Exception e) {
                log.error("DmsAbnormalEclpConsumer consume :保存失败：{}" , JsonHelper.toJson(dmsAbnormalEclp), e);
            }
        }
    }

}
