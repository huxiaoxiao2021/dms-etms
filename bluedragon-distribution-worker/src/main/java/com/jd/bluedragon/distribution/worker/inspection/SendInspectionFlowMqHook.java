package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName SendInspectionFlowMqHook
 * @Description 验货发送操作流水hook
 * @Author wyd
 * @Date 2023/09/18 15:09
 **/
public class SendInspectionFlowMqHook extends AbstractTaskHook {

    @Autowired
    private JyOperateFlowService jyOperateFlowService;

    @Override
    @JProfiler(jKey = "dmsworker.SendInspectionFlowMqHook.hook", jAppName= Constants.UMP_APP_NAME_DMSWORKER, mState={JProEnum.TP, JProEnum.FunctionError})
    public int hook(InspectionTaskExecuteContext context) {

        if (CollectionUtils.isNotEmpty(context.getInspectionList())) {
            jyOperateFlowService.sendInspectOperateFlowData(context.getInspectionList(), OperateBizSubTypeEnum.INSPECTION);
    	}
        return 1;
    }
}
