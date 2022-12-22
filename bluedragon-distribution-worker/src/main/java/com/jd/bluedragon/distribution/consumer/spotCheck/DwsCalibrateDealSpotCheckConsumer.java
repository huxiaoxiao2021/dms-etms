package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * dws设备校准抽检处理消费
 *
 * @author hujiping
 * @date 2022/12/22 2:34 PM
 */
@Service("dwsCalibrateDealSpotCheckConsumer")
public class DwsCalibrateDealSpotCheckConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(DwsCalibrateDealSpotCheckConsumer.class);

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DwsCalibrateDealSpotCheck.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("dws设备校准抽检处理消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            WeightVolumeSpotCheckDto weightVolumeSpotCheckDto = JsonHelper.fromJsonUseGson(message.getText(), WeightVolumeSpotCheckDto.class);
            if(weightVolumeSpotCheckDto == null) {
                logger.warn("dws设备校准抽检处理消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            // 更新抽检状态
            WeightVolumeSpotCheckDto updateSpotDto = new WeightVolumeSpotCheckDto();
            updateSpotDto.setPackageCode(weightVolumeSpotCheckDto.getPackageCode());
            updateSpotDto.setReviewSiteCode(weightVolumeSpotCheckDto.getReviewSiteCode());
            updateSpotDto.setMachineStatus(weightVolumeSpotCheckDto.getMachineStatus());
            spotCheckQueryManager.insertOrUpdateSpotCheck(updateSpotDto);
            // 下发抽检数据
            if(spotCheckDealService.spotCheckIssueIsRelyOnMachineStatus(weightVolumeSpotCheckDto.getReviewSiteCode())
                    && Objects.equals(weightVolumeSpotCheckDto.getMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())){
                spotCheckDealService.executeIssue(weightVolumeSpotCheckDto);
            }

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("dws设备校准抽检处理处理异常, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
