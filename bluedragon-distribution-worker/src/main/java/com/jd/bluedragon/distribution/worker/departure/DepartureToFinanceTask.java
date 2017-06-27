package com.jd.bluedragon.distribution.worker.departure;

import com.jd.bluedragon.distribution.finance.service.DataToFinance;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 非转车（支线）&&承运人为第三方的发车数据推送财务
 * Created by xumei3 on 2017/6/12.
 */
public class DepartureToFinanceTask extends DBSingleScheduler {
    private static final Log logger= LogFactory.getLog(DepartureToFinanceTask.class);

    @Autowired
    private DataToFinance dataToFinance;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            logger.info("task id is " + task.getId()+"task type is"+task.getType());
            result = dataToFinance.departure2Finance(task);
        } catch (Exception e) {
            logger.error("task id is" + task.getId()+"task type is"+task.getType());
            logger.error("第三方发车数据推送jmq失败，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return result;
    }
}
