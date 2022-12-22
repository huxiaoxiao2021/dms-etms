package com.jd.bluedragon.distribution.consumer.weight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;
import com.jd.bluedragon.distribution.jy.service.calibrate.JyWeightVolumeCalibrateService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * dws设备校准推送咚咚消费
 *
 * @author hujiping
 * @date 2022/12/22 12:49 PM
 */
@Service("dwsCalibratePushDDConsumer")
public class DwsCalibratePushDDConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(DwsCalibratePushDDConsumer.class);

    @Autowired
    private JyWeightVolumeCalibrateService jyWeightVolumeCalibrateService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DwsCalibratePushDDConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("dws设备校准推送咚咚消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyBizTaskMachineCalibrateDetailEntity machineCalibrateDetail = JsonHelper.fromJsonUseGson(message.getText(), JyBizTaskMachineCalibrateDetailEntity.class);
            if(machineCalibrateDetail == null) {
                logger.warn("dws设备校准推送咚咚消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            // 推送咚咚
            jyWeightVolumeCalibrateService.noticeToDD(machineCalibrateDetail);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("dws设备校准推送咚咚处理异常, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
