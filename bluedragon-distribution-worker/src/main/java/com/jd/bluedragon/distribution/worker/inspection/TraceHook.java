package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 验货全程跟踪推送
 * Created by wangtingwei on 2017/1/17.
 */
public class TraceHook implements TaskHook<InspectionTaskExecuteContext> {

    private static final Log log= LogFactory.getLog(TraceHook.class);

    @Autowired
    private CenConfirmService cenConfirmService;

    @Autowired
    private TaskService taskService;

    @Override
    @JProfiler( jKey = "dmsworker.TraceHook.hook")
    public int hook(InspectionTaskExecuteContext context) {
        for (CenConfirm cenConfirm:context.getCenConfirmList()){
            BaseStaffSiteOrgDto bDto = context.getCreateSite();
            BaseStaffSiteOrgDto rDto = null;
            String message =cenConfirmService.getTipsMessage(cenConfirm);
            if ( Constants.BUSSINESS_TYPE_FC!=cenConfirm.getType().intValue()) {
                rDto = context.getReceiveSite();
            }
            if (bDto == null) {
                log.error("[PackageBarcode=" + cenConfirm.getPackageBarcode()
                        + "]根据[siteCode=" + cenConfirm.getCreateSiteCode()
                        + "]获取基础资料站点信息[getSiteBySiteID]返回null,不再插入"+message);
            } else {
                WaybillStatus tWaybillStatus =cenConfirmService.createWaybillStatus(cenConfirm,
                        bDto, rDto);
                if (cenConfirmService.checkFormat(tWaybillStatus, cenConfirm.getType())) {
                    // 添加到task表
                    taskService.add(cenConfirmService.toTask(tWaybillStatus, cenConfirm.getOperateType()));
                } else {
                    log.error("[PackageCode=" + tWaybillStatus.getPackageCode()
                            + " WaybillCode=" + tWaybillStatus.getWaybillCode()
                            + "][参数信息不全],不再插入"+message);
                }

            }
        }
        return 0;
    }
}
