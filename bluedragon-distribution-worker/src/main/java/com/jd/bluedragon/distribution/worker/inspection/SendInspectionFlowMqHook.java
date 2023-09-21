package com.jd.bluedragon.distribution.worker.inspection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

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
    	
    	List<JyOperateFlowMqData> sortingFlowMqList = new ArrayList<>();
    	if(CollectionUtils.isNotEmpty(context.getInspectionList())) {
    		for(Inspection inspection : context.getInspectionList()) {
    	        JyOperateFlowMqData sortingFlowMq = BeanConverter.convertToJyOperateFlowMqData(inspection);
    	        sortingFlowMq.setOperateBizSubType(OperateBizSubTypeEnum.SORTING.getCode());
    	        sortingFlowMqList.add(sortingFlowMq);
    		}
            jyOperateFlowService.sendMqList(sortingFlowMqList);
    	}

        return 1;
    }
}
