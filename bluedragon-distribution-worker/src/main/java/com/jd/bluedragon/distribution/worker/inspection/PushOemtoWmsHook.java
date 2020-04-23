package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * OEM推送WMS
 * Created by wangtingwei on 2017/1/17.
 */
public class PushOemtoWmsHook implements TaskHook<InspectionTaskExecuteContext> {

    private static final Logger log = LoggerFactory.getLogger(PushOemtoWmsHook.class);

    @Autowired
    private InspectionService inspectionService;


    @Override
    @JProfiler( jKey = "dmsworker.PushOemtoWmsHook.hook")
    public int hook(InspectionTaskExecuteContext context) {
        for (Inspection inspection:context.getInspectionList()) {
            if (Constants.BUSSINESS_TYPE_OEM == inspection.getInspectionType()) {
                // OEM同步wms
                try {
                    inspectionService.pushOEMToWMS(inspection);//FIXME:51号库推送，需要检查是否在用
                } catch (Exception e) {
                   // e.printStackTrace();
                    log.error(" 验货 inspectionCore调用OEM服务异常:{}", JsonHelper.toJson(inspection), e);
                }
            }
        }
        return 0;
    }
}
