package com.jd.bluedragon.distribution.worker.globaltrade;
import com.jd.bluedragon.distribution.framework.AbstractDBSingleScheduler;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2015/8/21
 */
public class PreLoadBillTask extends AbstractDBSingleScheduler {

    private static final Log logger = LogFactory.getLog(PreLoadBillTask.class);

    @Autowired
    private LoadBillService loadBillService;

    @Override
    protected TaskResult executeExtendSingleTask(Task task, String ownSign) throws Exception {
        logger.info("处理全球购预装载Task[" + task.getId() + "]");
        return loadBillService.dealPreLoadBillTask(task);
    }
}
