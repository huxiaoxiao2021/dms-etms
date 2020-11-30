package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.inspection.constants.InspectionExeModeEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @ClassName InspectionTaskExeStrategyImpl
 * @Description
 * @Author wyh
 * @Date 2020/9/25 9:31
 **/
@Service
public class InspectionTaskExeStrategyImpl implements InspectionTaskExeStrategy {

    @Qualifier("inspectionWaybillTaskExecutor")
    @Autowired
    private InspectionTaskExecutor waybillTaskExecutor;

    @Qualifier("splitWaybillTaskExecutor")
    @Autowired
    private InspectionTaskExecutor splitWaybillTaskExecutor;

    @Qualifier("inspectionInitSplitTaskExecutor")
    @Autowired
    private InspectionTaskExecutor inspectionInitSplitTaskExecutor;

    @Autowired
    private InspectionService inspectionService;

    @Override
    @JProfiler(jKey = "DMSWEB.InspectionTaskExeStrategyImpl.decideExecutor", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    public InspectionTaskExecutor decideExecutor(InspectionRequest request) {

        InspectionExeModeEnum typeEnum = inspectionService.findInspectionExeMode(request);

        InspectionTaskExecutor targetExecutor = null;

        switch (typeEnum) {

            case NONE_SPLIT_MODE:
                targetExecutor = waybillTaskExecutor;
                break;

            case PACKAGE_PAGE_MODE:
                targetExecutor = splitWaybillTaskExecutor;
                break;

            case INIT_SPLIT_MODE:
                targetExecutor = inspectionInitSplitTaskExecutor;
                break;
        }

        if (null == targetExecutor) {
            throw new IllegalArgumentException("非法的验货任务执行模式");
        }

        return targetExecutor;
    }
}
