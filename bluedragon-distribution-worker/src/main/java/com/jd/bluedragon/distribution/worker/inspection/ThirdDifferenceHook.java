package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangtingwei on 2017/1/17.
 */
public class ThirdDifferenceHook implements TaskHook<InspectionTaskExecuteContext> {

    @Autowired
    private InspectionService inspectionService;

    @Override
    @JProfiler( jKey = "dmsworker.ThirdDifferenceHook.hook")
    public int hook(InspectionTaskExecuteContext context) {
        for (Inspection inspection:context.getInspectionList()){
            if (inspection.getInspectionType().equals(Inspection.BUSSINESS_TYPE_THIRD_PARTY)) {
                inspectionService.thirdPartyWorker(inspection);
            }
        }
        return 0;
    }
}
