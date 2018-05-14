package com.jd.bluedragon.distribution.consumer.abnormalEclp;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillResponse;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 三无寄托物核实
 * @date 2018年05月13日 16时:17分
 */
@Service("dmsAbnormalUnknownConsumer")
public class dmsAbnormalUnknownConsumer extends MessageBaseConsumer {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private AbnormalUnknownWaybillService abnormalUnknownWaybillService;

    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        this.logger.debug("dmsAbnormalUnknownConsumer consume --> 消息Body为【"
                + message.getText() + "】");
        if (message == null || "".equals(message.getText()) || null == message.getText()) {
            this.logger.warn("dmsAbnormalUnknownConsumer consume -->消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("dmsAbnormalUnknownConsumer consume -->消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        AbnormalUnknownWaybillResponse abnormalUnknownWaybillResponse = JsonHelper.fromJson(message.getText(), AbnormalUnknownWaybillResponse.class);
        if (abnormalUnknownWaybillResponse == null) {
            this.logger.error("dmsAbnormalUnknownConsumer consume -->消息转换对象失败：" + message.getText());
            return;
        }
        if (abnormalUnknownWaybillResponse.getWaybillCode() == null) {
            this.logger.warn("dmsAbnormalUnknownConsumer consume : 运单号为空！");
            return;
        }
        if (StringUtils.isEmpty(abnormalUnknownWaybillResponse.getContent())) {
            this.logger.warn("dmsAbnormalUnknownConsumer consume : 回复内容不能为空！");
            return;
        }
        if (abnormalUnknownWaybillResponse.getReportNumber() == null) {
            this.logger.warn("dmsAbnormalUnknownConsumer consume : 回复次数不能为空！");
            return;
        }

        AbnormalUnknownWaybill abnormalUnknownWaybill = abnormalUnknownWaybillService.findLastReportByWaybillCode(abnormalUnknownWaybillResponse.getWaybillCode());
        if (abnormalUnknownWaybill == null) {
            logger.warn(message.getText() + "该运单还未上报过");
            return;
        }
        if (abnormalUnknownWaybill.getOrderNumber() == 0) {
            logger.warn(message.getText() + "已由系统回复，不允许B商家回复");
            return;
        }
        if (abnormalUnknownWaybill.getIsReceipt() == 1) {
            logger.warn(message.getText() + "不存在未回复的上报申请");
            return;
        }
        Date now = new Date();
        if (abnormalUnknownWaybill.getIsReceipt() == 0 && abnormalUnknownWaybill.getOrderNumber().equals(abnormalUnknownWaybillResponse.getReportNumber())) {
            abnormalUnknownWaybill.setReceiptTime(now);
            abnormalUnknownWaybill.setUpdateTime(now);
            abnormalUnknownWaybill.setIsReceipt(1);
            abnormalUnknownWaybill.setReceiptContent(abnormalUnknownWaybillResponse.getContent());
            try {
                abnormalUnknownWaybillService.updateReceive(abnormalUnknownWaybill);
            } catch (Exception e) {
                logger.error("dmsAbnormalUnknownConsumer回写失败", e);
            }
        }

    }
}
