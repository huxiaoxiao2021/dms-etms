package com.jd.bluedragon.distribution.worker.pop;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tangchunqing
 * @Description: 平台打印，未收货的补验货任务
 * @date 2018年05月21日 21时:05分
 */
public class PopPrintInspectionTask extends DBSingleScheduler {
    private final Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private InspectionService inspectionService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            this.logger.info("task id&type is " + task.getId()+"&"+task.getType());
            this.inspectionService.popPrintInspection(task,ownSign);
        } catch (Exception e) {
            this.logger.error("task id is" + task.getId());
            this.logger.error("平台打印补验货数据，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }



}
