package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

/**
 * 验货操作日志写入
 * Created by wangtingwei on 2017/1/17.
 */
public class InspectionLogHook implements TaskHook<InspectionTaskExecuteContext> {

    @Autowired
    private OperationLogService operationLogService;

    @Resource( name = "storeIdSet")
    private Set<Integer> storeIdSet;

    @Override
    @JProfiler( jKey = "dmsworker.InspectionLogHook.hook")
    public int hook(InspectionTaskExecuteContext context) {
        for (Inspection inspection:context.getInspectionList()) {
            OperationLog operationLog = new OperationLog();
            operationLog.setBoxCode(inspection.getBoxCode());
            operationLog.setCreateSiteCode(inspection.getCreateSiteCode());
            operationLog.setCreateUser(inspection.getCreateUser());
            operationLog.setCreateUserCode(inspection.getCreateUserCode());
            if ((Constants.BUSSINESS_TYPE_TRANSFER == inspection.getInspectionType()) || storeIdSet.contains(inspection.getInspectionType())) {
                operationLog.setLogType(OperationLog.LOG_TYPE_TRANSFER);
            } else {
                operationLog.setLogType(OperationLog.LOG_TYPE_INSPECTION);
            }
            operationLog
                    .setOperateTime(inspection.getOperateTime() == null ? new Date()
                            : inspection.getOperateTime());
            operationLog.setPackageCode(inspection.getPackageBarcode());
            operationLog.setReceiveSiteCode(inspection.getReceiveSiteCode());
            operationLog.setWaybillCode(inspection.getWaybillCode());
            operationLog.setMethodName("InspectionLogHook#hook");
            operationLogService.add(operationLog);
        }
        return 0;
    }
}
