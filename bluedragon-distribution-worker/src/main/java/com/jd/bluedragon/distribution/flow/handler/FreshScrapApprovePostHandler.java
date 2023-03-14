package com.jd.bluedragon.distribution.flow.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
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
 * 生鲜报废审批回传处理handler
 *
 * @author hujiping
 * @date 2023/3/9 3:12 PM
 */
@Service("freshScrapApprovePostHandler")
public class FreshScrapApprovePostHandler implements ApprovePostHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private JyScrappedExceptionService jyScrappedExceptionService;

    @JProfiler(jKey = "com.jd.bluedragon.distribution.flow.handler.FreshScrapApprovePostHandler",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    @Override
    public void postApprove(HistoryApprove historyApprove) throws Throwable {
        if(historyApprove == null
                // 系统不一致
                || !Objects.equals(historyApprove.getSystem(), Constants.SYSTEM_NAME)
                // 应用不一致
                || !Objects.equals(historyApprove.getAppName(),Constants.SYS_DMS)
                // 流程编码不一致
                || !Objects.equals(historyApprove.getFlowName(), FlowConstants.FLOW_CODE_FRESH_SCRAP)){
            logger.warn("非生鲜报废流程审批结果!审批结果:{}", JsonHelper.toJson(historyApprove));
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("生鲜报废审批工单号:{}的审批回调结果：{}", historyApprove.getProcessInstanceNo(), JsonHelper.toJson(historyApprove));
        }
        // 处理审批结果
        jyScrappedExceptionService.dealApproveResult(historyApprove);
        
    }
}
