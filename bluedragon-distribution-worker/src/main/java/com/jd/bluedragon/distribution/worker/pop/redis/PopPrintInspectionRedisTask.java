package com.jd.bluedragon.distribution.worker.pop.redis;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tangchunqing
 * @Description: 平台打印，未收货的补验货任务
 * @date 2018年05月22日 15时:26分
 */
public class PopPrintInspectionRedisTask extends RedisSingleScheduler {
    @Autowired
    private InspectionService inspectionService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            this.log.info("task id&type is {}&{}",task.getId(),task.getType());
            this.inspectionService.popPrintInspection(task,ownSign);
        } catch (Exception e) {
            this.log.error("平台打印补验货数据异常，Redis task id is {}" , task.getId(), e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
