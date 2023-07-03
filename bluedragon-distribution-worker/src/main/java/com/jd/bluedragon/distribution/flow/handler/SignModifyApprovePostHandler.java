package com.jd.bluedragon.distribution.flow.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.core.base.PrintHandoverListManager;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.station.service.UserSignRecordFlowService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverLitQueryCondition;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.ApproveRequestOrder;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.lsb.flow.handler.ApprovePostHandler;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 签到修改-审批结果监听处理器
 *
 * @author wuyoude
 * @date 2023/03/10 7:44 下午
 */
@Service("signModifyApprovePostHandler")
public class SignModifyApprovePostHandler implements ApprovePostHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("userSignRecordFlowService")
	private UserSignRecordFlowService userSignRecordFlowService;

    @JProfiler(jKey = "com.jd.bluedragon.distribution.flow.handler.SignModifyApprovePostHandler",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    @Override
    public void postApprove(HistoryApprove historyApprove) throws Throwable {
        if(logger.isInfoEnabled()){
            logger.info("审批回调结果：{}", JsonHelper.toJson(historyApprove));
        }
        if(historyApprove == null
                // 系统不一致
                || !Objects.equals(historyApprove.getSystem(),Constants.SYSTEM_NAME)
                // 应用不一致
                || !Objects.equals(historyApprove.getAppName(),Constants.SYS_DMS)
                // 流程编码不一致
                || !Objects.equals(historyApprove.getFlowName(), FlowConstants.FLOW_CODE_SIGN_MODIFY)){
            logger.warn("签到数据修改流程审批结果!审批结果【{}】", JsonHelper.toJson(historyApprove));
            return;
        }
        userSignRecordFlowService.dealFlowResult(historyApprove);
    }
}
