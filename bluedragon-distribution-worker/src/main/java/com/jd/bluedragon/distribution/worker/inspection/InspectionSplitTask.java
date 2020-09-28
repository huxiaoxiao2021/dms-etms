package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ump.UmpMonitorHandler;
import com.jd.bluedragon.utils.ump.UmpMonitorHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * @ClassName InspectionSplitTask
 * @Description
 * @Author wyh
 * @Date 2020/9/21 15:20
 **/
public class InspectionSplitTask extends DBSingleScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspectionSplitTask.class);

    @Autowired
    private InspectionTaskExeStrategy inspectionTaskExeStrategy;

    @Override
    protected boolean executeSingleTask(final Task task, String ownSign) throws Exception {
        try {

            String umpKey = "DmsWorker.Task.InspectionSplitTask.execute";
            String umpApp = Constants.UMP_APP_NAME_DMSWORKER;
            UmpMonitorHelper.doWithUmpMonitor(umpKey, umpApp, new UmpMonitorHandler() {
                @Override
                public void process() {
                    InspectionRequest request = JsonHelper.fromJsonUseGson(task.getBody(), InspectionRequest.class);
                    inspectionTaskExeStrategy.decideExecutor(request).process(request);
                }
            });
        }
        catch (Exception ex) {
            LOGGER.error("验货拆分任务执行失败, task : {}. 异常: ", task.getBody() , ex);
            return false;
        }

        return true;
    }
}
