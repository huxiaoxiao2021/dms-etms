package com.jd.bluedragon.distribution.consumer.send;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.DeliveryCancelSendMQBody;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.crossbow.itms.constants.ItmsConstants;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsCancelSendDto;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsPackageDetail;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsResponse;
import com.jd.bluedragon.external.crossbow.itms.service.TibetBizService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @ClassName TibetCancelSendDetailConsumer
 * @Description
 * @Author wyh
 * @Date 2021/6/7 21:43
 **/
@Service("tibetCancelSendDetailConsumer")
public class TibetCancelSendDetailConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(TibetCancelSendDetailConsumer.class);

    @Autowired
    private TibetBizService tibetBizService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("TibetCancelSendDetailConsumer-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        String umpKey = Constants.UMP_APP_NAME_DMSWEB + ".TibetCancelSendDetailConsumer.consume";
        CallerInfo umpMonitor = ProfilerHelper.registerInfo(umpKey, Constants.UMP_APP_NAME_DMSWORKER);

        try {
            DeliveryCancelSendMQBody sendMQBody = JSON.parseObject(message.getText(), DeliveryCancelSendMQBody.class);

            Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(sendMQBody.getSendCode());
            if (siteCodes[0] == -1 || siteCodes[1] == -1) {
                return;
            }
            // 开启西藏模式，给ITMS系统发送取消发货数据
            boolean tibetMode = tibetBizService.tibetModeSwitch(siteCodes[0], siteCodes[1]);
            if (tibetMode) {

                sendCancelDataToItms(sendMQBody, siteCodes);
            }
        }
        catch (Exception e) {
            Profiler.functionError(umpMonitor);
            log.error("TibetCancelSendDetailConsumer消费失败. message:{}", message.getBusinessId(), e);
        }
        finally {
            Profiler.registerInfoEnd(umpMonitor);
        }
    }

    /**
     *
     * @param sendMQBody
     * @param siteCodes
     */
    private void sendCancelDataToItms(DeliveryCancelSendMQBody sendMQBody, Integer[] siteCodes) {
        ItmsCancelSendDto cancelSendDto = new ItmsCancelSendDto();
        cancelSendDto.setSource(ItmsConstants.sourceDms);
        cancelSendDto.setReceiptCode(sendMQBody.getSendCode());
        cancelSendDto.setOpeTime(DateHelper.formatDateTime(sendMQBody.getOperateTime()));
        cancelSendDto.setOpeSiteId(siteCodes[0] + "");

        BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(siteCodes[0]);
        cancelSendDto.setOpeSiteName(operateSite.getSiteName());

        ItmsPackageDetail packageDetail = new ItmsPackageDetail();
        packageDetail.setPackageCode(sendMQBody.getPackageBarcode());
        cancelSendDto.setPackageDetailList(Collections.singletonList(packageDetail));

        ItmsResponse response = tibetBizService.downSendCancelDataToItms(cancelSendDto);

        if (log.isInfoEnabled()) {
            log.info("西藏模式给ITMS下发发货数据. data:{}, response:{}", JsonHelper.toJson(cancelSendDto), JsonHelper.toJson(response));
        }

        if (!response.success()) {
            throw new RuntimeException();
        }
    }

}
