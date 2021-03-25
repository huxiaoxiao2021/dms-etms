package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.ump.UmpMonitorHandler;
import com.jd.bluedragon.utils.ump.UmpMonitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 按运单发货 分页任务
 * 基类中 按 tye- keyType查询 task
 */
public class WaybillSendDeliveryTask extends SendDBSingleScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaybillSendDeliveryTask.class);
    @Autowired
    private DeliveryService deliveryService;

    @Override
    public boolean executeSingleTask(final Task task, String ownSign) throws Exception {
        try {

            String umpKey = "DmsWorker.Task.WaybillSendDeliveryTask.execute";
            String umpApp = Constants.UMP_APP_NAME_DMSWORKER;
            UmpMonitorHelper.doWithUmpMonitor(umpKey, umpApp, new UmpMonitorHandler() {
                @Override
                public void process() {
                    //调用按运单发货分页处理逻辑
                    deliveryService.doWaybillSendDelivery(task);
                }
            });
        }
        catch (Exception ex) {
            LOGGER.error("按运单发货任务执行失败, task : {}. 异常: ", task.getBody() , ex);
            return false;
        }
        return true;
    }

}
