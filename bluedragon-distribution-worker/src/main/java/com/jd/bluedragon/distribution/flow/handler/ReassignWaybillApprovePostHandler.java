package com.jd.bluedragon.distribution.flow.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.lsb.flow.handler.ApprovePostHandler;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/3 15:40
 * @Description: 返调度审批
 */
@Service("reassignWaybillApprovePostHandler")
public class ReassignWaybillApprovePostHandler implements ApprovePostHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReassignWaybillService reassignWaybillService;

    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.flow.handler.ReassignWaybillApprovePostHandler",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    public void postApprove(HistoryApprove historyApprove) throws Throwable {

        if(historyApprove == null
                // 系统不一致
                || !Objects.equals(historyApprove.getSystem(), Constants.SYSTEM_NAME)
                // 应用不一致
                || !Objects.equals(historyApprove.getAppName(),Constants.SYS_DMS)
                // 流程编码不一致
                || !Objects.equals(historyApprove.getFlowName(), FlowConstants.FLOW_CODE_REASSIGN_WAYBILL)){
            logger.warn("返调度流程审批结果!审批结果:{}", JsonHelper.toJson(historyApprove));
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("返调度审批工单号:{}的审批回调结果：{}", historyApprove.getProcessInstanceNo(), JsonHelper.toJson(historyApprove));
        }
        // 处理审批结果
        reassignWaybillService.dealApproveResult(historyApprove);
    }
}
