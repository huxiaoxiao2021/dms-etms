package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedReason;
import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.service.DirectDeliverySortCollectWaybillService;
import com.jd.bluedragon.external.gateway.service.JyDirectDeliverySortCollectGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 直送分拣揽收网关实现
 *
 * @author hujiping
 * @date 2024/3/4 3:21 PM
 */
@Service("jyDirectDeliverySortCollectGatewayService")
public class JyDirectDeliverySortCollectGatewayServiceImpl implements JyDirectDeliverySortCollectGatewayService {
    
    @Autowired
    private DirectDeliverySortCollectWaybillService directDeliverySortCollectWaybillService;

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyCollectGatewayService.queryTerminatedReasons",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<List<CollectTerminatedReason>> queryTerminatedReasons() {
        JdCResponse<List<CollectTerminatedReason>> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        jdCResponse.setData(directDeliverySortCollectWaybillService.queryTerminatedReasons());
        return jdCResponse;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyCollectGatewayService.terminateCollect",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<Void> terminateCollect(CollectTerminatedRequest request) {
        InvokeResult<Void> result = directDeliverySortCollectWaybillService.terminateCollect(request);
        return new JdCResponse<>(result.getCode(), result.getMessage());
    }
}
