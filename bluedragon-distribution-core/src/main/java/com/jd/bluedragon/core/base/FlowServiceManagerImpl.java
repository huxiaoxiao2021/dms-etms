package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.lsb.flow.condition.ApproveQueryCondition;
import com.jd.lsb.flow.domain.Approve;
import com.jd.lsb.flow.domain.ApproveRequestOrder;
import com.jd.lsb.flow.exception.FlowException;
import com.jd.lsb.flow.service.FlowService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程处理包装类
 *
 * @author hujiping
 * @date 2021/4/21 11:34 上午
 */
@Service("flowServiceManager")
public class FlowServiceManagerImpl implements FlowServiceManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FlowService flowService;

    @Override
    public String startFlow(Map<String,Object> oaMap,Map<String,Object> businessMap,Map<String,Object> flowControlMap,
                            String name, String operateErp, String businessNoKey) throws FlowException {
        CallerInfo info
                = Profiler.registerInfo("DMS.BASE.FlowServiceManagerImpl.startFlow", Constants.UMP_APP_NAME_DMSWEB,false, true);;
        try {
            Map<String, Object> dataMap = new HashMap<>();
            // 设置OA基础数据
            dataMap.put(FlowConstants.FLOW_DATA_MAP_KEY_OA, oaMap);
            // 设置业务数据
            dataMap.put(FlowConstants.FLOW_DATA_MAP_KEY_BUSINESS_DATA, businessMap);
            // 设置流程分支数据
            dataMap.put(FlowConstants.FLOW_DATA_MAP_KEY_FLOW_CONTROL, flowControlMap);

            return flowService.startFlow(Constants.SYSTEM_NAME, Constants.SYS_DMS, name, FlowConstants.FLOW_VERSION, operateErp, dataMap, businessNoKey);
        }catch (Exception e){
            logger.error("提交审批单异常!",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @Override
    public ApproveRequestOrder getRequestOrder(String processInstanceNo) throws FlowException {
        CallerInfo info
                = Profiler.registerInfo("DMS.BASE.FlowServiceManagerImpl.getRequestOrder", Constants.UMP_APP_NAME_DMSWEB,false, true);;
        try {
            return flowService.getRequestOrder(processInstanceNo);
        }catch (Exception e){
            logger.error("提交审批单异常!",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;

    }

    @Override
    public List<Approve> getAllActiveApproveTaskList(ApproveQueryCondition condition) throws FlowException {
        CallerInfo info
                = Profiler.registerInfo("DMS.BASE.FlowServiceManagerImpl.getAllActiveApproveTaskList", Constants.UMP_APP_NAME_DMSWEB,false, true);;
        try {
            return flowService.getAllActiveApproveTaskList(condition);
        }catch (Exception e){
            logger.error("提交审批单异常!",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @JProfiler(jKey = "DMS.BASE.FlowServiceManagerImpl.cancelRequestOrder",mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public void cancelRequestOrder(String processInstanceNo, String applicant) throws FlowException {
        CallerInfo info
                = Profiler.registerInfo("DMS.BASE.FlowServiceManagerImpl.cancelRequestOrder", Constants.UMP_APP_NAME_DMSWEB,false, true);;
        try {
            flowService.cancelRequestOrder(processInstanceNo,applicant);
        }catch (Exception e){
            logger.error("提交审批单异常!",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
