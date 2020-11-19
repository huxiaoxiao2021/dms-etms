package com.jd.bluedragon.distribution.consumer.inspection;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.inspection.domain.InspectionPackageMQ;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ump.UmpMonitorHandler;
import com.jd.bluedragon.utils.ump.UmpMonitorHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

/**
 * @ClassName InspectionPackageConsumer
 * @Description
 * @Author wyh
 * @Date 2020/11/10 22:49
 **/
@Service("inspectionPackageConsumer")
public class InspectionPackageConsumer extends MessageBaseConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspectionPackageConsumer.class);

    @Autowired
    private InspectionService inspectionService;

    @Override
    public void consume(Message message) throws Exception {

        try {
            // TODO 保留毫秒时间
            final InspectionPackageMQ packageBody = JsonHelper.fromJsonMs(message.getText(), InspectionPackageMQ.class);

            String umpKey = "DmsWorker.jmq.consumer.InspectionPackageConsumer.consume";

            UmpMonitorHelper.doWithUmpMonitor(umpKey, Constants.UMP_APP_NAME_DMSWORKER, new UmpMonitorHandler() {
                @Override
                public void process() {

                    inspectionService.doPackageInspection(packageBody);

                }
            });
        }
        catch (Exception ex) {
            LOGGER.error("消费包裹验货消息失败. bizId:{}", message.getBusinessId(), ex);
        }

    }
}
