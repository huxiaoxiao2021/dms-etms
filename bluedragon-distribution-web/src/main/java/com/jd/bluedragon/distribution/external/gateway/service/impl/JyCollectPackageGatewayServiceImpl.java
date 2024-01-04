package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyCollectPackageService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.service.JyCollectPackageGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_FAIL;
import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_ERROR;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_THIRD_ERROR_CODE;

@Slf4j
@UnifiedExceptionProcess
@Service
public class JyCollectPackageGatewayServiceImpl implements JyCollectPackageGatewayService {

    @Autowired
    JyCollectPackageService jyCollectPackageService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.collectScan", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<CollectPackageResp> collectScan(CollectPackageReq request) {
        try {
            if (BusinessUtil.isCollectionBag(request.getBarCode())) {
                BindCollectBagReq bindCollectBagReq = assembleBindCollectBagReq(request);
                return retJdCResponse(jyCollectPackageService.bindCollectBag(bindCollectBagReq));
            }
            return retJdCResponse(jyCollectPackageService.collectPackage(request));
        } catch (JyBizException e) {
            if (ObjectHelper.isNotNull(e.getCode())) {
                return new JdCResponse(e.getCode(), e.getMessage());
            }
            return new JdCResponse(CODE_ERROR, e.getMessage());
        }
    }

    private BindCollectBagReq assembleBindCollectBagReq(CollectPackageReq request) {
        BindCollectBagReq bindCollectBagReq = new BindCollectBagReq();
        bindCollectBagReq.setBoxCode(request.getBoxCode());
        bindCollectBagReq.setMaterialCode(request.getBarCode());
        bindCollectBagReq.setCurrentOperate(request.getCurrentOperate());
        bindCollectBagReq.setUser(request.getUser());
        bindCollectBagReq.setForceBindFlag(request.getSkipInterceptChain());
        return bindCollectBagReq;
    }

    @Override
    public JdCResponse<CollectPackageTaskResp> listCollectPackageTask(CollectPackageTaskReq request) {
        try {
            return retJdCResponse(jyCollectPackageService.listCollectPackageTask(request));
        } catch (Exception e) {
            log.error("集包任务列表查询异常{}", JsonHelper.toJson(request), e);
            return new JdCResponse<>(CODE_FAIL, "集包任务列表查询异常！");
        }
    }

    @Override
    public JdCResponse<CollectPackageTaskResp> searchPackageTask(SearchPackageTaskReq request) {
        try {
            return retJdCResponse(jyCollectPackageService.searchPackageTask(request));
        } catch (Exception e) {
            log.error("集包任务检索异常{}", JsonHelper.toJson(request), e);
            return new JdCResponse<>(CODE_FAIL, "集包任务检索异常！");
        }
    }

    @Override
    public JdCResponse<TaskDetailResp> queryTaskDetail(TaskDetailReq request) {
        try {
            return retJdCResponse(jyCollectPackageService.queryTaskDetail(request));
        } catch (Exception e) {
            log.error("查询集包任务信息异常{}", JsonHelper.toJson(request), e);
            return new JdCResponse<>(CODE_FAIL, "查询集包任务信息异常！");
        }
    }

    @Override
    public JdCResponse<SealingBoxResp> sealingBox(SealingBoxReq request) {
        try {
            return retJdCResponse(jyCollectPackageService.sealingBox(request));
        } catch (Exception e) {
            log.error("封箱完成异常：{}", JsonHelper.toJson(request), e);
            return new JdCResponse<>(CODE_FAIL, "封箱完成异常！");
        }
    }

    @Override
    public JdCResponse<BindCollectBagResp> bindCollectBag(BindCollectBagReq request) {
        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.cancelCollectPackage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request) {
        try {
            return retJdCResponse(jyCollectPackageService.cancelCollectPackage(request));
        } catch (JyBizException e) {
            if (ObjectHelper.isNotNull(e.getCode())) {
                return new JdCResponse(e.getCode(), e.getMessage());
            }
            return new JdCResponse(CODE_ERROR, e.getMessage());
        }
    }

    @Override
    public JdCResponse<StatisticsUnderTaskQueryResp> queryStatisticsUnderTask(StatisticsUnderTaskQueryReq request) {
        return retJdCResponse(jyCollectPackageService.queryStatisticsUnderTask(request));
    }

    @Override
    public JdCResponse<StatisticsUnderFlowQueryResp> queryStatisticsUnderFlow(StatisticsUnderFlowQueryReq request) {
        return retJdCResponse(jyCollectPackageService.queryStatisticsUnderFlow(request));
    }

    @Override
    public JdCResponse<MixFlowListResp> querySiteMixFlowList(MixFlowListReq request) {
        return retJdCResponse(jyCollectPackageService.querySiteMixFlowList(request));
    }

    @Override
    public JdCResponse<UpdateMixFlowListResp> updateTaskFlowList(UpdateMixFlowListReq request) {
        try {
            return retJdCResponse(jyCollectPackageService.updateTaskFlowList(request));
        } catch (Exception e) {
            return new JdCResponse<>(RESULT_THIRD_ERROR_CODE, "更新集包任务异常，请联系分拣小秘！");
        }
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    public static void main(String[] args) {
        StatisticsUnderFlowQueryResp data = new StatisticsUnderFlowQueryResp();
        List<CollectPackageDto> collectPackageDtoList = new ArrayList<>();
        data.setCollectPackageDtoList(collectPackageDtoList);

        CollectPackageDto collectPackageDto = new CollectPackageDto();
        collectPackageDto.setPackageCode("包裹号1");
        collectPackageDtoList.add(collectPackageDto);

        CollectPackageDto collectPackageDto2 = new CollectPackageDto();
        collectPackageDto2.setPackageCode("包裹号2");
        collectPackageDtoList.add(collectPackageDto2);


        JdCResponse jdCResponse = new JdCResponse(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(data);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }
}
