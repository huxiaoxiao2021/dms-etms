package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName AbstractTaskHook
 * @Description
 * @Author wyh
 * @Date 2020/11/18 15:37
 **/
public abstract class AbstractTaskHook implements TaskHook<InspectionTaskExecuteContext> {

    @Autowired
    private InspectionService inspectionService;

    @Override
    public boolean escape(InspectionTaskExecuteContext context) {
        return false;
    }

    protected boolean siteEnableInspectionAgg(InspectionTaskExecuteContext context) {
        List<Inspection> inspectionList = context.getInspectionList();
        if (CollectionUtils.isNotEmpty(inspectionList)) {
            Integer siteCode = inspectionList.get(0).getCreateSiteCode();
            return inspectionService.siteEnableInspectionAgg(siteCode);
        }
        return false;
    }
}
