package com.jd.bluedragon.distribution.consumer.abnormalEclp;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpCondition;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpResponse;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    private final Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private DmsAbnormalEclpService dmsAbnormalEclpService;

    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        this.logger.info("DmsAbnormalEclpConsumer consume --> 消息Body为【"
                + message.getText() + "】");

        DmsAbnormalEclpResponse dmsAbnormalEclpResponse = JsonHelper.fromJson(message.getText(), DmsAbnormalEclpResponse.class);
        if (dmsAbnormalEclpResponse.getWaybillCode() == null) {
            this.logger.warn("DmsAbnormalEclpConsumer consume : 运单号为空！");
            return;
        }
        DmsAbnormalEclpCondition condition = new DmsAbnormalEclpCondition();
        condition.setWaybillCode(dmsAbnormalEclpResponse.getWaybillCode());
        condition.setIsReceipt(0);
        PagerResult result = dmsAbnormalEclpService.queryByPagerCondition(condition);
        if (result.getTotal() == 0) {
            this.logger.warn("DmsAbnormalEclpConsumer consume : 未查到外呼申请！运单号：" + dmsAbnormalEclpResponse.getWaybillCode());
            return;
        }
        List<DmsAbnormalEclp> dmsAbnormalEclps = result.getRows();
        for (DmsAbnormalEclp dmsAbnormalEclp : dmsAbnormalEclps) {
            try {
                dmsAbnormalEclp.setReceiptMark(dmsAbnormalEclpResponse.getReceiptMark());
                if (StringHelper.isNotEmpty(dmsAbnormalEclpResponse.getReceiptTime())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dmsAbnormalEclp.setReceiptTime(sdf.parse(dmsAbnormalEclpResponse.getReceiptTime()));
                }
                dmsAbnormalEclp.setReceiptValue(dmsAbnormalEclpResponse.getReceiptValue());
                dmsAbnormalEclpService.updateResult(dmsAbnormalEclp);
            } catch (Exception e) {
                logger.error("DmsAbnormalEclpConsumer consume :保存失败：" + JsonHelper.toJson(dmsAbnormalEclp), e);
            }
        }
    }

}
