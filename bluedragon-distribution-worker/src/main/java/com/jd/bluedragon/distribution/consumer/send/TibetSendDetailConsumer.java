package com.jd.bluedragon.distribution.consumer.send;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.itms.constants.ItmsConstants;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsPackageDetail;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsResponse;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsSendDetailDto;
import com.jd.bluedragon.external.crossbow.itms.service.TibetBizService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

/**
 * @ClassName TibetSendDetailConsumer
 * @Description 西藏项目给ITMS系统推送发货数据
 * @Author wyh
 * @Date 2021/6/4 13:56
 **/
@Service("tibetSendDetailConsumer")
public class TibetSendDetailConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(TibetSendDetailConsumer.class);

    @Autowired
    private TibetBizService tibetBizService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("TibetSendDetailConsumer消费[dmsWorkSendDetail消费]MQ-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        String umpKey = Constants.UMP_APP_NAME_DMSWEB + ".TibetSendDetailConsumer.consume";
        CallerInfo umpMonitor = ProfilerHelper.registerInfo(umpKey, Constants.UMP_APP_NAME_DMSWORKER);
        try {
            SendDetailMessage sendDetail = JSON.parseObject(message.getText(), SendDetailMessage.class);

            if (!SerialRuleUtil.isWaybillOrPackageNo(sendDetail.getPackageBarcode())) {
                return;
            }

            // 去除备件库条码
            if (WaybillUtil.isReverseSpareCode(sendDetail.getPackageBarcode())) {
                return;
            }

            // 开启西藏模式，给ITMS系统下发发货数据
            boolean tibetMode = tibetBizService.tibetModeSwitch(sendDetail.getCreateSiteCode(), sendDetail.getReceiveSiteCode());
            if (tibetMode) {

                sendDataToItms(sendDetail);
            }
        }
        catch (Exception ex) {
            Profiler.functionError(umpMonitor);
            log.error("TibetSendDetailConsumer消费失败. message:{}", message.getBusinessId(), ex);
            throw ex;
        }
        finally {
            Profiler.registerInfoEnd(umpMonitor);
        }
    }

    /**
     *
     * @param sendDetail
     */
    private void sendDataToItms(SendDetailMessage sendDetail) {
        ItmsSendDetailDto sendDetailDto = new ItmsSendDetailDto();
        sendDetailDto.setSource(ItmsConstants.sourceDms);
        sendDetailDto.setReceiptCode(sendDetail.getSendCode());
        sendDetailDto.setFromLocationId(sendDetail.getCreateSiteCode() + "");
        sendDetailDto.setToLocationId(sendDetail.getReceiveSiteCode() + "");

        BaseStaffSiteOrgDto createSite = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
        BaseStaffSiteOrgDto receiveSite = baseMajorManager.getBaseSiteBySiteId(sendDetail.getReceiveSiteCode());

        sendDetailDto.setFromLocationIdName(createSite.getSiteName());
        sendDetailDto.setToLocationIdName(receiveSite.getSiteName());
        sendDetailDto.setOpeTime(DateHelper.formatDateTime(new Date(sendDetail.getOperateTime())));

        ItmsPackageDetail packageDetail = new ItmsPackageDetail();
        packageDetail.setPackageCode(sendDetail.getPackageBarcode());
        sendDetailDto.setPackageDetailList(Collections.singletonList(packageDetail));

        ItmsResponse response = tibetBizService.downSendDataToItms(sendDetailDto);

        if (log.isInfoEnabled()) {
            log.info("西藏模式给ITMS下发发货数据. data:{}, response:{}", JsonHelper.toJson(sendDetailDto), JsonHelper.toJson(response));
        }

        if (!response.success()) {
            throw new RuntimeException();
        }
    }

}
