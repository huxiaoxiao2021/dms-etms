package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyCollectPackageService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.service.JyCollectPackageGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@UnifiedExceptionProcess
public class JyCollectPackageGatewayServiceImpl implements JyCollectPackageGatewayService {

    @Autowired
    JyCollectPackageService jyCollectPackageService;
    @Override
    public JdCResponse<CollectPackageResp> collectScan(CollectPackageReq request) {
        if (BusinessUtil.isCollectionBag(request.getBarCode())){
            BindCollectBagReq bindCollectBagReq =assembleBindCollectBagReq(request);
            return retJdCResponse(jyCollectPackageService.bindCollectBag(bindCollectBagReq));
        }
        return retJdCResponse(jyCollectPackageService.collectPackage(request));
    }

    private BindCollectBagReq assembleBindCollectBagReq(CollectPackageReq request) {
        BindCollectBagReq bindCollectBagReq =new BindCollectBagReq();
        bindCollectBagReq.setBoxCode(request.getBoxCode());
        bindCollectBagReq.setMaterialCode(request.getBarCode());
        return bindCollectBagReq;
    }

    @Override
    public JdCResponse<CollectPackageTaskResp> listCollectPackageTask(CollectPackageTaskReq request) {
        return null;
    }

    @Override
    public JdCResponse<CollectPackageTaskResp> searchPackageTask(SearchPackageTaskReq request) {
        return null;
    }

    @Override
    public JdCResponse<TaskDetailResp> queryTaskDetail(TaskDetailReq request) {
        return null;
    }

    @Override
    public JdCResponse<SealingBoxResp> sealingBox(SealingBoxReq request) {
        return null;
    }

    @Override
    public JdCResponse<BindCollectBagResp> bindCollectBag(BindCollectBagReq request) {
        return null;
    }

    @Override
    public JdCResponse<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request) {
        return null;
    }
    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
                invokeResult.getData());
    }
}
