package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.finance.service.DataToFinance;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xumei3 on 2017/6/12.
 */
public class DeliveryToFinanceTask extends DBSingleScheduler {
    private static final Logger log = LoggerFactory.getLogger(com.jd.bluedragon.distribution.worker.delivery.DeliveryToFinanceTask.class);

    @Autowired
    private DataToFinance dataToFinanceService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;
        try {
            result = dataToFinanceService.delivery2Finance(task);
        } catch (Exception e) {
            log.error("第三方发货数据推送jmq失败，task id is {} task type is {}",task.getId(),task.getType(), e);
            return false;
        }
        return result;
    }
}
