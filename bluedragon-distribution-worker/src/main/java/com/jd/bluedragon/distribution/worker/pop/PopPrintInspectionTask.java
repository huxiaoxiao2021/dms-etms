package com.jd.bluedragon.distribution.worker.pop;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tangchunqing
 * @Description: 平台打印，未收货的补验货任务
 * @date 2018年05月21日 21时:05分
 */
public class PopPrintInspectionTask extends DBSingleScheduler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private InspectionService inspectionService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            this.log.info("task id&type is {}&{}" , task.getId(),task.getType());
            this.inspectionService.popPrintInspection(task,ownSign);
        } catch (Exception e) {
            this.log.error("平台打印补验货数据异常，task id is {}" , task.getId(), e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }



}
