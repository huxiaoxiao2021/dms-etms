package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.finance.service.DataToFinance;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xumei3 on 2017/6/12.
 */
public class DeliveryToFinanceTask extends DBSingleScheduler {
    private static final Log logger= LogFactory.getLog(com.jd.bluedragon.distribution.worker.departure.DepartureToFinanceTask.class);

    @Autowired
    private DataToFinance dataToFinanceService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            logger.info("task id is " + task.getId()+"task type is"+task.getType());
            result = dataToFinanceService.delivery2Finance(task);
        } catch (Exception e) {
            logger.error("task id is" + task.getId()+"task type is"+task.getType());
            logger.error("第三方发货数据推送jmq失败，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return result;
    }
}
