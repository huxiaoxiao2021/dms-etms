package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyCollectPackageService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.service.JyCollectPackageGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_FAIL;

@Slf4j
@UnifiedExceptionProcess
@Service
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
        try {
            return retJdCResponse(jyCollectPackageService.listCollectPackageTask(request));
        }catch (Exception e) {
            log.error("集包任务列表查询异常{}", JsonHelper.toJson(request),e);
            return new JdCResponse<>(CODE_FAIL,"集包任务列表查询异常！");
        }
    }

    @Override
    public JdCResponse<CollectPackageTaskResp> searchPackageTask(SearchPackageTaskReq request) {
        try {
            return retJdCResponse(jyCollectPackageService.searchPackageTask(request));
        }catch (Exception e) {
            log.error("集包任务检索异常{}", JsonHelper.toJson(request),e);
            return new JdCResponse<>(CODE_FAIL,"集包任务检索异常！");
        }
    }

    @Override
    public JdCResponse<TaskDetailResp> queryTaskDetail(TaskDetailReq request) {
        try{
            return retJdCResponse(jyCollectPackageService.queryTaskDetail(request));
        }catch (Exception e) {
            log.error("查询集包任务信息异常{}", JsonHelper.toJson(request),e);
            return new JdCResponse<>(CODE_FAIL,"查询集包任务信息异常！");
        }
    }

    @Override
    public JdCResponse<SealingBoxResp> sealingBox(SealingBoxReq request) {
        try{
            return retJdCResponse(jyCollectPackageService.sealingBox(request));
        }catch (Exception e) {
            log.error("封箱完成异常：{}", JsonHelper.toJson(request),e);
            return new JdCResponse<>(CODE_FAIL,"封箱完成异常！");
        }
    }

    @Override
    public JdCResponse<BindCollectBagResp> bindCollectBag(BindCollectBagReq request) {
        return null;
    }

    @Override
    public JdCResponse<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request) {
        return null;
    }

    @Override
    public JdCResponse<StatisticsUnderTaskQueryResp> queryStatisticsUnderTask(StatisticsUnderTaskQueryReq request) {
        return null;
    }

    @Override
    public JdCResponse<StatisticsUnderFlowQueryResp> queryStatisticsUnderFlow(StatisticsUnderFlowQueryReq request) {
        return null;
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
                invokeResult.getData());
    }
}
