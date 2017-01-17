package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 推送验货MQ消息
 * Created by wangtingwei on 2017/1/17.
 */
public class PushMessageHook implements TaskHook<InspectionTaskExecuteContext> {

    private static  final Log log= LogFactory.getLog(PushMessageHook.class);

    @Autowired
    private InspectionNotifyService inspectionNotifyService;

    @JProfiler( jKey = "dmsworker.PushMessageHook.hook")
    @Override
    public int hook(InspectionTaskExecuteContext context) {
        for (CenConfirm cenConfirm:context.getCenConfirmList()) {
            InspectionMQBody inspectionMQBody = new InspectionMQBody();
            inspectionMQBody.setWaybillCode(null != cenConfirm.getWaybillCode() ? cenConfirm.getWaybillCode() : SerialRuleUtil.getWaybillCode(cenConfirm.getPackageBarcode()));
            inspectionMQBody.setInspectionSiteCode(cenConfirm.getCreateSiteCode());
            try {
                /**
                 * fix wtw 任务监控
                 */
                inspectionNotifyService.send(inspectionMQBody);
            } catch (Throwable throwable) {
                log.error("推送验货MQ异常", throwable);
            }
        }
        return 0;
    }
}
