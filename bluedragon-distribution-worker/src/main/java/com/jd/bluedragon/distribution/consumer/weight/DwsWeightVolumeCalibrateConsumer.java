package com.jd.bluedragon.distribution.consumer.weight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.jy.service.calibrate.JyWeightVolumeCalibrateService;
import com.jd.bluedragon.distribution.spotcheck.domain.DwsAIDistinguishNotifyMQ;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * dws称重量方校准消息处理
 *
 * @author hujiping
 * @date 2022/12/7 9:16 PM
 */
@Service("dwsWeightVolumeCalibrateConsumer")
public class DwsWeightVolumeCalibrateConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(DwsWeightVolumeCalibrateConsumer.class);

    @Autowired
    private JyWeightVolumeCalibrateService jyWeightVolumeCalibrateService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DwsWeightVolumeCalibrateConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("dws称重量方校准消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            DwsMachineCalibrateMQ dwsMachineCalibrateMQ = JsonHelper.fromJsonUseGson(message.getText(), DwsMachineCalibrateMQ.class);
            if(dwsMachineCalibrateMQ == null) {
                logger.warn("dws称重量方校准消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            if(StringUtils.isEmpty(dwsMachineCalibrateMQ.getMachineCode())
                    || dwsMachineCalibrateMQ.getCalibrateStatus() == null
                    || dwsMachineCalibrateMQ.getMachineStatus() == null
                    || dwsMachineCalibrateMQ.getPreviousMachineEligibleTime() == null
                    || dwsMachineCalibrateMQ.getCalibrateTime() == null){
                logger.warn("dws称重量方校准消息体缺少必要参数，内容为【{}】", message.getText());
                return;
            }
            // 根据设备校准数据更新处理设备校准任务
            jyWeightVolumeCalibrateService.dealCalibrateTask(dwsMachineCalibrateMQ);

            // 抽检数据下发处理
            spotCheckDealService.dealSpotCheckWithDwsCalibrateData(dwsMachineCalibrateMQ);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("dws称重量方校准失败, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
